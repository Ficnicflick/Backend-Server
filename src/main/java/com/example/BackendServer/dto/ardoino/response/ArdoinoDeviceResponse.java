package com.example.BackendServer.dto.ardoino.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ArdoinoDeviceResponse {
    private String message;

    @Builder
    public ArdoinoDeviceResponse(String message) {
        this.message = message;
    }
}

