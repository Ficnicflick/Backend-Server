package com.example.BackendServer.entity.mat;

import com.example.BackendServer.common.entity.BaseEntity;
import com.example.BackendServer.entity.History;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mat extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mat_id")
    private Long id;

    private int price;

    @Embedded
    private MatCheck matCheck;

    @Enumerated(EnumType.STRING)
    private Place place;

    @Builder
    public Mat(int price, MatCheck matCheck, Place place) {
        this.price = price;
        this.matCheck = matCheck;
        this.place = place;
    }
}
