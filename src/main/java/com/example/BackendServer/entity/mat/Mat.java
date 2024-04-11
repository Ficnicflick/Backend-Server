package com.example.BackendServer.entity.mat;

import com.example.BackendServer.common.entity.BaseEntity;
import com.example.BackendServer.entity.History;
import jakarta.persistence.*;

@Entity
public class Mat extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mat_id")
    private Long id;

    private int price;

    @Embedded
    private MatCheck matCheck;

    @Enumerated(EnumType.STRING)
    private Place place;

    @OneToOne
    private History history;




}
