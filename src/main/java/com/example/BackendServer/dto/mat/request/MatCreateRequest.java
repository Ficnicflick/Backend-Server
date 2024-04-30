package com.example.BackendServer.dto.mat.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MatCreateRequest {


    @Min(value = 1, message = "가격은 양수여야 합니다.")
    private int price;

    private double logitude;
    private double lantitude;

}
