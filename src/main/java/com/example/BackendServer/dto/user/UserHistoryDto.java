package com.example.BackendServer.dto.user;

import com.example.BackendServer.entity.History;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
public class UserHistoryDto {
    private LocalDateTime rentDay;
    private LocalTime startTime;
    private LocalTime returnTime;
    private int rentCnt;
    private History.Status status;
    private int price;


    public UserHistoryDto() { }

    public UserHistoryDto(LocalDateTime rentDay, LocalTime startTime, LocalTime returnTime, int rentCnt, History.Status status, int price) {
        this.rentDay = rentDay;
        this.startTime = startTime;
        this.returnTime = returnTime;
        this.rentCnt = rentCnt;
        this.status = status;
        this.price = price;
    }

    public static UserHistoryDto HistoryEntityToHistoryRes(History history) {
        return new UserHistoryDto(
                history.getStarted_time(),
                history.getStarted_time().toLocalTime(),
                history.getReturned_time().toLocalTime(),
                history.getCnt(),
                history.getStatus(),
                history.getPay().getTotal()     // todo: rent? total?
        );
    }
}