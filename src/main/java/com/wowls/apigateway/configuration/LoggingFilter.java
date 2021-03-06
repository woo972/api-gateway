package com.wowls.apigateway.configuration;

import com.netflix.discovery.converters.Auto;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {
    public LoggingFilter() {
        super(Config.class);
    }

    @Autowired
    private Environment env;


    @Data
    public static class Config {
        private String baseMessage;
        private boolean preFilter;
        private boolean postFilter;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new OrderedGatewayFilter((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            log.info("token:{}", env.getProperty("token.secret"));
            if (config.preFilter) {
                log.info("pre logging filter : request id : {}", request.getId());
            }
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("post logging filter : status code : {}", response.getStatusCode());
            }));
        }, Ordered.LOWEST_PRECEDENCE);
    }

}
