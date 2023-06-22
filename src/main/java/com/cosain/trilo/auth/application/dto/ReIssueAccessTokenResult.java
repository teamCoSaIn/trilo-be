package com.cosain.trilo.auth.application.dto;

import lombok.Getter;

@Getter
public class ReIssueAccessTokenResult {

    private String accessToken;
    private Long id;

    private ReIssueAccessTokenResult(String accessToken, Long id) {
        this.accessToken = accessToken;
        this.id = id;
    }
    public static ReIssueAccessTokenResult of(String accessToken, Long id) {
        return new ReIssueAccessTokenResult(accessToken, id);
    }
}
