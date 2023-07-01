package com.cosain.trilo.user.application;

import com.cosain.trilo.user.application.event.UserDeleteEvent;
import com.cosain.trilo.user.application.exception.NoUserDeleteAuthorityException;
import com.cosain.trilo.user.application.exception.NoUserProfileSearchAuthorityException;
import com.cosain.trilo.user.application.exception.UserNotFoundException;
import com.cosain.trilo.user.domain.User;
import com.cosain.trilo.user.domain.UserRepository;
import com.cosain.trilo.user.presentation.dto.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

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
}
