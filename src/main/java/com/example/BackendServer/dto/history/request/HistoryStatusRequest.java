package com.example.BackendServer.dto.history.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class HistoryStatusRequest {

    private String status;
}

