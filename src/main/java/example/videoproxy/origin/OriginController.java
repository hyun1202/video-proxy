package example.videoproxy.origin;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/origins")
public class OriginController {

    private final OriginService originService;

    public OriginController(OriginService originService) {
        this.originService = originService;
    }

    @GetMapping
    public ResponseEntity<List<String>> getOrigins() {
        List<String> origins = originService.getOrigins();
        return ResponseEntity.ok(origins);
    }

    @PostMapping
    public ResponseEntity<Void> add(@RequestBody OriginRequest req) {
        originService.add(req.origin());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> removeOrigin(@RequestBody OriginRequest req) {
        originService.remove(req.origin());
        return ResponseEntity.noContent().build();
    }
}
