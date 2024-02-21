package com.example.BackendServer.common.auth.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
public class TokenInfoResponse {
    private String accessToken;
    private String refreshToken;

    @Builder
    public TokenInfoResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
