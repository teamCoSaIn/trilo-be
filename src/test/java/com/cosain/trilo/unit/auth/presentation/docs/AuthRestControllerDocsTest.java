package com.cosain.trilo.unit.auth.presentation.docs;

import com.cosain.trilo.auth.application.AuthService;
import com.cosain.trilo.auth.application.dto.LoginResult;
import com.cosain.trilo.auth.application.dto.OAuthLoginParams;
import com.cosain.trilo.auth.application.dto.ReIssueAccessTokenResult;
import com.cosain.trilo.auth.presentation.AuthRestController;
import com.cosain.trilo.auth.presentation.dto.GoogleOAuthLoginRequest;
import com.cosain.trilo.auth.presentation.dto.KakaoOAuthLoginRequest;
import com.cosain.trilo.auth.presentation.dto.NaverOAuthLoginRequest;
import com.cosain.trilo.auth.presentation.dto.RefreshTokenStatusResponse;
import com.cosain.trilo.support.RestDocsTestSupport;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthRestController.class)
class AuthRestControllerDocsTest extends RestDocsTestSupport {

    @MockBean
    private AuthService authService;

    private static final String BASE_URL = "/api/auth";

    @Test
    void 접근토큰_재발급_요청() throws Exception{

        ReIssueAccessTokenResult result = ReIssueAccessTokenResult.of("accessToken", 1L);
        given(authService.reissueAccessToken(any())).willReturn(result);

        mockMvc.perform(RestDocumentationRequestBuilders.post(BASE_URL +"/reissue")
                        .cookie(new Cookie("refreshToken", "refreshToken")))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestCookies(
                                cookieWithName("refreshToken").description("접근 토큰 발급에 사용될 재발급 토큰")
                        ),
                        responseFields(
                                fieldWithPath("id").type(NUMBER).description("사용자 ID"),
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
                        requestCookies(
                            cookieWithName("refreshToken").description("상태 조회할 재발급 토큰")
                        ),
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
                    requestCookies(
                            cookieWithName("refreshToken").description("삭제할 재발급 토큰")
                    ),
                    requestHeaders(
                            headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 타입 AccessToken")
                    )
                ));
    }

    @Test
    void 카카오_로그인_요청() throws Exception{

        KakaoOAuthLoginRequest kakaoOAuthLoginRequest = new KakaoOAuthLoginRequest("code", "redirect_uri");
        given(authService.login(any(OAuthLoginParams.class))).willReturn(LoginResult.of("accessToken", "refreshToken", 1L));

        mockMvc.perform(RestDocumentationRequestBuilders.post(BASE_URL + "/login/kakao")
                        .content(createJson(kakaoOAuthLoginRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                    requestFields(
                            fieldWithPath("code").type(STRING).description("Authorization Code"),
                            fieldWithPath("redirect_uri").type(STRING).description("인증 코드 발급에 사용했던 Redirect Uri")
                    ),
                    responseFields(
                            fieldWithPath("id").type(NUMBER).description("사용자 ID"),
                            fieldWithPath("authType").type(STRING).description("인증 타입 (Bearer)"),
                            fieldWithPath("accessToken").description("AccessToken")
                    ),
                    responseHeaders(
                            headerWithName("Set-Cookie").description("RefreshToken")
                    )

                ));
    }

    @Test
    void 네이버_로그인_요청() throws Exception {

        given(authService.login(any(OAuthLoginParams.class))).willReturn(LoginResult.of("accessToken", "refreshToken", 1L));
        NaverOAuthLoginRequest naverOAuthLoginRequest = new NaverOAuthLoginRequest("code", "state");

        mockMvc.perform(RestDocumentationRequestBuilders.post(BASE_URL + "/login/naver")
                        .content(createJson(naverOAuthLoginRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                   requestFields(
                           fieldWithPath("code").type(STRING).description("Authorization Code"),
                           fieldWithPath("state").type(STRING).description("Authorization Code 발급시 전달했던 redirect_uri")
                   ),
                   responseFields(
                           fieldWithPath("id").type(NUMBER).description("사용자 ID"),
                           fieldWithPath("authType").type(STRING).description("인증 타입 (Bearer)"),
                           fieldWithPath("accessToken").description("AccessToken")
                   ),
                   responseHeaders(
                           headerWithName("Set-Cookie").description("RefreshToken")
                   )
                ));
    }

    @Test
    void 구글_로그인_요청() throws Exception {

        given(authService.login(any(OAuthLoginParams.class))).willReturn(LoginResult.of("accessToken", "refreshToken", 1L));
        GoogleOAuthLoginRequest googleOAuthLoginRequest = new GoogleOAuthLoginRequest("code", "redirectUrl");

        mockMvc.perform(RestDocumentationRequestBuilders.post(BASE_URL + "/login/google")
                        .content(createJson(googleOAuthLoginRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("code").type(STRING).description("Authorization Code"),
                                fieldWithPath("redirect_uri").type(STRING).description("Authorization Code 발급시 전달했던 redirect_uri")
                        ),
                        responseFields(
                                fieldWithPath("id").type(NUMBER).description("사용자 ID"),
                                fieldWithPath("authType").type(STRING).description("인증 타입 (Bearer)"),
                                fieldWithPath("accessToken").description("AccessToken")
                        ),
                        responseHeaders(
                                headerWithName("Set-Cookie").description("RefreshToken")
                        )
                ));
    }

}