package com.cosain.trilo.integration.user;

import com.cosain.trilo.support.IntegrationTest;
import com.cosain.trilo.user.domain.User;
import com.cosain.trilo.user.domain.UserRepository;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import static com.cosain.trilo.fixture.UserFixture.KAKAO_MEMBER;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserIntegrationTest extends IntegrationTest {
    private final String BASE_URL = "/api/users";
    private final String TOKEN_TYPE = "Bearer ";

    @Autowired
    UserRepository userRepository;

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
}
