package com.cosain.trilo.deploy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DeployControllerTest {

    private MockMvc mockMvc;
    private String version;

    @BeforeEach
    void setUp(WebApplicationContext context, @Value("${deploy-module.version}") String version){
        this.version = version;

        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void 배포_버전_확인() throws Exception{
        mockMvc.perform(get("/deploy/version"))
                .andDo(print())
                .andExpect(content().string("Project.version : "+ version))
                .andExpect(status().isOk());
    }

    @Test
    void 배포_HealthCheck() throws Exception{
        mockMvc.perform(get("/deploy/health"))
                .andDo(print())
                .andExpect(content().string("healthy"))
                .andExpect(status().isOk());
    }


}