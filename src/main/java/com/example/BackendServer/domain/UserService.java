package com.example.BackendServer.domain;

import com.example.BackendServer.common.auth.dto.TokenInfoResponse;
import com.example.BackendServer.common.auth.service.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    @Transactional
    public String signUp(UserSignUpRequestDto dto){
        User user = dto.toEntity();
        user.encodePassword(passwordEncoder);
        userRepository.save(user);

        return "sign-up success";
    }

    @Transactional
    public TokenInfoResponse signIn(UserSignInRequestDto dto){
        log.info("로그인 서비스 로직 시작");
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());
        log.info("UsernamePasswordAuthenticationToken = {}", authenticationToken);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        log.info("authentication = {}", authentication);

        User user = userRepository.findByUsername(dto.getUsername()).orElseThrow(() -> new RuntimeException("존재하지 않는 사용자"));

        TokenInfoResponse tokenInfo = jwtTokenProvider.generateToken(authentication);

        return tokenInfo;
    }
}
