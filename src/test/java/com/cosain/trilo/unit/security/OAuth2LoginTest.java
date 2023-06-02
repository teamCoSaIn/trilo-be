package com.cosain.trilo.unit.security;

import com.cosain.trilo.config.security.SecurityConfig;
import com.cosain.trilo.support.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SecurityConfig.class)
public class OAuth2LoginTest extends RestDocsTestSupport{

    private static final String AUTH_URL_REQUEST_URL = "/api/auth/login/";

    @Test
    void 카카오_인증_URL_요청() throws Exception {
        mockMvc.perform(get(AUTH_URL_REQUEST_URL+"kakao"))
                .andExpect(status().is3xxRedirection())
                .andDo(print())
                .andDo(restDocs.document(
                        responseCookies(
                                cookieWithName("oauth2_auth_request").description("응답요청에 포함된 쿠키")
                        )
                ));

    }

    @Test
    void 구글_인증_URL_요청() throws Exception{
        mockMvc.perform(get(AUTH_URL_REQUEST_URL+"google"))
                .andExpect(status().is3xxRedirection())
                .andDo(restDocs.document(
                        responseCookies(
                                cookieWithName("oauth2_auth_request").description("응답요청에 포함된 쿠키")
                        )
                ));
    }

    @Test
    void 네이버_인증_URL_요청() throws Exception{
        mockMvc.perform(get(AUTH_URL_REQUEST_URL+"naver"))
                .andExpect(status().is3xxRedirection())
                .andDo(restDocs.document(
                        responseCookies(
                                cookieWithName("oauth2_auth_request").description("응답요청에 포함된 쿠키")
                        )
                ));
    }

}
