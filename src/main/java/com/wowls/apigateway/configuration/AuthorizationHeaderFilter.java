package com.wowls.apigateway.configuration;

import io.jsonwebtoken.Jwts;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    private Environment environment;

    public AuthorizationHeaderFilter(Environment environment)
    {
        super(Config.class);
        this.environment = environment;
    }

    @Getter
    public static class Config {
        // put the configuration properties
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            ServerHttpRequest request = exchange.getRequest();

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "no auth header", HttpStatus.UNAUTHORIZED);
            }

            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String jwt = authorizationHeader.replace("Bearer", "");

            if (isValidJwt(jwt)) {
                return onError(exchange, "JWT is not valid", HttpStatus.UNAUTHORIZED);
            }

            return chain.filter(exchange);
        };
    }

    private boolean isValidJwt(String jwt) {
        boolean isValid = true;
        String subject = null;

        try {
            subject = Jwts.parser().setSigningKey(environment.getProperty("token.secret"))
                    .parseClaimsJws(jwt).getBody()
                    .getSubject();
        } catch (Exception e) {
            isValid = false;
        }

        if (subject == null | subject.isEmpty()) {
            isValid = false;
        }

        return isValid;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus http) {
        ServerHttpResponse serverHttpResponse = exchange.getResponse();
        serverHttpResponse.setStatusCode(http);

        log.error(err);
        return serverHttpResponse.setComplete();
    }


}

