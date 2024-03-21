package com.example.BackendServer.fliter;

import com.example.BackendServer.util.JwtProvider;
import com.example.BackendServer.common.response.BaseResponse;
import com.example.BackendServer.common.response.BaseResponseStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.example.BackendServer.common.response.BaseResponseStatus.*;

@Component @Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TYPE = "Bearer";
    private final JwtProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. Request Header 에서 JWT 토큰 추출
        String token = resolveToken(request);
        log.info("token = {}", token);
        try {
            if (token != null && jwtTokenProvider.validateToken(token)) { // 2. validateToken 으로 토큰 유효성 검사
                // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext 에 저장
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("해당 토큰은 유효하므로 SecurityContext에 저장");

            }

            filterChain.doFilter(request, response);
            log.info("토큰 검증 성공");
        }catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
            setErrorResponse(response, INVALID_TOKEN);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
            setErrorResponse(response, EXPRIED_TOKEN);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
            setErrorResponse(response, UNSURPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
            setErrorResponse(response, EMPTY_TOKEN_CLAIM);
        }


        }
    // Request Header 에서 토큰 정보 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void setErrorResponse(
            HttpServletResponse response,
            BaseResponseStatus status
    ){
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(status.getCode());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        BaseResponse result = new BaseResponse(status);
        //BaseException<Object> result = ApiResponse.of(status, message, null);
        try{
            response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(result));
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}

