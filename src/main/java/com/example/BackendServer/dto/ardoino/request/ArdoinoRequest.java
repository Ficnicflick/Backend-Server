package com.example.BackendServer.dto.ardoino.request;

import lombok.Builder;
import lombok.Getter;


@Getter
public class ArdoinoRequest {
    private Long matId;
    private String change;

    @Builder
    public ArdoinoRequest(Long matId, String change) {
        this.matId = matId;
        this.change = change;
    }
}
