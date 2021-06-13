package com.wowls.apigateway.configuration;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class FilterConfig {
//    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/userservice/**")
                            .filters(f -> f.addRequestHeader("userservice-request","user service request")
                                            .addResponseHeader("userservice-response","user service response"))
                            .uri("http://localhost:9721"))
//                .route(r -> r.path("/userservice")
//                        .filters(f -> f.addRequestHeader("userservice-request","user service request")
//                                .addResponseHeader("userservice-response","user service response"))
//                        .uri("http://localhost:19721"))
                .build();
    }

}
