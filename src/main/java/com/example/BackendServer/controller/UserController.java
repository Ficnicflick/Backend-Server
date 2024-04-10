package com.example.BackendServer.controller;

import com.example.BackendServer.common.CurrentUser;
import com.example.BackendServer.common.exception.BaseException;
import com.example.BackendServer.dto.oauth2.TokenInfoResponseDto;
import com.example.BackendServer.common.response.BaseResponse;
import com.example.BackendServer.dto.user.UserHistoryDto;
import com.example.BackendServer.dto.user.UserInfoDto;
import com.example.BackendServer.repository.UserRepository;
import com.example.BackendServer.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Slf4j
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "로그아웃을 하면 해당 사용자와 매칭되는 refresh token은  순기능을 잃는다.")
    public BaseResponse<?> userLogout(@RequestHeader("Refresh") String refreshToken){ // act(인증, 인가) / rft(로그아웃 표시를 위해 블랙리스트 저장)

        userService.logout(refreshToken);

        return new BaseResponse<>("로그아웃 성공");
    }

    @PostMapping("/token/reissue")
    @Operation(summary = "토큰 재발급", description = "refresh token으로 access token을 재발급한다.")
    public BaseResponse<TokenInfoResponseDto> userTokenReissue(@RequestHeader(name = "Authorization") String token){

        return new BaseResponse(userService.reissue(token));

    }
    
    @GetMapping("/ping")
    private BaseResponse<?> ping(@CurrentUser String socialId){

        /*log.info("socialId = {}", socialId);
        User user = userRepository.findBySocialIdAndProvider(socialId, Provider.KAKAO)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NON_EXIST_USER));*/

        System.out.println("good job");
        return new BaseResponse<>("ok");
    }


    // 마이페이지
    @GetMapping("/mypage")
    public BaseResponse<UserInfoDto> getUserInfo(@CurrentUser String socialId) {
        try {
            UserInfoDto userInfoDto = userService.getUserInfo(socialId);
            return new BaseResponse<>(userInfoDto);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    // 이용내역
    @GetMapping("/history")
    public BaseResponse<List<UserHistoryDto>> getUserHistory(@CurrentUser String socialId) {
        try {
            List<UserHistoryDto> historyList = userService.getUserHistory(socialId);
            return new BaseResponse<>(historyList);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
