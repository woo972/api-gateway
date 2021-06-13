package com.wowls.apigateway.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CustomFilter extends AbstractGatewayFilterFactory<CustomFilter.Config> {
    public CustomFilter(){
        super(Config.class);
    }

    @Getter
    public static class Config {
        // put the configuration properties
    }

    @Override
    public GatewayFilter apply(Config config) {
        /**
         * Process the Web request and (optionally) delegate to the next {@code WebFilter}
         * through the given {@link GatewayFilterChain}.
         * @param exchange the current server exchange
         * @param chain provides a way to delegate to the next filter
         * @return {@code Mono<Void>} to indicate when request processing is complete
         */
//        Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain);

//        Contract for an HTTP request-response interaction. Provides access to the HTTP request and response and also exposes additional server-side processing related properties and features such as request attributes.
//        Contract to allow a WebFilter to delegate to the next in the chain.
        return (exchange, chain) -> {

            ServerHttpRequest request = exchange.getRequest();

            request.mutate()
                    .header("userservice-request", "new-header-values")
                    .build()
                    ;
            log.info("custom pre filter request id : {}", request.getId());



            // then : element를 소스에서 제거
            ServerHttpResponse response = exchange.getResponse();
            // 변경된 request를 다음 filter에 전달한다
            return chain.filter(exchange.mutate().request(request).build()).then(Mono.fromRunnable(() ->{ // 여기 왜 runnerble?
                log.info("custom post filter response code : {}", response.getStatusCode());
            }));
        };
    }

}
