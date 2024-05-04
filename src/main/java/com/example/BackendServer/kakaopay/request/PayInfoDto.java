package com.example.BackendServer.kakaopay.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PayInfoDto {
    @NotNull private Long matId;
    @NotNull private int totalAmount;
}
