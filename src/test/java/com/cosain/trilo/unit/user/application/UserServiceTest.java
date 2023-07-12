package com.cosain.trilo.unit.user.application;

import com.cosain.trilo.auth.infra.OAuthProfileDto;
import com.cosain.trilo.trip.infra.dto.TripStatistics;
import com.cosain.trilo.trip.infra.repository.trip.TripQueryRepository;
import com.cosain.trilo.user.application.UserService;
import com.cosain.trilo.user.application.event.UserDeleteEvent;
import com.cosain.trilo.user.application.exception.NoUserDeleteAuthorityException;
import com.cosain.trilo.user.application.exception.NoUserProfileSearchAuthorityException;
import com.cosain.trilo.user.application.exception.UserNotFoundException;
import com.cosain.trilo.user.domain.AuthProvider;
import com.cosain.trilo.user.domain.Role;
import com.cosain.trilo.user.domain.User;
import com.cosain.trilo.user.domain.UserRepository;
import com.cosain.trilo.user.presentation.dto.UserMyPageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.util.Optional;

import static com.cosain.trilo.fixture.UserFixture.KAKAO_MEMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TripQueryRepository tripQueryRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @BeforeEach
    void setUp(){
        String bucketPath = "/path/to/bucket/";
        userService = new UserService(userRepository, tripQueryRepository, eventPublisher, bucketPath);
    }

    @Nested
    class 회원_생성_또는_업데이트_기능{
        private String email;
        private OAuthProfileDto oAuthProfileDto;
        @BeforeEach
        void setUp(){
            email = "aaaa@nate.com";
            oAuthProfileDto = OAuthProfileDto.builder()
                    .name("김규성")
                    .profileImageUrl("profile-image-url")
                    .provider(AuthProvider.KAKAO)
                    .email(email)
                    .build();
        }

        @Test
        void 신규_회원일_경우_저장(){
            // given
            User user = mock(User.class);
            given(userRepository.findByEmail(eq(email))).willReturn(Optional.empty());
            given(userRepository.save(any(User.class))).willReturn(user);

            // when
            Long userId = userService.createOrUpdate(oAuthProfileDto);

            // then
            assertThat(userId).isEqualTo(user.getId());
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        void 기존_회원일_경우_업데이트_후_저장(){
            // given
            User user = mock(User.class);
            given(userRepository.findByEmail(eq(email))).willReturn(Optional.ofNullable(user));
            given(userRepository.save(any(User.class))).willReturn(user);

            // when
            Long userId = userService.createOrUpdate(oAuthProfileDto);

            // then
            assertThat(userId).isEqualTo(user.getId());
            verify(user, times(1)).updateUserByOauthProfile(any());
            verify(userRepository, times(1)).save(eq(user));
        }
    }


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
            assertThatThrownBy(() -> userService.getUserProfile(targetUserId, requestUserId)).isInstanceOf(UserNotFoundException.class);
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
            assertThatThrownBy(() -> userService.getUserProfile(targetUserId, requestUserId)).isInstanceOf(NoUserProfileSearchAuthorityException.class);
        }

    }

    @Nested
    class 회원_삭제{
        @Test
        void 메서드_호출_테스트(){
            // given
            Long targetUserId = 1L;
            Long requestUserId = 1L;

            User user = KAKAO_MEMBER.create();
            given(userRepository.findById(eq(targetUserId))).willReturn(Optional.ofNullable(user));

            // when
            userService.delete(targetUserId, requestUserId);
            // then
            verify(userRepository).findById(eq(targetUserId));
            verify(eventPublisher).publishEvent(any(UserDeleteEvent.class));
            verify(userRepository).deleteById(eq(targetUserId));
        }

        @Test
        void 요청한_사용자의_ID와_삭제할_대상_사용자의_ID가_다를_경우_예외를_발생시킨다(){
            // given
            Long targetUserId = 1L;
            Long requestUserId = 2L;

            // when & then
            assertThatThrownBy(() -> userService.delete(targetUserId, requestUserId)).isInstanceOf(NoUserDeleteAuthorityException.class);
        }

        @Test
        void 삭제할_대상이_이미_존재하지_않으면_예외를_발생시킨다(){
            // given
            Long targetUserId = 1L;
            Long requestUserId = 1L;

            // when & then
            assertThatThrownBy(() -> userService.delete(targetUserId, requestUserId)).isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    class 마이페이지_조회{

        @Test
        void 사용자_이름과_여행_통계_정보를_반환한다(){
            // given
            Long userId = 1L;
            LocalDate today = LocalDate.of(2023, 4, 28);
            User user = KAKAO_MEMBER.create();
            TripStatistics tripStatistics = new TripStatistics(10L, 3L);
            given(userRepository.findById(eq(userId))).willReturn(Optional.ofNullable(user));
            given(tripQueryRepository.findTripStaticsByTripperId(eq(userId),eq(today))).willReturn(tripStatistics);
            // when
            UserMyPageResponse myPageResponse = userService.getMyPage(userId, today);

            // then
            assertThat(myPageResponse.getNickName()).isEqualTo(user.getNickName());
            assertThat(myPageResponse.getTripStatistics().getTotalTripCnt()).isEqualTo(tripStatistics.getTotalTripCnt());
            assertThat(myPageResponse.getTripStatistics().getTerminatedTripCnt()).isEqualTo(tripStatistics.getTerminatedTripCnt());
        }

        @Test
        void 조회할_회원이_존재하지_않는_경우_예외를_발생시킨다(){
            // given
            Long userId = 1L;
            LocalDate today = LocalDate.of(2023, 4, 28);
            TripStatistics tripStatistics = new TripStatistics(10L, 3L);
            given(tripQueryRepository.findTripStaticsByTripperId(eq(userId), eq(today))).willReturn(tripStatistics);
            // when & then
            assertThatThrownBy(() -> userService.getMyPage(userId, today)).isInstanceOf(UserNotFoundException.class);
        }
    }

}
