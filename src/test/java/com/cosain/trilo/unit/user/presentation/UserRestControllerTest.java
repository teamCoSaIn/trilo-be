package com.cosain.trilo.unit.user.presentation;


import com.cosain.trilo.fixture.UserFixture;
import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.user.application.UserService;
import com.cosain.trilo.user.presentation.UserRestController;
import com.cosain.trilo.user.presentation.dto.UserProfileResponse;
import com.cosain.trilo.user.presentation.dto.UserUpdateRequest;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
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
            mockingForLoginUserAnnotation(userId);
            given(userService.getUserProfile(eq(userId), eq(userId))).willReturn(UserProfileResponse.from(UserFixture.kakaoUser_Id(userId)));

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
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL + "/{userId}/profile", userId))
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
            mockingForLoginUserAnnotation(userId);

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
            mockMvc.perform(RestDocumentationRequestBuilders.delete(BASE_URL + "/{userId}", userId))
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
            mockingForLoginUserAnnotation(userId);

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
        void 미인증된_사용자_요청_200() throws Exception{
            // when & then
            Clock fixedClock = Clock.fixed(
                    LocalDate.of(2023, 4, 28).atStartOfDay(ZoneId.systemDefault()).toInstant(),
                    ZoneId.systemDefault()
            );
            given(clock.instant()).willReturn(fixedClock.instant());
            given(clock.getZone()).willReturn(fixedClock.getZone());
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL + "/{userId}/my-page", userId))
                    .andExpect(MockMvcResultMatchers.status().isOk());
        }
    }

    @Nested
    class 회원_정보_수정{
        private Long userId = 1L;
        @Test
        void 인증된_사용자_요청_200() throws Exception{
            // given
            mockingForLoginUserAnnotation(userId);
            UserUpdateRequest userUpdateRequest = new UserUpdateRequest("nickName");

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.patch(BASE_URL + "/{userId}", userId)
                            .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                            .content(createJson(userUpdateRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk());

            verify(userService).update(eq(userId), eq(userId), any(UserUpdateRequest.class));
        }

        @ValueSource(strings = {"", " ", "    ","012345678901234567890"})
        @ParameterizedTest
        void 변경하려는_닉네임이_유효하지_않은_요청_400(String nickName) throws Exception{
            // given
            mockingForLoginUserAnnotation(userId);
            UserUpdateRequest userUpdateRequest = new UserUpdateRequest(nickName);

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.patch(BASE_URL + "/{userId}", userId)
                            .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                            .content(createJson(userUpdateRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("request-0003"))
                    .andExpect(jsonPath("$.errorMessage").exists())
                    .andExpect(jsonPath("$.errorDetail").exists());
        }

        @Test
        void 미인증된_사용자_요청_401() throws Exception{
            // given
            UserUpdateRequest userUpdateRequest = new UserUpdateRequest("nickName");

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.patch(BASE_URL + "/{userId}", userId)
                            .content(createJson(userUpdateRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        }
    }

}
