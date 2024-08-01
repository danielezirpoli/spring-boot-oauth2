package com.danzir.springboot.oauth2.client.config;

import com.danzir.springboot.oauth2.client.service.AuthService;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import io.netty.handler.logging.LogLevel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

@Configuration
public class WebClientConfig {

    @Value("${resource-server.base.url}")
    private String backendBaseUrl;

    private final AuthService authService;

    public WebClientConfig(AuthService authService){
        this.authService = authService;
    }

    /*
    //Uses the standard Authorization HTTP header to bring the access_token
    @Value("${spring.security.oauth2.client.registration.external.provider}")
    private String registrationId;

    @Bean
    public WebClient webClient(OAuth2AuthorizedClientManager authorizedClientManager) {
        ServletOAuth2AuthorizedClientExchangeFilterFunction filter =
                new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);

        HttpClient httpClient = HttpClient
                .create()
                .wiretap("reactor.netty.http.client.HttpClient",
                        LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL);

        filter.setDefaultClientRegistrationId(registrationId);

        return WebClient.builder()
                .apply(filter.oauth2Configuration())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
    */

    //To use custom HTTP header to bring the access_token
    @Bean
    public WebClient webClient() {
        HttpClient httpClient = HttpClient
                .create()
                .wiretap("reactor.netty.http.client.HttpClient",
                        LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL);

        return WebClient.builder()
                .filter((request, next) -> {
                    // Recupera il token da qualche sorgente (ad es. contesto di sicurezza)
                    String accessToken = authService.getAccessToken();
                    BearerAccessToken bearerAccessToken = new BearerAccessToken(accessToken);
                    // Aggiungi l'header personalizzato
                    return next.exchange(
                            ClientRequest.from(request)
                                    .header("jwt-token", bearerAccessToken.toAuthorizationHeader())
                                    .build()
                    );
                })
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(backendBaseUrl)
                .build();
    }

}
