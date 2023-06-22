package com.cosain.trilo.auth.application.dto;

import lombok.Getter;

@Getter
public class ReIssueAccessTokenResult {

    private String accessToken;
    private Long userId;

    private ReIssueAccessTokenResult(String accessToken, Long userId) {
        this.accessToken = accessToken;
        this.userId = userId;
    }
    public static ReIssueAccessTokenResult of(String accessToken, Long userId) {
        return new ReIssueAccessTokenResult(accessToken, userId);
    }
}
