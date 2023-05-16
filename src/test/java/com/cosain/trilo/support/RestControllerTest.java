package com.cosain.trilo.support;

import com.cosain.trilo.auth.domain.repository.TokenRepository;
import com.cosain.trilo.auth.infra.TokenAnalyzer;
import com.cosain.trilo.auth.infra.TokenProvider;
import com.cosain.trilo.config.LoggingTestConfig;
import com.cosain.trilo.config.MessageSourceTestConfig;
import com.cosain.trilo.config.SecurityTestConfig;
import com.cosain.trilo.user.domain.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import static com.cosain.trilo.fixture.UserFixture.KAKAO_MEMBER;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@Import({SecurityTestConfig.class, MessageSourceTestConfig.class, LoggingTestConfig.class})
public class RestControllerTest {

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext context;

    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @MockBean
    protected TokenProvider tokenProvider;
    @MockBean
    protected TokenAnalyzer tokenAnalyzer;
    @MockBean
    protected TokenRepository tokenRepository;
    @MockBean
    protected UserRepository userRepository;

    protected String createJson(Object dto) throws JsonProcessingException{
        return objectMapper.writeValueAsString(dto);
    }

    protected void mockingForLoginUserAnnotation(){
        given(tokenAnalyzer.validateToken(any())).willReturn(true);
        given(tokenRepository.existsLogoutAccessTokenById(any())).willReturn(false);
        given(userRepository.findByEmail(any())).willReturn(Optional.ofNullable(KAKAO_MEMBER.create()));
    }


}
