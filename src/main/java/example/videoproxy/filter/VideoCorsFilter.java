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

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String origin = exchange.getRequest().getHeaders().getOrigin();
        origin = removeEndSeparation(origin);

        if (origin == null) {
            return getForbiddenResponse(exchange, origin);
        }

        log.info("origin = {}", origin);

        if (!allowOrigins.contains(origin)) {
            return getForbiddenResponse(exchange, origin);
        }

        exchange.getResponse().getHeaders().add("Access-Control-Allow-Origin", origin);
        return chain.filter(exchange);
    }

    private static Mono<Void> getForbiddenResponse(ServerWebExchange exchange, String origin) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        exchange.getResponse().getHeaders().add("X-Reason", "Invalid or missing Origin: " + (origin != null ? origin : "null"));
        return exchange.getResponse().setComplete();
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
