package com.cosain.trilo.unit.auth.presentation.api;

import com.cosain.trilo.auth.application.AuthService;
import com.cosain.trilo.auth.presentation.AuthRestController;
import com.cosain.trilo.auth.presentation.dto.RefreshTokenStatusResponse;
import com.cosain.trilo.support.RestControllerTest;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthRestController.class)
class AuthRestControllerTest extends RestControllerTest {

    @MockBean
    private AuthService authService;

    private static final String BASE_URL = "/api/auth";

    @Test
    void 쿠키를_포함한_접근_토큰_재발급_요청시_200_상태코드_반환_확인() throws Exception{

        given(authService.reissueAccessToken(any())).willReturn("accessToken");

        mockMvc.perform(RestDocumentationRequestBuilders.post(BASE_URL + "/reissue")
                        .cookie(new Cookie("refreshToken", "refreshToken")))
                .andExpect(jsonPath("$.authType").isNotEmpty())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(status().isOk());
    }

    @Test
    void 접근토큰_재발급_요청시_쿠키가_존재하지_않을_경우_400_Bad_Request_에러를_발생_확인() throws Exception{
        mockMvc.perform(RestDocumentationRequestBuilders.post(BASE_URL + "/reissue"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 재발급_토큰_상태_조회_요청시_재발급_토큰이_유효한_경우() throws Exception {

        given(authService.createTokenStatus(any())).willReturn(RefreshTokenStatusResponse.from(true));

        mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL+"/token/refresh-token-info")
                        .cookie(new Cookie("refreshToken", "refreshToken")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availability").value(true));
    }

    @Test
    void 재발급_토큰_상태_조회_요청시_재발급_토큰이_유효하지_않은_경우() throws Exception {

        given(authService.createTokenStatus(any())).willReturn(RefreshTokenStatusResponse.from(false));

        mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL+"/token/refresh-token-info")
                        .cookie(new Cookie("refreshToken", "refreshToken")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availability").value(false));
    }

    @Test
    void 재발급_토큰_상태_조회_요청시_쿠키가_존재하지_않는_경우() throws Exception{

        given(authService.createTokenStatus(any())).willReturn(RefreshTokenStatusResponse.from(false));

        mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL+"/token/refresh-token-info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availability").value(false));
    }

    @Test
    @WithMockUser
    void 쿠키와_인증_헤더를_포함한_로그아웃_요청시_200_상태코드_반환_확인() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.post(BASE_URL + "/logout")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                        .cookie(new Cookie("refreshToken", "refreshToken")))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void 로그아웃_요청시_쿠키가_존재하지_않으면_400_BadRequest_에러를_발생시킨다() throws Exception{

        mockMvc.perform(RestDocumentationRequestBuilders.post(BASE_URL + "/logout")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 로그아웃_요청시_인증_헤더가_존재하지_않으면_401_Unauthorized_에러를_발생시킨다() throws Exception {

        mockMvc.perform(RestDocumentationRequestBuilders.post(BASE_URL + "/logout")
                        .cookie(new Cookie("refreshToken", "refreshToken")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 로그아웃_요청시_인증_헤더가_유효하지_않으면_401_Unauthorized_에러를_발생시킨다() throws Exception {

        mockMvc.perform(RestDocumentationRequestBuilders.post(BASE_URL + "/logout")
                        .cookie(new Cookie("refreshToken", "refreshToken"))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken"))
                .andExpect(status().isUnauthorized());
    }
}