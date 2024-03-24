package com.example.BackendServer.dto.oauth2;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginResponseDto {

    private TokenInfoResponseDto tokenInfoResponseDto;
    private String name;

    @Builder
    public LoginResponseDto(TokenInfoResponseDto tokenInfoResponseDto, String name) {
        this.tokenInfoResponseDto = tokenInfoResponseDto;
        this.name = name;
    }
}
