package com.cosain.trilo.integration.user;

import com.cosain.trilo.support.IntegrationTest;
import com.cosain.trilo.user.domain.User;
import com.cosain.trilo.user.domain.UserRepository;
import jakarta.persistence.EntityManager;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import static com.cosain.trilo.fixture.UserFixture.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserIntegrationTest extends IntegrationTest {
    private final String BASE_URL = "/api/users";
    private final String TOKEN_TYPE = "Bearer ";

    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManager entityManager;

    @Nested
    class 회원_프로필_조회{
        @Test
        void 회원_프로필_조회_성공() throws Exception{
            // given
            User user = userRepository.save(KAKAO_MEMBER.create());
            String accessToken = createAccessToken(user.getId());

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL + "/{userId}/profile", user.getId())
                            .header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + accessToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(user.getId()))
                    .andExpect(jsonPath("$.name").value(user.getName()))
                    .andExpect(jsonPath("$.email").value(user.getEmail()))
                    .andExpect(jsonPath("$.profileImageURL").value(user.getProfileImageURL()))
                    .andExpect(jsonPath("$.role").value(user.getRole().name()));
        }

        @Test
        void 회원_프로필_조회_시_다른_회원의_프로필을_조회할_경우_403_응답() throws Exception{
            // given
            User user = userRepository.save(KAKAO_MEMBER.create());
            User anotherUser = userRepository.save(GOOGLE_MEMBER.create());
            String accessToken = createAccessToken(user.getId());

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL + "/{userId}/profile", anotherUser.getId())
                            .header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + accessToken))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.errorCode").value("user-0002"))
                    .andExpect(jsonPath("$.errorMessage").exists())
                    .andExpect(jsonPath("$.errorDetail").exists());
        }
    }

    @Nested
    class 회원_탈퇴{
        @Test
        void 회원_탈퇴_성공() throws Exception{
            // given
            User user = userRepository.save(KAKAO_MEMBER.create());
            String accessToken = createAccessToken(user.getId());

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.delete(BASE_URL + "/{userId}", user.getId())
                    .header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + accessToken))
                    .andExpect(status().isNoContent());

        }

        @Test
        void 회원_탈퇴_시_본인이_아닌_회원의_탈퇴_요청을_하는_경우_403_응답() throws Exception{
            // given
            User user = userRepository.save(KAKAO_MEMBER.create());
            User anotherUser  = userRepository.save(NAVER_MEMBER.create());
            String accessToken = createAccessToken(user.getId());
            System.out.println(user.getId() +" "+ anotherUser.getId());

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.delete(BASE_URL + "/{userId}", anotherUser.getId())
                            .header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + accessToken))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.errorCode").value("user-0004"))
                    .andExpect(jsonPath("$.errorMessage").exists())
                    .andExpect(jsonPath("$.errorDetail").exists());
        }
    }


}
