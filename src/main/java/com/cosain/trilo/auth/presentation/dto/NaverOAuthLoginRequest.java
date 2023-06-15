package com.cosain.trilo.auth.presentation.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NaverOAuthLoginRequest {
    @NotEmpty
    private String code;
    @NotEmpty
    private String state;
}
