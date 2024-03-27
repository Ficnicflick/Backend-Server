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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "OAuth Login", description = "OAuth Login API" )
public class OAuthLoginController {
    private final OAuth2Service oAuth2Service;

    /**
     * 프론트엔드 측에서 앱 설정 rest api key와 redirect-url 인자와 함께 카카오 로그인 인가 코드 API를 요청
     * -> 프론트엔드 측에 redirect-url로 code와 함께 get 요청으로 전달 -> 카카오 로그인 성공(카카오에서 code 파라미터 제공)
     * 사실 카카오 로그인 자체는 성공했지만 서비스(무인 돗자리 서비스) 서버에서 인증이 되지 않았다. 또한 서버에서는 해당 사용자의 정보를 모르므로
     * 카카오 토큰 받기 API를 통해 사용자 정보와 카카오 토큰을 받고 서비스 자체 토큰(jwt)를 통해 인증과 인가를 관리한다.
     * 참고로 카카오 토큰은 카카오 API를 사용할 때, 필요하다고 한다.
     *
     * 해당 API는 프론트엔드가 제공한 code와 함께 해당 서비스에서 토큰(jwt) 발급과 사용자 정보를 db에 저장하기 위한 API 이다.
     * 카카오 로그인 API 구성 : 카카오 토큰 발급 API + 카카오 사용자 정보 불러오기 API -> jwt 발급
     *
     */

    // http://localhost:3000/login/oauth2/callback/kakao
    @PostMapping("/login/oauth2/code/kakao") // 카카오 로그인 API
    @Operation(summary = "사용자 로그인/회원가입", description = "인가 코드를 통해 사용자 저장 / 토큰 발급 API")
    @Parameters(value = {
            @Parameter(name = "code", description = "카카오 인가 코드", required = true)
    })

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = {@Content( schema = @Schema(implementation = BaseResponse.class, description = "카카오 로그인 성공"))}
            )}
    )
    public BaseResponse<LoginResponseDto> loginKakao(@RequestParam(value = "code",required = true) String code){

        LoginResponseDto loginResponseDto = oAuth2Service.socialSignIn(code);

        return new BaseResponse(loginResponseDto);
    }




}
