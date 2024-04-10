package com.example.BackendServer.entity;

import com.example.BackendServer.common.entity.BaseEntity;
import com.example.BackendServer.entity.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "History")
public class History extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime started_time;

    @Column(nullable = false)
    private LocalDateTime returned_time;

    @Column(nullable = false)
    private int cnt;

    // 돗자리 반납 여부
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "pay_id")
    private Pay pay;

    public enum Status {
        RETURNED,
        NOT_RETURNED
    }

    @Builder
    public History(LocalDateTime started_time, LocalDateTime returned_time, int cnt, Status status, User user, Pay pay) {
        this.started_time = started_time;
        this.returned_time = returned_time;
        this.cnt = cnt;
        this.status = status;
        this.user = user;
        this.pay = pay;
    }

    public void setUser(User user) {
        this.user = user;
    }
}