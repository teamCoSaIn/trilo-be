package com.cosain.trilo.unit.security;

import com.cosain.trilo.config.security.SecurityConfig;
import com.cosain.trilo.support.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SecurityConfig.class)
public class OAuth2LoginTest extends RestDocsTestSupport{

    private static final String AUTH_URL_REQUEST_URL = "/api/auth/login/";

    @Test
    void 카카오_인증_URL_요청() throws Exception {
        mockMvc.perform(get(AUTH_URL_REQUEST_URL+"kakao"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(fieldWithPath("uri").type(STRING).description("카카오 인증 URI"))
                ));
    }

    @Test
    void 구글_인증_URL_요청() throws Exception{
        mockMvc.perform(get(AUTH_URL_REQUEST_URL+"google"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(fieldWithPath("uri").type(STRING).description("구글 인증 URI"))
                ));
    }

    @Test
    void 네이버_인증_URL_요청() throws Exception{
        mockMvc.perform(get(AUTH_URL_REQUEST_URL+"naver"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(fieldWithPath("uri").type(STRING).description("네이버 인증 URI"))
                ));
    }

}
