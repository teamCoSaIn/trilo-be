package com.cosain.trilo.user.presentation;

import com.cosain.trilo.common.LoginUser;
import com.cosain.trilo.user.application.UserService;
import com.cosain.trilo.user.domain.User;
import com.cosain.trilo.user.presentation.dto.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;

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

}
