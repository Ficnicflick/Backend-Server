package com.example.BackendServer.controller;

import com.example.BackendServer.common.exception.BaseException;
import com.example.BackendServer.dto.oauth2.TokenInfoResponseDto;
import com.example.BackendServer.common.response.BaseResponse;
import com.example.BackendServer.dto.user.UserHistory;
import com.example.BackendServer.dto.user.UserInfoDto;
import com.example.BackendServer.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Tag(name = "User", description = "User API")
public class UserController {
    private final UserService userService;

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "로그아웃을 하면 해당 사용자와 매칭되는 refresh token은  순기능을 잃는다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = {@Content( schema = @Schema(implementation = BaseResponse.class, description = "로그아웃 성공"))}
            )}
    )
    public BaseResponse<?> userLogout(@RequestHeader("Refresh") String refreshToken){ // act(인증, 인가) / rft(로그아웃 표시를 위해 블랙리스트 저장)

        userService.logout(refreshToken);

        return new BaseResponse<>("로그아웃 성공");
    }

    @PostMapping("/token/reissue")
    @Operation(summary = "토큰 재발급", description = "refresh token으로 access token을 재발급한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = {@Content( schema = @Schema(implementation = BaseResponse.class, description = "로그아웃 성공"))}
            )}
    )
    public BaseResponse<TokenInfoResponseDto> userTokenReissue(@RequestHeader(name = "Authorization") String token){

        return new BaseResponse(userService.reissue(token));

    }
    
    @GetMapping("/ping")
    @Operation(summary = "테스트 API", description = "JWT 없이 요청이 가능하다. 반환되면 서버가 정상적으로 실행 중 이다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = {@Content( schema = @Schema(implementation = BaseResponse.class, description = "API 호출 테스트 성공"))}
            )}
    )
    private BaseResponse<?> ping(){
        System.out.println("good job");
        return new BaseResponse<>("성공");
    }


    // 마이페이지
    @GetMapping("/mypage")
    public BaseResponse<UserInfoDto> getUserInfo(Principal principal) {
        try {
            UserInfoDto userInfoDto = userService.getUserInfo(principal);
            return new BaseResponse<>(userInfoDto);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    // 이용내역
    @GetMapping("/history")
    public BaseResponse<List<UserHistory>> getUserHistory(Principal principal) {
        try {
            List<UserHistory> historyList = userService.getUserHistory(principal);
            return new BaseResponse<>(historyList);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
