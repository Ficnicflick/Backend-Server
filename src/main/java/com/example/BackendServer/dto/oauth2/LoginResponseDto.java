package com.example.BackendServer.dto.oauth2;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(name = "카카오 로그인/회원가입 DTO")
public class LoginResponseDto {

    @Schema(description = "jwt token",
    example = "{\n" +
            "            \"accessToken\": \"Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIzMzgxNDE0MTc0IiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MTcxMTI1MTcyN30.Dct91MDMti-vk6bc7X_p3cDWKEUq7gVO4DwgPNCBUH0\",\n" +
            "            \"refreshToken\": \"Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIzMzgxNDE0MTc0IiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MTcxMTg1NDcyN30.xf-PiP4D36MEJtXkjE5ZrNn3vjZKPGXhFnLfp0ReCRI\"\n" +
            "        },")
    private TokenInfoResponseDto tokenInfoResponseDto;
    @Schema(description = "카카오 닉네임", example = "김승진")
    private String name;

    private String email;

    @Builder
    public LoginResponseDto(TokenInfoResponseDto tokenInfoResponseDto, String name, String email) {
        this.tokenInfoResponseDto = tokenInfoResponseDto;
        this.name = name;
        this.email = email;
    }
}
