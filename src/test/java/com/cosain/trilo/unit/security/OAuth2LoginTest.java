package com.cosain.trilo.unit.security;

import com.cosain.trilo.config.security.SecurityConfig;
import com.cosain.trilo.support.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SecurityConfig.class)
public class OAuth2LoginTest extends RestDocsTestSupport{

    private final String QUERY_PARAM = "?redirect_uri=http://localhost:3000/oauth2/redirect";

    @Test
    void 소셜_로그인() throws Exception {

        mockMvc.perform(get("/api/auth/login/{provider}"+QUERY_PARAM, "kakao"))
                .andExpect(status().is3xxRedirection())
                .andDo(print())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("provider").description("소셜 로그인 써드 파티 API 제공자 (kakao, google, naver)")
                        ),
                        queryParameters(
                                parameterWithName("redirect_uri").description("로그인 성공 시 이동할 리다이렉트 URL")
                        ),
                        responseCookies(
                                cookieWithName("oauth2_auth_request").description("oauth 요청 정보를 담은 쿠키"),
                                cookieWithName("redirect_uri").description("리다이렉트 URL를 담은 쿠키")

                        )
                ));

    }

}
