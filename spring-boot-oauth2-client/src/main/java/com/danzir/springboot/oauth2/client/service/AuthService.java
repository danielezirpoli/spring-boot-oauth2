package com.danzir.springboot.oauth2.client.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final OAuth2AuthorizedClientService authorizedClientService;

    public AuthService(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    public OAuth2AuthenticationToken getAuthentication() {
        return (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    }
    public String getAccessToken() {
        OAuth2AuthenticationToken authentication = getAuthentication();
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName());
        return authorizedClient.getAccessToken().getTokenValue();
    }

}
