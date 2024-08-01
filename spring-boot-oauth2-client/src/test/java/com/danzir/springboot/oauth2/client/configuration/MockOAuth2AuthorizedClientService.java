package com.danzir.springboot.oauth2.client.configuration;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@TestConfiguration
public class MockOAuth2AuthorizedClientService {

    @Autowired
    ClientRegistrationRepository registrations;

    @Bean
    @Primary
    public OAuth2AuthorizedClientService aAuth2AuthorizedClientService(){
        OAuth2AuthenticationToken authenticationToken = createToken();
        OAuth2AuthorizedClient authorizedClient = createAuthorizedClient(authenticationToken);
        OAuth2AuthorizedClientService authorizedClientService = Mockito.mock(OAuth2AuthorizedClientService.class);
        Mockito.when(authorizedClientService.loadAuthorizedClient(Mockito.anyString(), Mockito.anyString())).thenReturn(authorizedClient);
        return authorizedClientService;
    }

    private OAuth2AuthenticationToken createToken() {
        Set<GrantedAuthority> authorities = new HashSet<>(AuthorityUtils.createAuthorityList("USER"));
        OAuth2User oAuth2User = new DefaultOAuth2User(authorities, Collections.singletonMap("name", "rob"), "name");
        return new OAuth2AuthenticationToken(oAuth2User, authorities, "external");
    }

    private OAuth2AuthorizedClient createAuthorizedClient(OAuth2AuthenticationToken authenticationToken) {
        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "fakejwt",
                Instant.now(),
                Instant.now().plus(Duration.ofDays(1))
        );

        ClientRegistration clientRegistration = this.registrations.findByRegistrationId(authenticationToken.getAuthorizedClientRegistrationId());
        return new OAuth2AuthorizedClient(clientRegistration, authenticationToken.getName(), accessToken);
    }

}
