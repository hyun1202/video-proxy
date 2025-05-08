package example.videoproxy.origin;

import example.videoproxy.filter.AllowOrigin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
public class OriginService {
    private final AllowOrigin allowOrigin;
    public final static String fileName = "origin";

    private final String filePath;

    public OriginService(AllowOrigin allowOrigin,
                         @Value("${data.origin.path}") String filePath) {
        this.allowOrigin = allowOrigin;
        this.filePath = filePath;
    }

    public void add(String origin) {
        if (origin == null) {
            throw new IllegalArgumentException("데이터가 없어 추가할 수 없습니다.");
        }

        if (!allowOrigin.add(origin)) {
            throw new IllegalArgumentException("이미 추가된 주소입니다. " + origin);
        }

        write(List.of(origin), true);
    }

    public void remove(String origin) {
        allowOrigin.remove(origin);
        write(getOrigins(), false);
    }

    public boolean contains(String origin) {
        return allowOrigin.contains(origin);
    }

    public List<String> getOrigins() {
        return allowOrigin.getAll();
    }

    private void write(List<String> origins, boolean append) {
        String filePath = this.filePath + "/" + fileName;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, append))) {
            for (String origin : origins) {
                writer.write(origin + "\n");
            }
        } catch (IOException e) {
            log.info("origin 파일 생성 중 에러가 발생했습니다.", e);
        }
    }

    public boolean read() {
        Path path = Paths.get(filePath + "/" + fileName);
        log.info("read path: " + filePath);
        try (Stream<String> lines = Files.lines(path)) {
            lines.forEach(allowOrigin::add);
            return true;
        } catch (IOException e) {
            log.info("origin 파일을 읽던 중 에러가 발생했습니다.", e);
            return false;
        }
    }
}
