package com.cosain.trilo.unit.auth.presentation.docs;

import com.cosain.trilo.auth.application.AuthService;
import com.cosain.trilo.auth.presentation.AuthRestController;
import com.cosain.trilo.auth.presentation.dto.RefreshTokenStatusResponse;
import com.cosain.trilo.support.RestDocsTestSupport;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthRestController.class)
class AuthRestControllerDocsTest extends RestDocsTestSupport {

    @MockBean
    private AuthService authService;

    private static final String BASE_URL = "/api/auth";

    @Test
    void 접근토큰_재발급_요청() throws Exception{

        given(authService.reissueAccessToken(any())).willReturn("accessToken");

        mockMvc.perform(RestDocumentationRequestBuilders.post(BASE_URL +"/reissue")
                        .cookie(new Cookie("refreshToken", "refreshToken")))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("authType").type(STRING).description("인증 타입 (Bearer)"),
                                fieldWithPath("accessToken").type(STRING).description("재발급한 접근 토큰")
                        )
                ));
    }

    @Test
    void 재발급_토큰_상태_조회_요청() throws Exception{

        given(authService.createTokenStatus(any())).willReturn(RefreshTokenStatusResponse.from(true));

        mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL+"/token/refresh-token-info")
                        .cookie(new Cookie("refreshToken", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzd2VldF9zbWVsbEBuYXRlLmNvbSIsImlhdCI6MTY3OTg4MjQ2NiwiZXhwIjoxNjc5ODkzMjY2fQ.v0E4tjveoiOSP2GONUvfTH8-pR_zB5A9w5l5ZNPc4Wk")))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("availability").type(BOOLEAN).description("토큰 사용 가능 여부")
                        )
                ));
    }

    @Test
    @WithMockUser
    void 로그아웃_요청() throws Exception{

        mockMvc.perform(RestDocumentationRequestBuilders.post(BASE_URL + "/logout")
                .cookie(new Cookie("refreshToken", "refreshToken"))
                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                    requestHeaders(
                            headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 타입 AccessToken")
                    )
                ));
    }

}