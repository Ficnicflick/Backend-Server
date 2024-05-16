package com.example.BackendServer.entity;

import com.example.BackendServer.common.entity.BaseEntity;
import com.example.BackendServer.entity.mat.Mat;
import com.example.BackendServer.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "History")
public class History extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime startedTime;

    @Column(nullable = false)
    private LocalDateTime returnedTime;

    @Column(nullable = false)
    private int cnt;

    // 돗자리 반납 여부
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "mat_id")
    private Mat mat;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "pay_id")
    private Pay pay;

    @Getter
    @RequiredArgsConstructor
    public enum Status {
        RETURNED,
        LATE_RETURNED,
        NOT_RETURNED;
        public static Status getStatus(String text){

            return text == null ? null : Status.valueOf(Status.class, text);
        }
    }


    @Builder
    public History(LocalDateTime startedTime, LocalDateTime returnedTime, int cnt, Status status, User user, Pay pay, Mat mat) {
        this.startedTime = startedTime;
        this.returnedTime = returnedTime;
        this.cnt = cnt;
        this.status = status;
        this.user = user;
        this.pay = pay;
        this.mat = mat;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setReturnedTime(LocalDateTime returnedTime) {
        this.returnedTime = returnedTime;
    }
}
