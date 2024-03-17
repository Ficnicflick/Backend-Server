package com.example.BackendServer.controller;

import com.example.BackendServer.dto.token.TokenInfoResponse;
import com.example.BackendServer.common.response.BaseResponse;
import com.example.BackendServer.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Tag(name = "User", description = "User API")
public class UserController {
    private final UserService userService;

    /*@PostMapping("/sign-up")
    @Operation(summary = "사용자 회원가입", description = "회원가입을 통해 새로운 사용자를 서비스에 저장한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공",
            content = {@Content( schema = @Schema(implementation = BaseResponse.class, description = "sign-up success"))}
            )}
    )
    public BaseResponse<String> userSignUp(@Valid @RequestBody UserSignUpRequestDto dto){

        return new BaseResponse(userService.signUp(dto));
    }
    @PostMapping("/sign-in")
    @Operation(summary = "사용자 로그인", description = "아이디와 비밀번호를 통해 로그인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = {@Content( schema = @Schema(implementation = BaseResponse.class, description = "sign-up success"))})
    })
    public BaseResponse<TokenInfoResponse> userSignIn(@Valid @RequestBody UserSignInRequestDto dto){

        return new BaseResponse(userService.signIn(dto));
    }
*/
    @PostMapping("/logout")
    public BaseResponse<?> userLogout(@RequestHeader("Refresh") String refreshToken){ // act(인증, 인가) / rft(로그아웃 표시를 위해 블랙리스트 저장)
        /**
         * 1. rft을 body, header 중 무엇으로 받을까
         */
        userService.logout(refreshToken);


        return new BaseResponse<>("로그아웃 성공");
    }

    @PostMapping("/token/reissue")
    public BaseResponse<TokenInfoResponse> userTokenReissue(@RequestHeader(name = "Authorization") String token/*, @RequestHeader(name = "isRefreshToken") String isRefreshToken*/){
        /*if(!isRefreshToken.equals("true")){
            //예외 처리 필요
        }*/

        return new BaseResponse(userService.reissue(token));

    }
    @GetMapping("/ping")
    private BaseResponse<?> ping(){
        System.out.println("good job");
        return new BaseResponse<>("성공");
    }

}
