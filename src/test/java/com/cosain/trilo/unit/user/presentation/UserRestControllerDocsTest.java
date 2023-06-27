package com.cosain.trilo.unit.user.presentation;

import com.cosain.trilo.support.RestDocsTestSupport;
import com.cosain.trilo.user.presentation.UserRestController;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

@WebMvcTest(UserRestController.class)
public class UserRestControllerDocsTest extends RestDocsTestSupport {

    private final String BASE_URL = "/api/users";
    private final String ACCESS_TOKEN = "Bearer accessToken";
    @Test
    public void 사용자_프로필_조회() throws Exception{
        // given
        mockingForLoginUserAnnotation();
        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL + "/profile")
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 타입 AccessToken")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("회원 ID"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("회원 이름"),
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("profileImageUrl").type(JsonFieldType.STRING).description("프로필 이미지 URL"),
                                fieldWithPath("authProvider").type(JsonFieldType.STRING).description("소셜 로그인 제공자"),
                                fieldWithPath("role").type(JsonFieldType.STRING).description("회원 자격 (MEMBER, ADMIN)")
                        )
                ));
    }
}
