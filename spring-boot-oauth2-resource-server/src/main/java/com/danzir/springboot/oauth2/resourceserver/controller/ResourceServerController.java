package com.danzir.springboot.oauth2.resourceserver.controller;

import com.danzir.springboot.oauth2.resourceserver.service.PermissionsCheckService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/v1/backend")
public class ResourceServerController {

    private final PermissionsCheckService permissionsCheckService;

    public ResourceServerController(PermissionsCheckService permissionsCheckService){
        this.permissionsCheckService = permissionsCheckService;
    }

    @GetMapping(path = "/permissions/check")
    public ResponseEntity<HashMap> checkPermissions(JwtAuthenticationToken jwtAuthToken) {
        return ResponseEntity.ok(permissionsCheckService.getPermissions(jwtAuthToken));
    }

    @GetMapping(path = "/do/this")
    @PreAuthorize("hasRole('my-role-1')")
    public ResponseEntity<String> doThis() {
        return ResponseEntity.ok("yes, you can do this");
    }

    @GetMapping(path = "/do/that")
    @PreAuthorize("hasRole('my-role-2')")
    public ResponseEntity<String> doThat() {
        return ResponseEntity.ok("yes, you can do that");
    }

    @GetMapping(path = "/do/other")
    @PreAuthorize("hasRole('my-role-3')")
    public ResponseEntity<String> doOther() {
        return ResponseEntity.ok("yes, you can do other");
    }

}
