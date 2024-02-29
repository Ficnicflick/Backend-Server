package com.example.BackendServer.common.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "jwtToken", timeToLive = /*60 * 60 * 24 * 7*/ 30)
public class RefreshToken implements Serializable {

    @Id
    private String id;

    @Indexed
    private String refreshToken; // 권한 필드가 추후 필요하면 추가

    private String isSocailLogin;

    private String isLougout;

    public static RefreshToken of(String key, String refreshToken, String isSocailLogin, String isLougout){
        return RefreshToken.builder()
                .id(key)
                .refreshToken(refreshToken)
                .isSocailLogin(isSocailLogin)
                .isLougout(isLougout)
                .build();
    }
}
