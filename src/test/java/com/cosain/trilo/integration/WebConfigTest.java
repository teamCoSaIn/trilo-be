package com.cosain.trilo.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class WebConfigTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String ALLOWED_HTTP_METHODS = "GET,POST,HEAD,PUT,PATCH,DELETE,OPTIONS";
    private static final String FE_LOCALHOST = "http://localhost:3000";
    private static final String DOMAIN = "http://cosain-trilo.com";
    private static final String WWW_DOMAIN = "http://www.cosain-trilo.com";

    @ParameterizedTest
    @ValueSource(strings = {FE_LOCALHOST, DOMAIN, WWW_DOMAIN})
    void CORS_허용_테스트(String origin) throws Exception{
        mockMvc.perform(options("/api/trips")
                .header(HttpHeaders.ORIGIN, origin)
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
        )
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, ALLOWED_HTTP_METHODS))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.LOCATION))
                .andDo(print());
    }

    @Test
    void CORS설정이_되어_있지_않은_Origin에서_Preflight_요청을_보내면_허용하지_않는다() throws Exception{

        String notAllowedOrigin = "http://xxxxx.com";

        mockMvc.perform(options("/api/trips")
                .header(HttpHeaders.ORIGIN, notAllowedOrigin)
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
        )
                .andExpect(status().isForbidden())
                .andDo(print());
    }
}
