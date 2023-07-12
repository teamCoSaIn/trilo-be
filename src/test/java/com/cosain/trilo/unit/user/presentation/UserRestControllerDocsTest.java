package com.cosain.trilo.unit.user.presentation;

import com.cosain.trilo.support.RestDocsTestSupport;
import com.cosain.trilo.trip.infra.dto.TripStatistics;
import com.cosain.trilo.user.application.UserService;
import com.cosain.trilo.user.domain.User;
import com.cosain.trilo.user.presentation.UserRestController;
import com.cosain.trilo.user.presentation.dto.UserMyPageResponse;
import com.cosain.trilo.user.presentation.dto.UserProfileResponse;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

import static com.cosain.trilo.fixture.UserFixture.KAKAO_MEMBER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

@WebMvcTest(UserRestController.class)
public class UserRestControllerDocsTest extends RestDocsTestSupport {

    @MockBean
    private UserService userService;

    @MockBean
    private Clock clock;

    private final String BASE_URL = "/api/users";
    private final String ACCESS_TOKEN = "Bearer accessToken";
    @Value("${cloud.aws.s3.bucket-path}")
    private String myPageBaseUrl;
    @Test
    public void 사용자_프로필_조회() throws Exception{
        // given
        Long userId = 1L;
        mockingForLoginUserAnnotation();
        User requestUser = KAKAO_MEMBER.create();
        given(userService.getUserProfile(userId, requestUser.getId())).willReturn(UserProfileResponse.from(requestUser));

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
                                fieldWithPath("nickName").type(JsonFieldType.STRING).description("회원 이름"),
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("profileImageURL").type(JsonFieldType.STRING).description("회원 프로필 이미지 URL(경로)"),
                                fieldWithPath("authProvider").type(JsonFieldType.STRING).description("소셜 로그인 제공자"),
                                fieldWithPath("role").type(JsonFieldType.STRING).description("회원 자격 (MEMBER, ADMIN)")
                        )
                ));
    }

    @Test
    public void 회원_탈퇴() throws Exception{
        // given
        Long userId = 1L;
        mockingForLoginUserAnnotation();

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.delete(BASE_URL + "/{userId}", userId)
                .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 타입 AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("userId").description("탈퇴할 회원 ID")
                        )
                ));
    }

    @Test
    public void 마이페이지_조회() throws Exception{
        // given
        Long userId = 1L;

        Clock fixedClock = Clock.fixed(
                LocalDate.of(2023, 4, 28).atStartOfDay(ZoneId.systemDefault()).toInstant(),
                ZoneId.systemDefault()
        );
        given(clock.instant()).willReturn(fixedClock.instant());
        given(clock.getZone()).willReturn(fixedClock.getZone());

        mockingForLoginUserAnnotation();
        User user = KAKAO_MEMBER.create();
        TripStatistics tripStatistics = new TripStatistics(5L, 3L);
        given(userService.getMyPage(eq(userId), any(LocalDate.class))).willReturn(UserMyPageResponse.of(user, myPageBaseUrl, tripStatistics));

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL + "/{userId}/my-page", userId)
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
                                fieldWithPath("nickName").type(JsonFieldType.STRING).description("회원 이름"),
                                fieldWithPath("imageURL").type(JsonFieldType.STRING).description("이미지 URL"),
                                subsectionWithPath("tripStatistics").type("TripStatistics").description("여행 통계 정보 (하단 표 참고)")
                        ),
                        responseFields(beneathPath("tripStatistics").withSubsectionId("tripStatistics"),
                                fieldWithPath("totalTripCnt").type(JsonFieldType.NUMBER).description("총 여행 개수"),
                                fieldWithPath("terminatedTripCnt").type(JsonFieldType.NUMBER).description("종료된 여행 개수")
                        )
                ));

    }
}
