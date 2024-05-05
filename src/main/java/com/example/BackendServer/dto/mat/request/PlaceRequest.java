package com.example.BackendServer.dto.mat.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PlaceRequest {
    @NotNull(message = "latitude 필수입니다.")
    private Double latitude;
    @NotNull(message = "longitude 필수입니다.")
    private Double longitude;

}
