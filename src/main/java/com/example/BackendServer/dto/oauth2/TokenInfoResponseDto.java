package com.example.BackendServer.dto.oauth2;


import lombok.Builder;
import lombok.Getter;

@Getter
public class TokenInfoResponseDto {
    private String accessToken;
    private String refreshToken;

    @Builder
    public TokenInfoResponseDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
