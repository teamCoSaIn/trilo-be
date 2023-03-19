package com.cosain.trilo.auth.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;

@DataRedisTest
class TokenRepositoryTest {

    @Autowired
    private TokenRepository tokenRepository;

    @BeforeEach
    void setUp(){
        tokenRepository.deleteAll();
    }

    @Test
    void refreshToken_저장(){
        // given
        Token token = Token.of("refresh", 1000L);

        // when & then
        Assertions.assertThat(tokenRepository.existsById(token.getRefreshToken()));
    }
}