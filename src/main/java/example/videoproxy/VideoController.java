package example.videoproxy;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@RestController
@RequestMapping("/video")
public class VideoController {
    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping(produces = "video/mp4")
    public Mono<ResponseEntity<Flux<DataBuffer>>> streamVideo(@RequestParam("url") String url,
                                                              @RequestParam(value = "new", defaultValue = "false") Boolean isNew) {
        if (isNew) {
            videoService.clearContentLength(url);
        }
        Flux<ResponseWithHeaders> res = videoService.streamVideo(url);
        Flux<DataBuffer> data = res.map(ResponseWithHeaders::getDataBuffer);
        return res.next()
                .map(first -> {
                    ResponseEntity.BodyBuilder builder = getBodyBuilder();
                    builder.headers(first.getHeaders());
                    return builder.body(data);
                })
                .onErrorResume(error -> Mono.empty());
    }

    private ResponseEntity.BodyBuilder getBodyBuilder() {
        ResponseEntity.BodyBuilder builder = ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("video/mp4"))
                .cacheControl(CacheControl.maxAge(Duration.ofDays(14)).cachePublic());
        return builder;
    }
}
