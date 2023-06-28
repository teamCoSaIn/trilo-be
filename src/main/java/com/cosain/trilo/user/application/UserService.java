package com.cosain.trilo.user.application;

import com.cosain.trilo.common.exception.UserNotFoundException;
import com.cosain.trilo.user.application.exception.NoUserProfileSearchAuthorityException;
import com.cosain.trilo.user.domain.User;
import com.cosain.trilo.user.domain.UserRepository;
import com.cosain.trilo.user.presentation.dto.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

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
}
