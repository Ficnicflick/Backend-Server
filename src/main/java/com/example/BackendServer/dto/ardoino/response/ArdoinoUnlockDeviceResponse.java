package com.example.BackendServer.dto.ardoino.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
public class ArdoinoUnlockDeviceResponse {
    private String deviceId;

    @Builder
    public ArdoinoUnlockDeviceResponse(String deviceId) {
        this.deviceId = deviceId;
    }
}

