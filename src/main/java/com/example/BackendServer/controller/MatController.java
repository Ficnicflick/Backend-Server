package com.example.BackendServer.controller;

import com.example.BackendServer.common.entity.BaseEntity;
import com.example.BackendServer.common.response.BaseResponse;
import com.example.BackendServer.service.MatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mat")
public class MatController {
    private final MatService matService;

    @PostMapping
    public BaseResponse<?> createMat(){

        return null;

    }
}
