package example.videoproxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
@RequestMapping("/video")
public class VideoController {
    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping(produces = "video/mp4")
    public ResponseEntity<Flux<DataBuffer>> streamVideo(@RequestParam("url") String url) {
        Flux<DataBuffer> res = videoService.streamVideo(url);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("video/mp4"))
                .cacheControl(CacheControl.maxAge(Duration.ofDays(14)).cachePublic())
                .body(res);
    }
}
