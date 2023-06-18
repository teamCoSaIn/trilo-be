package com.cosain.trilo.auth.presentation.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleOAuthLoginRequest {

    @NotEmpty
    private String code;

    @NotEmpty
    private String redirect_uri;
}
