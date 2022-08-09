package com.jeorgius.jbackend.filter;

import com.jeorgius.jbackend.config.GatewayConfig;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class FilterWithoutJwt implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//        ServerHttpRequest request = exchange.getRequest();
//        URI uri = request.getURI();
//        PathMatcher pathMatcher = new AntPathMatcher();
//
//        String[] ignoreUrls = GatewayConfig.AUTH_WHITELIST;
//        for (String ignoreUrl : ignoreUrls) {
//            if (pathMatcher.match(ignoreUrl, uri.getPath())) {
////                request = exchange.getRequest().mutate().header("Authorization", "").build();
////                exchange = exchange.mutate().request(request).build();
//                return chain.filter(exchange);
//            }
//        }
        return chain.filter(exchange);
    }
}
