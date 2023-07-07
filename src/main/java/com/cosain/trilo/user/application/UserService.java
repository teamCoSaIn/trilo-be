package com.cosain.trilo.user.application;

import com.cosain.trilo.auth.infra.OAuthProfileDto;
import com.cosain.trilo.trip.infra.dto.TripStatistics;
import com.cosain.trilo.trip.infra.repository.trip.TripQueryRepository;
import com.cosain.trilo.user.application.event.UserDeleteEvent;
import com.cosain.trilo.user.application.exception.NoUserDeleteAuthorityException;
import com.cosain.trilo.user.application.exception.NoUserProfileSearchAuthorityException;
import com.cosain.trilo.user.application.exception.UserNotFoundException;
import com.cosain.trilo.user.domain.User;
import com.cosain.trilo.user.domain.UserRepository;
import com.cosain.trilo.user.presentation.dto.UserMyPageResponse;
import com.cosain.trilo.user.presentation.dto.UserProfileResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final TripQueryRepository tripQueryRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final String s3ImageBaseURL;

    public UserService(
            UserRepository userRepository,
            TripQueryRepository tripQueryRepository,
            ApplicationEventPublisher eventPublisher,
            @Value("${cloud.aws.s3.bucket-name}") String bucketName,
            @Value("${cloud.aws.s3.bucket-path}") String bucketPath
    ) {
        this.userRepository = userRepository;
        this.tripQueryRepository = tripQueryRepository;
        this.eventPublisher = eventPublisher;
        this.s3ImageBaseURL = bucketPath.concat(bucketName);
    }

    public Long createOrUpdate(OAuthProfileDto oAuthProfileDto){
        Optional<User> userOptional = userRepository.findByEmail(oAuthProfileDto.getEmail());

        User user = userOptional.map(existingUser -> {
            existingUser.updateUserByOauthProfile(oAuthProfileDto);
            return existingUser;
        }).orElse(User.from(oAuthProfileDto, s3ImageBaseURL));

        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(Long targetUserId, Long requestUserId){

        User user = findUserOrThrows(targetUserId);
        validateAuthority(user, requestUserId);
        return UserProfileResponse.from(user);
    }

    private void validateAuthority(User user, Long requestUserId) {
        if(!user.getId().equals(requestUserId)){
            throw new NoUserProfileSearchAuthorityException();
        }
    }

    private User findUserOrThrows(Long userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    public void delete(Long targetUserId, Long requestUserId){

        validateUserDeleteAuthority(targetUserId, requestUserId);
        User findUser = userRepository.findById(targetUserId)
                .orElseThrow(UserNotFoundException::new);

        eventPublisher.publishEvent(new UserDeleteEvent(findUser.getId())); // 비동기적으로 사용자 관련된 여행 정보 삭제
        userRepository.deleteById(targetUserId);
    }

    private void validateUserDeleteAuthority(Long targetUserId, Long requestUserId){
        if(!targetUserId.equals(requestUserId)){
            throw new NoUserDeleteAuthorityException();
        }
    }

    @Transactional(readOnly = true)
    public UserMyPageResponse getMyPage(Long userId, LocalDate today){
        TripStatistics tripStatistics = tripQueryRepository.findTripStaticsByTripperId(userId, today);
        User user = findUserOrThrows(userId);
        return UserMyPageResponse.of(user, tripStatistics);
    }

}
