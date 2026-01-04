package com.airflights.gateway.security;

import java.util.stream.Collectors;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Order(9)
public class AuthHeaderWebFilter implements WebFilter {

    public static final String HEADER_USER = "X-Auth-User";
    public static final String HEADER_EMAIL = "X-Auth-Email";
    public static final String HEADER_ROLES = "X-Auth-Roles";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .map(context -> context.getAuthentication())
                .defaultIfEmpty(null)
                .flatMap(authentication -> applyHeaders(exchange, chain, authentication));
    }

    private Mono<Void> applyHeaders(
            ServerWebExchange exchange,
            WebFilterChain chain,
            Authentication authentication
    ) {
        ServerWebExchange mutated = exchange.mutate()
                .request(builder -> builder.headers(headers -> {
                    headers.remove(HEADER_USER);
                    headers.remove(HEADER_EMAIL);
                    headers.remove(HEADER_ROLES);
                    if (authentication != null && authentication.isAuthenticated()) {
                        String roles = authentication.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.joining(","));
                        headers.add(HEADER_ROLES, roles);
                        Object principal = authentication.getPrincipal();
                        if (principal instanceof AuthenticatedUser user) {
                            headers.add(HEADER_USER, user.username());
                            if (user.email() != null) {
                                headers.add(HEADER_EMAIL, user.email());
                            }
                        } else if (principal instanceof String user) {
                            headers.add(HEADER_USER, user);
                        }
                    }
                }))
                .build();
        return chain.filter(mutated);
    }
}
