package com.example.BackendServer.dto.history.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class HistorySimpleDto {
    private Long historyId;
    private String location;
    private LocalDateTime started_time; // 대여 시간
    private LocalDateTime returned_time; // 반납 예정 시간

    @Builder
    public HistorySimpleDto(Long historyId, String location, LocalDateTime started_time, LocalDateTime returned_time) {
        this.historyId = historyId;
        this.location = location;
        this.started_time = started_time;
        this.returned_time = returned_time;
    }

}
