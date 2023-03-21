package com.cosain.trilo.security;

import com.cosain.trilo.support.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OAuth2LoginTest extends RestDocsTestSupport{

    private static final String AUTH_URL_REQUEST_URL = "/api/auth/login/";
    private static final String AUTH_URL_HEADER_NAME = "Auth-Url";

    @Test
    void 카카오_인증_URL_요청() throws Exception {
        mockMvc.perform(get(AUTH_URL_REQUEST_URL+"kakao"))
                .andExpect(status().isOk())
                .andExpect(header().exists(AUTH_URL_HEADER_NAME))
                .andDo(restDocs.document(
                        responseHeaders(
                                headerWithName(AUTH_URL_HEADER_NAME).description("인증 URL")
                        )
                ));
    }

    @Test
    void 구글_인증_URL_요청() throws Exception{
        mockMvc.perform(get(AUTH_URL_REQUEST_URL+"google"))
                .andExpect(status().isOk())
                .andExpect(header().exists(AUTH_URL_HEADER_NAME))
                .andDo(restDocs.document(
                        responseHeaders(
                                headerWithName(AUTH_URL_HEADER_NAME).description("인증 URL")
                        )
                ));
    }

    @Test
    void 네이버_인증_URL_요청() throws Exception{
        mockMvc.perform(get(AUTH_URL_REQUEST_URL+"naver"))
                .andExpect(status().isOk())
                .andExpect(header().exists(AUTH_URL_HEADER_NAME))
                .andDo(restDocs.document(
                        responseHeaders(
                                headerWithName(AUTH_URL_HEADER_NAME).description("인증 URL")
                        )
                ));
    }

}
