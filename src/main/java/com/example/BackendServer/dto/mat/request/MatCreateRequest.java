package com.example.BackendServer.dto.mat.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MatCreateRequest {


    @Min(value = 1, message = "가격은 양수여야 합니다.")
    private int price;
    @NotNull(message = "위치 번호는 필수입니다.")
    private Long placeId;

}
