package com.cosain.trilo.auth.application.dto;

import lombok.Getter;

@Getter
public class ReIssueAccessTokenResult {

    private String accessToken;

    private ReIssueAccessTokenResult(String accessToken) {
        this.accessToken = accessToken;
    }
    public static ReIssueAccessTokenResult of(String accessToken) {
        return new ReIssueAccessTokenResult(accessToken);
    }
}
