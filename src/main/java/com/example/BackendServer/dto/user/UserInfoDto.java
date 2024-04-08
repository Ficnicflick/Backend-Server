package com.example.BackendServer.dto.user;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoDto {
    private String name;
    private String email;
    private int warning_cnt;
    private double echoRate;      // 탄소저감률
}
