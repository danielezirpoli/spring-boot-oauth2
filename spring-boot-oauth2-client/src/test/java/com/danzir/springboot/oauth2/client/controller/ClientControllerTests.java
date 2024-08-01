package com.danzir.springboot.oauth2.client.controller;

import com.c4_soft.springaddons.security.oauth2.test.annotations.WithOAuth2Login;
import com.danzir.springboot.oauth2.client.configuration.MockOAuth2AuthorizedClientService;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Import(MockOAuth2AuthorizedClientService.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class ClientControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private WireMockServer wireMockServer;

    @BeforeAll
    public void init(){
        wireMockServer = new WireMockServer(9999);
        wireMockServer.start();
        wireMockServer.stubFor(
                WireMock.get(urlPathEqualTo("/v1/backend/permissions/check"))
                        .willReturn(ok()
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody("{\"allow\":[\"that\", \"this\"], \"deny\":[\"other\"]}")
                        )
        );
    }

    @AfterAll
    public void teardown() {
        if (wireMockServer.isRunning()){
            wireMockServer.stop();
        }
    }

    @Test
    public void givenNotLoggedInUser_whenGetToken_thenRedirect() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/v1/client/read/tokens"))
                .andDo(print())
                .andExpectAll(
                        status().is3xxRedirection(),
                        header().string("Location", "http://localhost/oauth2/authorization/external")
                );
    }

    @Test
    @WithOAuth2Login(authorizedClientRegistrationId = "external")
    public void givenLoggedInUser_whenGetToken_thenOk() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/v1/client/read/tokens"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.access_token").value("fakejwt")
                );
    }

    @Test
    @WithOAuth2Login(authorizedClientRegistrationId = "external")
    public void givenLoggedInUser_whenCheckPermissionsFeign_thenOk() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/v1/client/permissions/check/feign"))
                .andDo(print())
                .andExpect(jsonPath("$.allow[0]").value("that"))
                .andExpect(status().isOk());
    }

    @Test
    @WithOAuth2Login(authorizedClientRegistrationId = "external")
    public void givenLoggedInUser_whenCheckPermissionsWebClient_thenOk() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/v1/client/permissions/check/webclient"))
                .andDo(print())
                .andExpect(jsonPath("$.allow[0]").value("that"))
                .andExpect(status().isOk());
    }

}
