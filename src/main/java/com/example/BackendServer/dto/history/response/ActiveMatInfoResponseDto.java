package com.example.BackendServer.dto.history.response;

import com.example.BackendServer.entity.mat.Place;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Builder
public class ActiveMatInfoResponseDto {
    private Long remainingTime;             // 남은 시간
    private LocalDateTime endTime;          // 반납 종료 시간
    private String place;
}
