package com.example.BackendServer.domain;

import com.example.BackendServer.common.auth.dto.TokenInfoResponse;
import com.example.BackendServer.common.auth.service.JwtTokenProvider;
import com.example.BackendServer.common.entity.RefreshToken;
import com.example.BackendServer.common.exception.BaseException;
import com.example.BackendServer.common.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

import static com.example.BackendServer.common.response.BaseResponseStatus.*;

@Service @Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    @Transactional
    public String signUp(UserSignUpRequestDto dto){

        if(checkPassword(dto.getPassword(), dto.getCheckedPassword())){
           throw new BaseException(WRONG_PASSWORD);
        }
        if(checkDuplicateUsername(dto.getUsername())){
            throw new BaseException(EXIST_USER);
        }
        if(checkDuplicateNickname(dto.getNickname())){
            throw new BaseException(EXIST_NICKNAME);
        }

        User user = dto.toEntity();
        user.encodePassword(passwordEncoder);
        userRepository.save(user);

        return "sign-up success";
    }

    private boolean checkDuplicateNickname(String nickname) {
        return userRepository.findByNickname(nickname).isPresent();
    }

    private boolean checkDuplicateUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    private static boolean checkPassword(String password, String checkedPassword ) {
        return !password.equals(checkedPassword);
    }

    @Transactional
    public TokenInfoResponse signIn(UserSignInRequestDto dto){
        try {
            log.info("로그인 서비스 로직 시작");
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());
            log.info("UsernamePasswordAuthenticationToken = {}", authenticationToken);
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            log.info("authentication = {}", authentication);

            TokenInfoResponse response = jwtTokenProvider.generateToken(authentication);
            
            String refreshToken = response.getRefreshToken(); // refreshToken 저장
            RefreshToken token = RefreshToken.of(authentication.getName(), refreshToken, "false", "false");
            refreshTokenRepository.save(token);

            return response;

        }catch (UsernameNotFoundException ex){
            throw new BaseException(NON_EXIST_USER);
        }catch (BadCredentialsException ex){
            throw new BaseException(WRONG_PASSWORD);
        }


    }

    @Transactional
    public TokenInfoResponse reissue(String refreshToken) {
        Optional<RefreshToken> findRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken);

        // 존재하지 않는 경우는 없음. 애초에 만료되어 사라진 토큰이라면 JwtAuthenticationFliter 단에서 미리 처리해버림.
        RefreshToken result = findRefreshToken.get();
        String token = result.getRefreshToken().substring(7);
        String subject = jwtTokenProvider.extractSubject(token);
        String auth = jwtTokenProvider.extractAuth(token);
        long now = (new Date()).getTime();
        String createdAccessToken = jwtTokenProvider.generateAccessToken(subject, auth, now);

        return TokenInfoResponse.builder()
                .accessToken(createdAccessToken)
                .build();

    }
}
