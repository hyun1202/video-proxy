package example.videoproxy;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class VideoMetaData {
    private final ConcurrentHashMap<String, Long> map;

    public VideoMetaData() {
        this.map = new ConcurrentHashMap<>();
    }

    public Long get(String key) {
        return map.get(key);
    }

    public Long get(String key, Long length) {
        return map.computeIfAbsent(key, (k) -> length);
    }

    public void remove(String key) {
        map.remove(key);
    }

    public boolean contains(String key) {
        return map.containsKey(key);
    }

    public void update(String key, Long length) {
        map.put(key, length);
    }

    public void printAll() {
        for (Map.Entry<String, Long> m : map.entrySet()) {
            String key = m.getKey();
            Long value = m.getValue();
            System.out.println("key: " + key + ", value: " + value);
        }
    }
}
