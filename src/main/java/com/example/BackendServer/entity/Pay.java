package com.example.BackendServer.entity;

import com.example.BackendServer.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "Pay")
public class Pay extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tid;

    private String item_name;

    private int quantity;

    private int total;
    
    private int rent;       // 대여비 (총 결제 금액 - 취소금액)
    
    private int deposit;        // 환불 금액 (보증금)

    @Builder
    public Pay(String tid, String item_name, int quantity, int total, int rent, int deposit) {
        this.tid = tid;
        this.item_name = item_name;
        this.quantity = quantity;
        this.total = total;
        this.rent = rent;
        this.deposit = deposit;
    }

    // todo: user와 연결
}
