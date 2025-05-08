package example.videoproxy.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Order(-1)
@Slf4j
@RequiredArgsConstructor
public class VideoCorsFilter implements WebFilter {
    private final AllowOrigin allowOrigins;

//    private final List<String> allowOrigins = List.of(
//            "https://ecuwdemo2698.cafe24.com",
//            "https://ecudemo357864.cafe24.com"
//    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String origin = exchange.getRequest().getHeaders().getOrigin();
        origin = removeEndSeparation(origin);
        log.info("origin = {}", origin);
        if (origin == null || !allowOrigins.contains(origin)) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            exchange.getResponse().getHeaders().add("X-Reason", "Invalid or missing Origin: " + (origin != null ? origin : "null"));
            return exchange.getResponse().setComplete();
        }

        exchange.getResponse().getHeaders().add("Access-Control-Allow-Origin", origin);
        return chain.filter(exchange);
    }

    private String removeEndSeparation(String origin) {
        if (origin == null) {
            return null;
        }
        if (origin.endsWith("/")) {
            return origin.substring(0, origin.length()-1);
        }
        return origin;
    }
}
