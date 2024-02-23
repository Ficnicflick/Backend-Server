package com.example.BackendServer.domain;

import com.example.BackendServer.common.auth.dto.TokenInfoResponse;
import com.example.BackendServer.common.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/sign-up")
    public BaseResponse<String> userSignUp(@Valid @RequestBody UserSignUpRequestDto dto){

        return new BaseResponse(userService.signUp(dto));
    }
    @PostMapping("/sign-in")
    public BaseResponse<TokenInfoResponse> userSignIn(@Valid @RequestBody UserSignInRequestDto dto){

        return new BaseResponse(userService.signIn(dto));
    }
    @GetMapping("/ping")
    private BaseResponse<?> ping(){
        System.out.println("good job");
        return new BaseResponse<>("성공");
    }

}
