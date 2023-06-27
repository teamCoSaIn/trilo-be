package com.cosain.trilo.user.presentation;

import com.cosain.trilo.common.LoginUser;
import com.cosain.trilo.user.domain.User;
import com.cosain.trilo.user.presentation.dto.UserProfileResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    @GetMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    public UserProfileResponse getUserProfile(@LoginUser User user){
        return UserProfileResponse.from(user);
    }

}
