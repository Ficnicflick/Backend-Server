package com.example.BackendServer.dto.mat.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PlaceRequest {
    @NotNull(message = "위치 번호는 필수입니다.")
    private Long placeId;

}
