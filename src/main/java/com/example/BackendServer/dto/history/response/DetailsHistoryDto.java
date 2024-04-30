package com.example.BackendServer.dto.history.response;

import com.example.BackendServer.entity.History;
import com.example.BackendServer.entity.Pay;
import com.example.BackendServer.entity.mat.Mat;
import com.example.BackendServer.entity.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DetailsHistoryDto {

    private LocalDateTime started_time; // 대여 시간
    private LocalDateTime returned_time; // 반납 예정 시간
    private int cnt; // 개수
    private History.Status status; // 반납 여부 확인
    private String location; // 돗자리 장소 정보
    private String itemName; // 상품 명칭
    private int totalPrice; // 최초 총 결제 금액
    private int rentPrice; // 대여비
    private int despositPrice; // 보증금

    @Builder
    public DetailsHistoryDto(LocalDateTime started_time, LocalDateTime returned_time, int cnt, History.Status status
            , String location, String itemName, int totalPrice, int rentPrice, int despositPrice) {
        this.started_time = started_time;
        this.returned_time = returned_time;
        this.cnt = cnt;
        this.status = status;
        this.location = location;
        this.itemName = itemName;
        this.totalPrice = totalPrice;
        this.rentPrice = rentPrice;
        this.despositPrice = despositPrice;
    }
}
