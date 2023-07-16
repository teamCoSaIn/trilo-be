package com.cosain.trilo.user.presentation;

import com.cosain.trilo.auth.infra.jwt.UserPayload;
import com.cosain.trilo.auth.presentation.Login;
import com.cosain.trilo.auth.presentation.LoginUser;
import com.cosain.trilo.user.application.UserService;
import com.cosain.trilo.user.presentation.dto.UserMyPageResponse;
import com.cosain.trilo.user.presentation.dto.UserProfileResponse;
import com.cosain.trilo.user.presentation.dto.UserUpdateRequest;
import jakarta.validation.Valid;
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
    @Login
    public UserProfileResponse getUserProfile(@PathVariable("userId") Long targetUserId, @LoginUser UserPayload userPayload){
        UserProfileResponse userProfileResponse = userService.getUserProfile(targetUserId, userPayload.getId());
        return userProfileResponse;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Login
    public void deleteUser(@PathVariable("userId") Long targetUserId, @LoginUser UserPayload userPayload){
        userService.delete(targetUserId, userPayload.getId());
    }

    @GetMapping("/{userId}/my-page")
    @ResponseStatus(HttpStatus.OK)
    public UserMyPageResponse getMyPage(@PathVariable("userId") Long userId){
        LocalDate today = LocalDate.now(clock);
        UserMyPageResponse myPageResponse = userService.getMyPage(userId, today);
        return myPageResponse;
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Login
    public void updateUser(@PathVariable("userId") Long targetUserId, @LoginUser UserPayload userPayload, @Valid @RequestBody UserUpdateRequest userUpdateRequest){
        userService.update(targetUserId, userPayload.getId(), userUpdateRequest);
    }

}
