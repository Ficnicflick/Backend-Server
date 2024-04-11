package com.example.BackendServer.entity.mat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Place {
    TTUKSEOM_HAN_RIVER("뚝섬 한강", 100, 200);

    private final String location;
    private final int logitude;
    private final int lantitude;
}
