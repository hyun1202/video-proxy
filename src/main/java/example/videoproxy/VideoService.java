package example.videoproxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
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

    public Flux<ResponseWithHeaders> streamVideo(String url) {
        log.debug("request url= {}", url);
        return webClient.get()
                .uri(url)
                .exchangeToFlux(response -> {
                    HttpHeaders headers = response.headers().asHttpHeaders();
                    log.debug("Response headers: {}", headers);

                    return response.bodyToFlux(DataBuffer.class)
                            .map(dataBuffer -> new ResponseWithHeaders(headers, dataBuffer));
                })
                .doOnError(throwable -> {
                    log.error("Error fetching video: {}", throwable.getMessage());
                });
    }

}
