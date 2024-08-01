package com.danzir.springboot.oauth2.resourceserver.config;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

@Component
public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken>, InitializingBean {

    @Override
    public void afterPropertiesSet() {
    }

    @Override
    public AbstractAuthenticationToken convert(final Jwt source) {
        return new JwtAuthenticationToken(source, Stream.concat(new JwtGrantedAuthoritiesConverter().convert(source)
                        .stream(), extractResourceRoles(source).stream())
                .collect(Collectors.toSet()));
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(final Jwt jwt) {
        Map<String, Collection<String>> realmAccess = jwt.getClaim("realm_access");
        Collection<String> realmRoles = realmAccess.get("roles");
        return realmRoles.isEmpty() ? Collections.EMPTY_SET
                : realmRoles.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r)).collect(Collectors.toSet());
    }

}
