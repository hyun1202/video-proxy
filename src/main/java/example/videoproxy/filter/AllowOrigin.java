package example.videoproxy.filter;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class AllowOrigin {
    private final Set<String> origins;

    public AllowOrigin() {
        this.origins = new HashSet<>();
    }

    public boolean add(String origin) {
        if (contains(origin)) {
            return false;
        }
        origins.add(origin);
        return true;
    }

    public void remove(String origin) {
        origins.remove(origin);
    }

    public boolean contains(String origin) {
        return origins.contains(origin);
    }

    public List<String> getAll() {
        return origins.stream().toList();
    }
}
