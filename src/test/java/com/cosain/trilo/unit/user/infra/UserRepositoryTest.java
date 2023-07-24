package com.cosain.trilo.unit.user.infra;

import com.cosain.trilo.fixture.UserFixture;
import com.cosain.trilo.support.RepositoryTest;
import com.cosain.trilo.user.domain.User;
import com.cosain.trilo.user.domain.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

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

    @Test
    void 삭제_플래그_컬럼에_해당하는_사용자_벌크_삭제(){
        // given
        User userA = UserFixture.kakaoUser_NullId();
        User userB = UserFixture.kakaoUser_NullId();
        User userC = UserFixture.kakaoUser_NullId();

        userA.updateIsDel(true);
        userB.updateIsDel(true);
        userC.updateIsDel(false);

        User findUserA = userRepository.save(userA);
        User findUserB = userRepository.save(userB);
        User findUserC = userRepository.save(userC);

        em.flush();
        em.clear();

        // when
        userRepository.deleteAllWhereIsDelTrue();
        em.flush();
        em.clear();

        // then
        Optional<User> optionalUserA = userRepository.findById(findUserA.getId());
        Optional<User> optionalUserB = userRepository.findById(findUserB.getId());
        Optional<User> optionalUserC = userRepository.findById(findUserC.getId());

        assertThat(optionalUserA.isEmpty()).isTrue();
        assertThat(optionalUserB.isEmpty()).isTrue();
        assertThat(optionalUserC.isPresent()).isTrue();
    }
}