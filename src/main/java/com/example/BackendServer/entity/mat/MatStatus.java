package com.example.BackendServer.entity.mat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MatStatus {
    AVAILABLE("사용 가능"),
    ACTIVE("사용 중"),
    UNAVAILABLE("사용 불가");

    private final String value;
}
