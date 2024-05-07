package com.example.BackendServer.dto.user;

import com.example.BackendServer.entity.History;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
public class UserHistoryDto {
    private Long history_id;
    private LocalDateTime rentDay;
    private LocalTime startTime;
    private LocalTime returnTime;
    private String location;
    private int rentCnt;
    private History.Status status;
    private int price;


    public UserHistoryDto() { }

    public UserHistoryDto(Long history_id, LocalDateTime rentDay, LocalTime startTime, LocalTime returnTime,String location, int rentCnt, History.Status status, int price) {
        this.history_id = history_id;
        this.rentDay = rentDay;
        this.startTime = startTime;
        this.returnTime = returnTime;
        this.location = location;
        this.rentCnt = rentCnt;
        this.status = status;
        this.price = price;
    }

    public static UserHistoryDto HistoryEntityToHistoryRes(History history) {
        return new UserHistoryDto(
                history.getId(),
                history.getStarted_time(),
                history.getStarted_time().toLocalTime(),
                history.getReturned_time().toLocalTime(),
                history.getMat().getPlace().getLocation(),
                history.getCnt(),
                history.getStatus(),
                history.getPay().getTotal()     // todo: rent? total?
        );
    }
}
