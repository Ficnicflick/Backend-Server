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
@RedisHash(value = "jwtToken", timeToLive = 24 * 7 * 60 * 60)
public class RefreshToken implements Serializable {

    @Id
    private String id;

    @Indexed
    private String refreshToken; // 권한 필드가 추후 필요하면 추가

    @Indexed
    private String isLougout;

    public static RefreshToken of(String key, String refreshToken, String isLougout){
        return RefreshToken.builder()
                .id(key)
                .refreshToken(refreshToken)
                .isLougout(isLougout)
                .build();
    }
    public RefreshToken updateLogoutStatus(RefreshToken refreshToken){
        refreshToken.isLougout = "true";
        return refreshToken;
    }

}
