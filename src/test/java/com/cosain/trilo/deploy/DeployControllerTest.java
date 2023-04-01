package com.cosain.trilo.deploy;

import com.cosain.trilo.support.RestControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeployController.class)
class DeployControllerTest extends RestControllerTest {

    private String version;

    @BeforeEach
    void setUp(@Value("${deploy-module.version}") String version){
        this.version = version;
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