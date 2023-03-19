package com.cosain.trilo.user.infra;

import com.cosain.trilo.user.domain.User;
import com.cosain.trilo.user.domain.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static com.cosain.trilo.fixture.UserFixture.KAKAO_MEMBER;

@Slf4j
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void 사용자_저장(){

        // given
        User user = KAKAO_MEMBER.create();
        // when
        User savedUser = userRepository.save(user);
        // then
        Assertions.assertThat(savedUser).isNotNull();
    }

    @Test
    void 이메일로_사용자_조회(){
        // given
        User user = KAKAO_MEMBER.create();
        User savedUser = userRepository.save(user);

        // when
        User findUser = userRepository.findByEmail(user.getEmail()).get();

        // then
        Assertions.assertThat(findUser).isSameAs(savedUser);
    }
}