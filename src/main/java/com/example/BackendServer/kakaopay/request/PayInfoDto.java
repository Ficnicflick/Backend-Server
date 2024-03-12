package com.example.BackendServer.kakaopay.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

@Getter
public class PayInfoDto {
    @NotNull private int quantity;
    @NotNull private int total_amount;
}
