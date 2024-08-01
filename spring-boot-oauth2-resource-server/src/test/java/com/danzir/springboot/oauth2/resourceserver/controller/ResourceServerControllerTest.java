package com.danzir.springboot.oauth2.resourceserver.controller;

import com.c4_soft.springaddons.security.oauth2.test.annotations.WithJwt;
import com.c4_soft.springaddons.security.oauth2.test.annotations.WithMockAuthentication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ResourceServerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithJwt("test-jwt.json")
    public void givenValidJwt_whenCheckPermissions_thenOk() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/v1/backend/permissions/check"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.allow.length()").value(1),
                        jsonPath("$.deny.length()").value(2),
                        jsonPath("$.deny[0]").value("that"),
                        jsonPath("$.deny[1]").value("other"),
                        jsonPath("$.allow[0]").value("this")
                );
    }

    @Test
    @WithMockAuthentication("ROLE_my-role-1")
    public void givenOnlyMyRole1_whenDoThis_thenOk() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/v1/backend/do/this"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().string("yes, you can do this")
                );
    }

    @Test
    @WithMockAuthentication(authorities = {"ROLE_my-role-1", "ROLE_my-role-2"})
    public void givenMyRole1AndMyRole2_whenDoThis_thenOk() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/v1/backend/do/this"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().string("yes, you can do this")
                );
    }

    @Test
    @WithAnonymousUser
    public void givenAnonymousUser_whenDoThis_then401Unauthorized() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/v1/backend/do/this"))
                .andDo(print())
                .andExpectAll(
                        status().isUnauthorized()
                );
    }

    @Test
    @WithMockAuthentication("ROLE_my-role-2")
    public void givenOnlyMyRole2_whenDoThis_then403Forbidden() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/v1/backend/do/this"))
                .andDo(print())
                .andExpect(
                        status().isForbidden()
                );
    }

}
