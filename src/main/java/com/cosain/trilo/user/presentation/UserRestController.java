package com.cosain.trilo.user.presentation;

import com.cosain.trilo.common.LoginUser;
import com.cosain.trilo.user.application.UserService;
import com.cosain.trilo.user.domain.User;
import com.cosain.trilo.user.presentation.dto.UserMyPageResponse;
import com.cosain.trilo.user.presentation.dto.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;
    private final Clock clock;

    @GetMapping("/{userId}/profile")
    @ResponseStatus(HttpStatus.OK)
    public UserProfileResponse getUserProfile(@PathVariable("userId") Long targetUserId, @LoginUser User user){
        UserProfileResponse userProfileResponse = userService.getUserProfile(targetUserId, user.getId());
        return userProfileResponse;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("userId") Long targetUserId, @LoginUser User user){
        userService.delete(targetUserId, user.getId());
    }

    @GetMapping("/{userId}/my-page")
    @ResponseStatus(HttpStatus.OK)
    public UserMyPageResponse getMyPage(@PathVariable("userId") Long userId){
        LocalDate today = LocalDate.now(clock);
        UserMyPageResponse myPageResponse = userService.getMyPage(userId, today);
        return myPageResponse;
    }

}
