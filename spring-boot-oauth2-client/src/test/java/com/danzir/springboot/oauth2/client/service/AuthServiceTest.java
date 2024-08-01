package com.danzir.springboot.oauth2.client.service;

import com.c4_soft.springaddons.security.oauth2.test.annotations.WithOAuth2Login;
import com.danzir.springboot.oauth2.client.configuration.MockOAuth2AuthorizedClientService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Import(MockOAuth2AuthorizedClientService.class)
@WithOAuth2Login(authorizedClientRegistrationId = "external")
@ActiveProfiles("test")
public class AuthServiceTest {

    @Autowired
    AuthService authService;

    @Test
    public void givenLoggedInUser_whenGetAccessToken_thenOk(){
        String token = authService.getAccessToken();
        Assertions.assertEquals("fakejwt", token);
    }

}
