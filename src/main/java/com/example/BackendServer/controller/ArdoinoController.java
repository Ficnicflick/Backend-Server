package com.example.BackendServer.controller;

import com.example.BackendServer.common.response.BaseResponse;
import com.example.BackendServer.dto.ardoino.response.ArdoinoUnlockDeviceResponse;
import com.example.BackendServer.service.ArdoinoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/arduino")
public class ArdoinoController {

    private final ArdoinoService ardoinoService;
    @PostMapping("/{id}")
    public BaseResponse<ArdoinoUnlockDeviceResponse> opneLock(@PathVariable(name = "id") Long id){

        return new BaseResponse<>(ardoinoService.unlockDivice(id));
    }
}
