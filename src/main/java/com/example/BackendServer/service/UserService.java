package com.example.BackendServer.service;

import com.example.BackendServer.common.exception.BaseException;
import com.example.BackendServer.dto.token.TokenInfoResponse;
import com.example.BackendServer.util.JwtProvider;
import com.example.BackendServer.common.entity.RefreshToken;
import com.example.BackendServer.common.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;

import static com.example.BackendServer.common.response.BaseResponseStatus.*;

@Service @Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public TokenInfoResponse reissue(String refreshToken) {
        RefreshToken findRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken).orElseThrow(
                () -> new BaseException(NOT_EXIST_REFRESHTOKEN));

        if(findRefreshToken.getIsLougout().equals("true")){ // 이미 로그아웃으로 인해 블랙 토큰이므로 재발급 X
            throw new BaseException(BLACK_TOKNE_REFRESHTOKEN);
        }

        // 존재하지 않는 경우는 없음. 애초에 만료되어 사라진 토큰이라면 JwtAuthenticationFliter 단에서 미리 처리해버림.
        String token = findRefreshToken.getRefreshToken().substring(7);
        String subject = jwtProvider.extractSubject(token);
        String auth = jwtProvider.extractAuth(token);
        long now = (new Date()).getTime();
        String createdAccessToken = jwtProvider.generateAccessToken(subject, auth, now);

        return TokenInfoResponse.builder()
                .accessToken(createdAccessToken)
                .build();

    }


    @Transactional
    public void logout(String token) {
        if(!jwtProvider.validateToken(resolveToken(token))){ // 변조된 refresh Token
            throw new BaseException(INVALID_TOKEN);
        }
        RefreshToken refreshToken = jwtProvider.changeToBlackToken(token);// 저장된 refreshToken 블랙토큰으로 변경
        refreshTokenRepository.save(refreshToken); //  CrudRepository를 상속해서 diry checking이 안됨. save()로 변경 적용
    }

    private String resolveToken(String token) {
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        throw new BaseException(UNSURPPORTED_TOKEN);
    }
}
