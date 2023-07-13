package com.cosain.trilo.user.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserUpdateRequest {

    @Size(max = 20, message = "닉네임은 20 글자 미만으로 입력해주세요.")
    @NotBlank(message = "닉네임을 입력해주세요. 공백은 허용하지 않습니다.")
    private String nickName;

    private UserUpdateRequest(){
    }

    public UserUpdateRequest(String nickName) {
        this.nickName = nickName;
    }
}