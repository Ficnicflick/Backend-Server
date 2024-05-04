package com.example.BackendServer.kakaopay.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class RefundDto {
    @NotNull private long matId;
}
