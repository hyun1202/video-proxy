package example.videoproxy;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public class VideoService {
    private final WebClient webClient;

    public VideoService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Flux<DataBuffer> streamVideo(String url) {
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
