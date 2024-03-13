package com.example.BackendServer.util;

import com.example.BackendServer.dto.KakaoInfo;
import com.example.BackendServer.dto.OAuth2Request;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;

@Component
@RequiredArgsConstructor
public class OAuthRequestFactory {

    private final KakaoInfo kakaoInfo;
    //private final GoogleReq googleReq;
    //private final NaverReq naverReq;
    public OAuth2Request getOAuthToken(String code, String provider){
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        if(provider.equals("kakao")){
            map.add("grant_type", kakaoInfo.getGrantType()); //카카오 공식문서 기준 authorization_code 로 고정
            map.add("client_id", kakaoInfo.getKakaoClientId()); // 카카오 Dev 앱 REST API 키
            map.add("redirect_uri", kakaoInfo.getKakaoRedirect()); // 카카오 Dev redirect uri
            map.add("code", code); // 프론트에서 인가 코드 요청시 받은 인가 코드값
            map.add("client_secret", kakaoInfo.getClientSecret()); // 카카오 Dev 카카오 로그인 Client Secret


            return new OAuth2Request("https://kauth.kakao.com/oauth/token",map);
        }

       /* else if(provider.equals("google")){
            map.add("grant_type", "authorization_code");
            map.add("client_id", googleReq.getGoogleClientId());
            map.add("client_secret", googleReq.getGoogleClientSecret());
            map.add("redirect_uri", "http://localhost:3000/login/oauth2/callback/google");
            map.add("code", code);

            return new OAuth2Request("https://oauth2.googleapis.com/token",map);
        }

        *//** 네이버 *//*
        else{
            map.add("grant_type", "authorization_code");
            map.add("client_id", naverReq.getNaverClientId());
            map.add("client_secret", naverReq.getNaverClientSecret());
            map.add("redirect_uri", naverReq.getNaverRedirect());
            map.add("code", code);
            map.add("state", "project");

            return new OAuth2Request("https://nid.naver.com/oauth2.0/token", map);
        }*/


        return null;
    }

    public String getProfileUrl(String provider) {
        if (provider.equals("kakao")) {
            return kakaoInfo.getUserInfoUrl();
        } else if(provider.equals("google")) {
            return "https://www.googleapis.com/oauth2/v3/userinfo";
        } else {
            return "https://openapi.naver.com/v1/nid/me";
        }
    }

}