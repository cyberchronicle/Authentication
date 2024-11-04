package org.cyberchronicle.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

@Configuration
public class Config {
    @Bean
    public RouterFunction<ServerResponse> getRoute(AuthFilter authFilter) {
        return route()
                .route(path("/get"), http("https://httpbin.org"))
                .filter(authFilter)
                .build();
    }

    @Bean
    public AuthService authService(@Value("${token.sign.key}") String key) {
        return new AuthService(key);
    }

    @Bean
    public AuthFilter authFilter(AuthService authService) {
        return new AuthFilter(authService);
    }
}
