package com.example.BackendServer.service;

import com.example.BackendServer.common.exception.BaseException;
import com.example.BackendServer.dto.oauth2.LoginResponseDto;
import com.example.BackendServer.dto.oauth2.TokenInfoResponseDto;
import com.example.BackendServer.common.entity.RefreshToken;
import com.example.BackendServer.common.repository.RefreshTokenRepository;
import com.example.BackendServer.dto.KakaoProfile;
import com.example.BackendServer.dto.OAuth2Request;
import com.example.BackendServer.dto.oauth2.OAuth2AccessToken;
import com.example.BackendServer.util.JwtProvider;
import com.example.BackendServer.entity.user.Provider;
import com.example.BackendServer.entity.user.User;
import com.example.BackendServer.repository.UserRepository;
import com.example.BackendServer.util.OAuthRequestFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static com.example.BackendServer.common.response.BaseResponseStatus.EXTERNAL_SERVER_ERROR;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OAuth2Service {

    private final OAuthRequestFactory oAuthRequestFactory;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final WebClient webClient;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public LoginResponseDto socialSignIn(String code){
        // TODO: 2024-03-13 : kakao 이외 소셜 로그인이 추가됐을 때, 리팩토링 필요( 소셜 로그인 메소드를 통합하거나 각 소셜마다 로그인 서비스 로직으로 변경
        log.info("카카오 토큰 API");
        OAuth2AccessToken token = getAccessToken(code, "kakao"); // 외부 API(카카오 토큰 불러오기)
        log.info("카카오 사용자 정보 API");
        KakaoProfile profile = getProfile(token.getAccess_token(), "kakao"); // 외부 API(카카오 사용자 정보 가져오기)

        User user = hasPreviousLogin(profile, Provider.KAKAO); // 사용자 회원가입 or 로그인 처리

        // security를 사용해 Authentication 객체 생성 
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(profile.id, profile.id + "_" + Provider.KAKAO.getText());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        log.info("authentication.getName() = {}", authentication.getName());
        
        TokenInfoResponseDto tokenInfoResponse = jwtProvider.generateToken(authentication); // jwt 발급
        saveRefreshToken(authentication, tokenInfoResponse); // jwt refresh token 저장

        return  LoginResponseDto.builder()
                .name(user.getNickname())
                .tokenInfoResponseDto(tokenInfoResponse)
                .build();

    }

    private void saveRefreshToken(Authentication authentication, TokenInfoResponseDto tokenInfoResponse) {
        String rft = tokenInfoResponse.getRefreshToken(); // refreshToken 저장
        RefreshToken refreshToken  = RefreshToken.of(authentication.getName(), rft, "false");
        refreshTokenRepository.save(refreshToken);
        Iterable<RefreshToken> all = refreshTokenRepository.findAll();
        all.forEach( r -> log.info("r.getRefreshToken() = {}", r.getRefreshToken()));
    }

    // 저장으로 인해 DB에 영향을 미침.
    public User hasPreviousLogin(KakaoProfile profile, Provider provider) {
        Optional<User> findUser = userRepository.findBySocialIdAndProvider(profile.id, provider);

        if(findUser.isEmpty()){ //회원 정보 저장하기
            User createdUser = User.toEntity(profile,provider);
            createdUser.encodePassword(passwordEncoder);
            userRepository.save(createdUser);
            findUser = Optional.of(createdUser);
        }
        return findUser.get();
    }

    public OAuth2AccessToken getAccessToken(String code, String provider) throws BaseException {
        OAuth2Request oAuthToken = oAuthRequestFactory.getOAuthToken(code, provider); // OAuth API 요청에 필요한 요청 url과 필요한 인자 담기

        Mono<OAuth2AccessToken> result = webClient.mutate().build() // WebClientConfig 설정에 이어 webclient로 외부 HTTP 요청 설정
                .post()
                .uri(oAuthToken.getUrl())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(oAuthToken.getMap()))
                .retrieve()
                .bodyToMono(OAuth2AccessToken.class);

        OAuth2AccessToken block = result.block();// 동기 처리
        if(block == null){
            throw new BaseException(EXTERNAL_SERVER_ERROR);
        }
        return block;
    }

    // TODO: 2024-03-13 : 마찬가지로 2개 이상의 소셜 로그인 기능이 필요한 경우, 형식이 달라질 수 있음.
    public KakaoProfile getProfile(String accessToken, String provider){ 
        String profileUrl = oAuthRequestFactory.getProfileUrl(provider);

        Mono<KakaoProfile> result = webClient.mutate().build()
                .post()
                .uri(profileUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(KakaoProfile.class);
        KakaoProfile block = result.block();
        log.info("사용자 정보 불러오기 서비스 무사히 종료");
        if(block == null){
            throw new BaseException(EXTERNAL_SERVER_ERROR);
        }

        return block;

    }

}
