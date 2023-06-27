package com.cosain.trilo.unit.user.application;

import com.cosain.trilo.common.exception.UserNotFoundException;
import com.cosain.trilo.user.application.UserService;
import com.cosain.trilo.user.application.exception.NoUserProfileSearchAuthorityException;
import com.cosain.trilo.user.domain.AuthProvider;
import com.cosain.trilo.user.domain.Role;
import com.cosain.trilo.user.domain.User;
import com.cosain.trilo.user.domain.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;


    @Nested
    class 회원_검색_기능{
        @Test
        void 메서드_호출_테스트(){
            // given
            Long requestUserId = 1L;
            Long targetUserId = 1L;
            User user = User.builder()
                    .id(targetUserId)
                    .email("email")
                    .authProvider(AuthProvider.KAKAO)
                    .profileImageUrl("imgae")
                    .role(Role.MEMBER)
                    .build();

            given(userRepository.findById(eq(targetUserId))).willReturn(Optional.ofNullable(user));

            // when
            userService.getUserProfile(targetUserId, requestUserId);

            // then
            verify(userRepository).findById(eq(1L));
        }

        @Test
        void 찾으려는_회원이_존재하지_않을_경우_UserNotFoundException_에러를_발생시킨다(){
            // given
            Long requestUserId = 1L;
            Long targetUserId = 1L;

            // when & then
            Assertions.assertThatThrownBy(() -> userService.getUserProfile(targetUserId, requestUserId)).isInstanceOf(UserNotFoundException.class);
        }

        @Test
        void 본인이_아닌_다른_사람이_조회할_경우_NoUserProfileSearchAuthorityException_에러를_발생시킨다(){
            // given
            Long requestUserId = 1L;
            Long targetUserId = 2L;
            User user = User.builder()
                    .id(targetUserId)
                    .email("email")
                    .authProvider(AuthProvider.KAKAO)
                    .profileImageUrl("imgae")
                    .role(Role.MEMBER)
                    .build();

            given(userRepository.findById(eq(targetUserId))).willReturn(Optional.ofNullable(user));

            // when & then
            Assertions.assertThatThrownBy(() -> userService.getUserProfile(targetUserId, requestUserId)).isInstanceOf(NoUserProfileSearchAuthorityException.class);
        }


    }






}
