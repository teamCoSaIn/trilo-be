package com.cosain.trilo.support;

import com.cosain.trilo.auth.infra.TokenProvider;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import com.cosain.trilo.trip.domain.vo.TripImage;
import com.cosain.trilo.trip.domain.vo.TripPeriod;
import com.cosain.trilo.trip.domain.vo.TripStatus;
import com.cosain.trilo.trip.domain.vo.TripTitle;
import com.cosain.trilo.user.domain.AuthProvider;
import com.cosain.trilo.user.domain.Role;
import com.cosain.trilo.user.domain.User;
import com.cosain.trilo.user.domain.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class IntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext context;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    protected User setupMockNaverUser() {
        return createMockUser("naver-user@naver.com", AuthProvider.NAVER);
    }

    protected User setupMockKakaoUser() {
        return createMockUser("kakao-user@kakao.com", AuthProvider.KAKAO);
    }

    protected User setupMockGoogleUser() {
        return createMockUser("google-user@google.com", AuthProvider.GOOGLE);
    }

    protected String authorizationHeader(User user) {
        String accessToken = tokenProvider.createAccessTokenById(user.getId());
        return String.format("Bearer %s",  accessToken);
    }

    private User createMockUser(String email, AuthProvider authProvider) {
        User mockUser = User.builder()
                .name("사용자")
                .email(email)
                .profileImageUrl("https://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg")
                .authProvider(authProvider)
                .role(Role.MEMBER)
                .build();

        userRepository.save(mockUser);
        return mockUser;
    }

    protected String createRequestJson(Object dto) throws JsonProcessingException {
        return objectMapper.writeValueAsString(dto);
    }

    protected <T> T createResponseObject(ResultActions resultActions, Class<T> clazz) throws UnsupportedEncodingException, JsonProcessingException {
        String jsonResponse = resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        return objectMapper.readValue(jsonResponse, clazz);
    }

}
