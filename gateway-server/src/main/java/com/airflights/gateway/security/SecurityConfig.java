package com.airflights.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity http,
            JwtAuthenticationWebFilter jwtAuthenticationWebFilter
    ) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/auth/**").permitAll()
                        .pathMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .pathMatchers("/docs/**").permitAll()
                        .pathMatchers("/actuator/health").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/users/**").hasRole("SUPERVISOR")
                        .pathMatchers("/api/users/**").authenticated()
                        .pathMatchers(HttpMethod.GET, "/api/flights/**")
                        .hasAnyRole("PASSENGER", "AIRLINE_COMPANY", "GOVERNMENT", "AIRPORT_MANAGER", "AIRPORT_ASSISTANCE", "SUPERVISOR", "USER")
                        .pathMatchers(HttpMethod.POST, "/api/flights/**")
                        .hasAnyRole("AIRLINE_COMPANY", "SUPERVISOR")
                        .pathMatchers(HttpMethod.PATCH, "/api/flights/*/approve")
                        .hasAnyRole("AIRPORT_MANAGER", "SUPERVISOR")
                        .pathMatchers(HttpMethod.PATCH, "/api/flights/*/depart")
                        .hasAnyRole("AIRPORT_MANAGER", "SUPERVISOR")
                        .pathMatchers(HttpMethod.PATCH, "/api/flights/*/arrive")
                        .hasAnyRole("AIRPORT_MANAGER", "SUPERVISOR")
                        .pathMatchers(HttpMethod.PUT, "/api/flights/**")
                        .hasAnyRole("AIRLINE_COMPANY", "SUPERVISOR")
                        .pathMatchers(HttpMethod.PATCH, "/api/flights/**")
                        .hasAnyRole("GOVERNMENT", "SUPERVISOR")
                        .pathMatchers(HttpMethod.DELETE, "/api/flights/**")
                        .hasAnyRole("AIRLINE_COMPANY", "SUPERVISOR")

                        .pathMatchers(HttpMethod.GET, "/api/bookings/**")
                        .hasAnyRole("PASSENGER", "SUPERVISOR", "USER")
                        .pathMatchers(HttpMethod.POST, "/api/bookings/**")
                        .hasAnyRole("PASSENGER", "SUPERVISOR")
                        .pathMatchers(HttpMethod.DELETE, "/api/bookings/**")
                        .hasAnyRole("PASSENGER", "SUPERVISOR")

                        .pathMatchers(HttpMethod.GET, "/api/passengers/**")
                        .hasAnyRole("PASSENGER", "SUPERVISOR", "GOVERNMENT", "USER")
                        .pathMatchers(HttpMethod.POST, "/api/passengers/**")
                        .hasAnyRole("PASSENGER", "SUPERVISOR", "USER")
                        .pathMatchers(HttpMethod.PUT, "/api/passengers/**")
                        .hasAnyRole("PASSENGER", "SUPERVISOR", "USER")
                        .pathMatchers(HttpMethod.DELETE, "/api/passengers/**").hasRole("SUPERVISOR")

                        .pathMatchers(HttpMethod.GET, "/api/airports/**")
                        .hasAnyRole("PASSENGER", "AIRLINE_COMPANY", "GOVERNMENT", "AIRPORT_MANAGER", "AIRPORT_ASSISTANCE", "SUPERVISOR", "USER")
                        .pathMatchers(HttpMethod.POST, "/api/airports/**")
                        .hasAnyRole("GOVERNMENT", "SUPERVISOR")
                        .pathMatchers(HttpMethod.PUT, "/api/airports/**")
                        .hasAnyRole("GOVERNMENT", "SUPERVISOR")
                        .pathMatchers(HttpMethod.DELETE, "/api/airports/**")
                        .hasAnyRole("GOVERNMENT", "SUPERVISOR")

                        .pathMatchers("/api/airport-managers/**").hasAnyRole("GOVERNMENT", "SUPERVISOR")

                        .pathMatchers(HttpMethod.GET, "/api/restricted-zones/**")
                        .hasAnyRole("GOVERNMENT", "AIRPORT_MANAGER", "AIRPORT_ASSISTANCE", "SUPERVISOR")
                        .pathMatchers(HttpMethod.POST, "/api/restricted-zones/**")
                        .hasAnyRole("GOVERNMENT", "SUPERVISOR")
                        .pathMatchers(HttpMethod.DELETE, "/api/restricted-zones/**")
                        .hasAnyRole("GOVERNMENT", "SUPERVISOR")

                        .pathMatchers("/api/**").authenticated()
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
