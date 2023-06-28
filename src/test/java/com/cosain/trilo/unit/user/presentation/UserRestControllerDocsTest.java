package com.cosain.trilo.unit.user.presentation;

import com.cosain.trilo.support.RestDocsTestSupport;
import com.cosain.trilo.user.application.UserService;
import com.cosain.trilo.user.presentation.UserRestController;
import com.cosain.trilo.user.presentation.dto.UserProfileResponse;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static com.cosain.trilo.fixture.UserFixture.KAKAO_MEMBER;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

@WebMvcTest(UserRestController.class)
public class UserRestControllerDocsTest extends RestDocsTestSupport {

    @MockBean
    private UserService userService;

    private final String BASE_URL = "/api/users";
    private final String ACCESS_TOKEN = "Bearer accessToken";
    @Test
    public void 사용자_프로필_조회() throws Exception{
        // given
        Long userId = 1L;
        mockingForLoginUserAnnotation();
        given(userService.getUserProfile(userId, 1L)).willReturn(UserProfileResponse.from(KAKAO_MEMBER.create()));

        mockingForLoginUserAnnotation();
        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL + "/{userId}/profile", userId)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 타입 AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("userId").description("조회할 회원 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("회원 ID"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("회원 이름"),
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("profileImageURL").type(JsonFieldType.STRING).description("회원 프로필 이미지 URL(경로)"),
                                fieldWithPath("authProvider").type(JsonFieldType.STRING).description("소셜 로그인 제공자"),
                                fieldWithPath("role").type(JsonFieldType.STRING).description("회원 자격 (MEMBER, ADMIN)")
                        )
                ));
    }
}
