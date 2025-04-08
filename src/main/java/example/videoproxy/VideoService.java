package example.videoproxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class VideoService {
    private final WebClient webClient;

    public VideoService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Flux<DataBuffer> streamVideo(String url) {
        log.debug("request url= {}", url);
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToFlux(DataBuffer.class)
                .doOnError(throwable -> {
                    // 에러 처리 (예: URL이 잘못된 경우)
                    System.err.println("Error fetching video: " + throwable.getMessage());
                });
    }

}
