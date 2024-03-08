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

    @Builder
    public Pay(String tid, String item_name, int quantity, int total) {
        this.tid = tid;
        this.item_name = item_name;
        this.quantity = quantity;
        this.total = total;
    }

    // todo: user와 연결
}
