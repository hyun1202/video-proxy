package example.videoproxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class VideoService {
    private final VideoMetaData videoMetaData;
    private final WebClient webClient;

    public VideoService(VideoMetaData videoMetaData, WebClient.Builder webClientBuilder) {
        this.videoMetaData = videoMetaData;
        this.webClient = webClientBuilder.build();
    }

    public Flux<ResponseWithHeaders> streamVideo(String url) {
        log.debug("request url= {}", url);
        Long contentLength = getContentLength(url);
        if (contentLength < 0L) {
            return Flux.empty();
        }
        return getDataBuffer(url, contentLength);
    }

    private Flux<ResponseWithHeaders> getDataBuffer(String url, Long contentLength) {
        return webClient.get()
                .uri(url)
                .exchangeToFlux(response -> {
                    HttpHeaders originHeaders = response.headers().asHttpHeaders();

                    // 새로운 HttpHeaders 생성
                    HttpHeaders headers = new HttpHeaders();

                    originHeaders.forEach((key, value) -> headers.add(key, value.getFirst()));
                    // Content-Length가 없다면 추가
                    if (!headers.isEmpty() && !headers.containsKey("Content-Length")) {
                        headers.add("Content-Length", String.valueOf(contentLength));
                    }
                    log.debug("Response headers: {}", headers);

                    return response.bodyToFlux(DataBuffer.class)
                            .map(dataBuffer -> new ResponseWithHeaders(headers, dataBuffer));
                })
                .doOnError(e -> {
                    log.error("Error fetching video: {}", e.getMessage(), e);
                });
    }

    /**
     * 파일 사이즈를 가져온다. 없다면 데이터를 스트리밍해서 캐싱한다.
     * 반드시 한 번은 제대로된 파일을 가져오지 못한다.
     *
     * @param url 파일 url
     * @return 파일 사이즈
     */
    public Long getContentLength(String url) {
        if (!videoMetaData.contains(url)) {
            AtomicLong contentLength = new AtomicLong(-1);
            getDataBuffer(url, contentLength.get())
                    .map(d -> calcDataLength(d, contentLength))
                    .doOnComplete(() -> writeContentLength(url, contentLength))
                    .subscribeOn(Schedulers.boundedElastic())
                    .subscribe();
            return contentLength.get();
        }

        return videoMetaData.get(url);
    }

    private void writeContentLength(String url, AtomicLong contentLength) {
        videoMetaData.get(url, contentLength.get());
        log.info("'{}' content length write success! file size: {}", url, contentLength);
    }

    public DataBuffer calcDataLength(ResponseWithHeaders response, AtomicLong contentLength) {
        // DataBuffer를 처리하며 크기 누적
        DataBuffer dataBuffer = response.getDataBuffer();
        contentLength.addAndGet(dataBuffer.readableByteCount());
        return dataBuffer;
    }

}
