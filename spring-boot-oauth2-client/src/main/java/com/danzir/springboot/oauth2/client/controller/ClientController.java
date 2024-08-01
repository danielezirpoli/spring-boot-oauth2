package com.danzir.springboot.oauth2.client.controller;

import com.danzir.springboot.oauth2.client.feign.BackendFeignClient;
import com.danzir.springboot.oauth2.client.service.AuthService;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/client")
public class ClientController {

    private final AuthService authService;
    private final OAuth2AuthorizedClientService authorizedClientService;

    private final BackendFeignClient backendFeignClient;

    private final WebClient webClient;

    public ClientController(AuthService authService, WebClient webClient, BackendFeignClient backendFeignClient, OAuth2AuthorizedClientService authorizedClientService){
        this.authService = authService;
        this.webClient = webClient;
        this.backendFeignClient = backendFeignClient;
        this.authorizedClientService = authorizedClientService;
    }

    //never do this - never expose tokens
    @GetMapping(path = "/read/tokens")
    public ResponseEntity<Map> getToken() {
        OAuth2User user = authService.getAuthentication().getPrincipal(); // id_token + other
        String accessToken = authService.getAccessToken(); // access_token
        return ResponseEntity.ok(new HashMap(){{
            put("response payload from", "client application");
            put("access_token", accessToken);
            put("id_token (decoded)", user.getAttributes());
        }});
    }

    @GetMapping(path = "/permissions/check/feign")
    public ResponseEntity<Map> checkPermissionsFeign() {
        String accessToken = authService.getAccessToken();
        BearerAccessToken bearerAccessToken = new BearerAccessToken(accessToken);
        return ResponseEntity.ok(backendFeignClient.getPermissions(bearerAccessToken.toAuthorizationHeader()));
    }

    @GetMapping(path = "/permissions/check/webclient")
    public ResponseEntity<Map> checkPermissionsWebClient() {
        Map beResp = this.webClient.get()
                .uri("/v1/backend/permissions/check")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        return ResponseEntity.ok(beResp);
    }

    @GetMapping(path = "/do/this")
    public ResponseEntity<String> doThis() {
        return this.webClient.get()
                .uri("/v1/backend/do/this")
                .retrieve()
                .toEntity(String.class)
                .block();
    }

    @GetMapping(path = "/do/that")
    public ResponseEntity<String> doThat() {
        return this.webClient.get()
                .uri("/v1/backend/do/that")
                .retrieve()
                .toEntity(String.class)
                .block();
    }

    @GetMapping(path = "/do/other")
    public ResponseEntity<String> doOther() {
        return this.webClient.get()
                .uri("/v1/backend/do/other")
                .retrieve()
                .toEntity(String.class)
                .block();
    }

}
