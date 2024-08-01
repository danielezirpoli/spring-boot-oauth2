package com.danzir.springboot.oauth2.resourceserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class ResourceServerSecurityConfig {

    private final KeycloakJwtAuthenticationConverter keycloakJwtAuthConverter;

    public ResourceServerSecurityConfig(KeycloakJwtAuthenticationConverter keycloakJwtAuthConverter){
        this.keycloakJwtAuthConverter = keycloakJwtAuthConverter;
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {

        http
                .oauth2ResourceServer((oauth2) -> oauth2.jwt(config -> config.jwtAuthenticationConverter(keycloakJwtAuthConverter)))
                .authorizeHttpRequests((authorize) -> authorize.anyRequest().authenticated());

        return http.build();
    }

    @Bean
    public BearerTokenResolver bearerTokenResolver() {
        DefaultBearerTokenResolver bearerTokenResolver = new DefaultBearerTokenResolver();
        bearerTokenResolver.setBearerTokenHeaderName("jwt-token");
        return bearerTokenResolver;
    }
/*
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    JwtAuthoritiesConverter realmRoles2AuthoritiesConverter() {
        return (Jwt jwt) -> {
            final var realmRoles = Optional.of(jwt.getClaimAsMap("realm_access"))
                    .orElse(Map.of());
            @SuppressWarnings("unchecked")
            final var roles = (List<String>) realmRoles.getOrDefault("roles", List.of());
            return Flux.fromStream(roles.stream())
                    .map(SimpleGrantedAuthority::new)
                    .map(GrantedAuthority.class::cast);
        };
    }
*/
}
