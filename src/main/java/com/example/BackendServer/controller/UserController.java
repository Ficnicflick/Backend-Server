package com.example.BackendServer.controller;

import com.example.BackendServer.common.CurrentUser;
import com.example.BackendServer.common.exception.BaseException;
import com.example.BackendServer.common.response.BaseResponseStatus;
import com.example.BackendServer.dto.oauth2.TokenInfoResponseDto;
import com.example.BackendServer.common.response.BaseResponse;
import com.example.BackendServer.dto.user.UserHistory;
import com.example.BackendServer.dto.user.UserInfoDto;
import com.example.BackendServer.entity.user.Provider;
import com.example.BackendServer.entity.user.User;
import com.example.BackendServer.repository.UserRepository;
import com.example.BackendServer.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

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

        log.info("socialId = {}", socialId);
        User user = userRepository.findBySocialIdAndProvider(socialId, Provider.KAKAO)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NON_EXIST_USER));

        System.out.println("good job");
        return new BaseResponse<>(user);
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
