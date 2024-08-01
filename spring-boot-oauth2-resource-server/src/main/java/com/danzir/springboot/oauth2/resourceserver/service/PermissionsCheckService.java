package com.danzir.springboot.oauth2.resourceserver.service;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import java.util.*;
@Service
public class PermissionsCheckService {

    public HashMap getPermissions(JwtAuthenticationToken jwtAuthenticationToken){
        Jwt jwt = jwtAuthenticationToken.getToken();

        Map<String, Collection<String>> realmAccess = jwt.getClaim("realm_access");
        Collection<String> realmRoles = realmAccess.get("roles");

        Set<String> allowUseCases = new HashSet<>();
        Set<String> denyUseCases = new HashSet<>();
        allowDenyUseCases(realmRoles, allowUseCases, denyUseCases);

        return new HashMap(){{
            put("allow", allowUseCases);
            put("deny", denyUseCases);
        }};
    }

    private static void allowDenyUseCases(Collection<String> realmRoles, Set<String> allowUseCases, Set<String> denyUseCases) {
        allowDenyThis(realmRoles, allowUseCases, denyUseCases);
        allowDenyThat(realmRoles, allowUseCases, denyUseCases);
        allowDenyOther(realmRoles, allowUseCases, denyUseCases);
    }

    private static void allowDenyThis(Collection<String> realmRole, Set<String> allowUseCases, Set<String> denyUseCases) {
        if(realmRole.contains("my-role-1")){
            allowUseCases.add("this");
        } else {
            denyUseCases.add("this");
        }
    }

    private static void allowDenyThat(Collection<String> realmRole, Set<String> allowUseCases, Set<String> denyUseCases) {
        if(realmRole.contains("my-role-2")){
            allowUseCases.add("that");
        } else {
            denyUseCases.add("that");
        }
    }

    private static void allowDenyOther(Collection<String> realmRole, Set<String> allowUseCases, Set<String> denyUseCases) {
        if(realmRole.contains("my-role-3")){
            allowUseCases.add("other");
        } else {
            denyUseCases.add("other");
        }
    }
}
