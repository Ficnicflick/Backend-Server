package com.example.BackendServer.domain;

import com.example.BackendServer.common.auth.dto.TokenInfoResponse;
import com.example.BackendServer.common.exception.BaseException;
import com.example.BackendServer.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Tag(name = "User", description = "User API")
public class UserController {
    private final UserService userService;

    @PostMapping("/sign-up")
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
    @GetMapping("/ping")
    private BaseResponse<?> ping(){
        System.out.println("good job");
        return new BaseResponse<>("성공");
    }

}
