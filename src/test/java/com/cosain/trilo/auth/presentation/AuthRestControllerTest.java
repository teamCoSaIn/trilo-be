package com.cosain.trilo.auth.presentation;

import com.cosain.trilo.auth.application.AuthService;
import com.cosain.trilo.support.RestDocsTestSupport;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthRestControllerTest extends RestDocsTestSupport {

    @MockBean
    private AuthService authService;

    @Test
    void 접근토큰_재발급_요청() throws Exception{

        given(authService.reissueAccessToken(any())).willReturn("accessToken");

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/auth/reissue")
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
    void 접근토큰_재발급_요청시_쿠키가_존재하지_않으면_400_BadRequest_에러를_발생시킨다() throws Exception{
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/auth/reissue"))
                .andExpect(status().isBadRequest());
    }
}