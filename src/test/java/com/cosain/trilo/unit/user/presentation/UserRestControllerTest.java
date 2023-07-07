package com.cosain.trilo.unit.user.presentation;


import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.user.application.UserService;
import com.cosain.trilo.user.presentation.UserRestController;
import com.cosain.trilo.user.presentation.dto.UserProfileResponse;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

import static com.cosain.trilo.fixture.UserFixture.KAKAO_MEMBER;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(UserRestController.class)
public class UserRestControllerTest extends RestControllerTest {

    @MockBean
    private UserService userService;

    @MockBean
    private Clock clock;

    private final String BASE_URL = "/api/users";
    private final String ACCESS_TOKEN = "Bearer accessToken";

    @Nested
    class 회원_프로필_조회{
        @Test
        public void 인증된_사용자_요청_200() throws Exception{
            // given
            Long userId = 2L;
            mockingForLoginUserAnnotation();
            given(userService.getUserProfile(userId, 1L)).willReturn(UserProfileResponse.from(KAKAO_MEMBER.create()));

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL + "/{userId}/profile", userId)
                            .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN))
                    .andExpect(MockMvcResultMatchers.status().isOk());
        }


        @Test
        public void 미인증된_사용자_요청_401() throws Exception{
            // given
            Long userId = 1L;

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL + "/{userId}/profile", userId)
                            .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN))
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                    .andExpect(jsonPath("$.errorCode").value("auth-0001"))
                    .andExpect(jsonPath("$.errorMessage").exists())
                    .andExpect(jsonPath("$.errorDetail").exists());
        }
    }

    @Nested
    class 회원_탈퇴{
        @Test
        void 인증된_사용자_요청_204() throws Exception{
            // given
            Long userId = 1L;
            mockingForLoginUserAnnotation();

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.delete(BASE_URL + "/{userId}", userId)
                            .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN))
                    .andExpect(MockMvcResultMatchers.status().isNoContent());
        }

        @Test
        void 미인증된_사용자_요청_401() throws Exception{
            // given
            Long userId = 1L;

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.delete(BASE_URL + "/{userId}", userId)
                            .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN))
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                    .andExpect(jsonPath("$.errorCode").value("auth-0001"))
                    .andExpect(jsonPath("$.errorMessage").exists())
                    .andExpect(jsonPath("$.errorDetail").exists());
        }
    }

    @Nested
    class 회원_마이페이지_조회{

        private Long userId = 1L;
        @Test
        void 인증된_사용자_요청_200() throws Exception{
            // given
            mockingForLoginUserAnnotation();

            Clock fixedClock = Clock.fixed(
                    LocalDate.of(2023, 4, 28).atStartOfDay(ZoneId.systemDefault()).toInstant(),
                    ZoneId.systemDefault()
            );
            given(clock.instant()).willReturn(fixedClock.instant());
            given(clock.getZone()).willReturn(fixedClock.getZone());

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL + "/{userId}/my-page", userId)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN))
                    .andExpect(MockMvcResultMatchers.status().isOk());

        }

        @Test
        void 미인증된_사용자_요청_204() throws Exception{
            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL + "/{userId}/my-page", userId)
                            .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN))
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized());
        }
    }

}
