package com.cosain.trilo.auth.presentation.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OauthLoginRequest {

    @NotEmpty
    private String code;

    @NotEmpty
    private String redirect_uri;

}
