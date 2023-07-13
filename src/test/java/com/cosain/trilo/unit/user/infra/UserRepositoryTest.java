package com.cosain.trilo.unit.user.infra;

import com.cosain.trilo.fixture.UserFixture;
import com.cosain.trilo.support.RepositoryTest;
import com.cosain.trilo.user.domain.User;
import com.cosain.trilo.user.domain.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RepositoryTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    EntityManager em;

    @Test
    void 사용자_저장(){
        // given
        User user = UserFixture.naverUser_NullId();
        userRepository.save(user);
        em.flush();
        em.clear();

        // when
        User findUser = userRepository.findById(user.getId()).orElse(null);

        // then
        assertThat(findUser).isNotNull();
    }

    @Test
    void 이메일로_사용자_조회(){
        // given
        User user = UserFixture.kakaoUser_NullId();
        userRepository.save(user);
        em.flush();
        em.clear();

        // when
        User findUser = userRepository.findByEmail(user.getEmail()).orElse(null);

        // then
        assertThat(findUser).isNotNull();
    }
}