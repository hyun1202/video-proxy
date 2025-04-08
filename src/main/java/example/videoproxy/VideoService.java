package example.videoproxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public class VideoService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final WebClient webClient;

    public VideoService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Flux<DataBuffer> streamVideo(String url) {
        logger.debug("request url= {}", url);
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
