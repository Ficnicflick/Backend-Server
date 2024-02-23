package com.example.BackendServer.domain;

import com.example.BackendServer.common.auth.dto.TokenInfoResponse;
import com.example.BackendServer.common.auth.service.JwtTokenProvider;
import com.example.BackendServer.common.exception.BaseException;
import com.example.BackendServer.common.response.BaseResponseStatus;
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

import static com.example.BackendServer.common.response.BaseResponseStatus.*;

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

            return jwtTokenProvider.generateToken(authentication);

        }catch (UsernameNotFoundException ex){
            throw new BaseException(NON_EXIST_USER);
        }catch (BadCredentialsException ex){
            throw new BaseException(WRONG_PASSWORD);
        }


    }
}
