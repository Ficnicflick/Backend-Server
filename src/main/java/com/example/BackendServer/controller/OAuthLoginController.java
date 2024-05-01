package com.example.BackendServer.controller;

import com.example.BackendServer.dto.oauth2.LoginResponseDto;
import com.example.BackendServer.dto.oauth2.TokenInfoResponseDto;
import com.example.BackendServer.common.response.BaseResponse;
import com.example.BackendServer.service.OAuth2Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OAuthLoginController {
    private final OAuth2Service oAuth2Service;

    @PostMapping("/login/oauth2/code/kakao") // 카카오 로그인 API
    public BaseResponse<LoginResponseDto> loginKakao(@RequestParam(value = "code",required = true) String code){

        LoginResponseDto loginResponseDto = oAuth2Service.socialSignIn(code);

        return new BaseResponse(loginResponseDto);
    }




}