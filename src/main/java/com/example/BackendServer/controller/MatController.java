package com.example.BackendServer.controller;

import com.example.BackendServer.common.response.BaseResponse;
import com.example.BackendServer.dto.mat.request.MatCreateRequest;
import com.example.BackendServer.dto.mat.response.MatCreateResponse;
import com.example.BackendServer.service.MatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mat")
@RequiredArgsConstructor
public class MatController {
    private final MatService matService;

    @PostMapping("/admin")
    public BaseResponse<MatCreateResponse> createMat(@Valid @RequestBody MatCreateRequest dto){

        return new BaseResponse<>(matService.addMat(dto));
    }

    @PatchMapping("/{id}/admin")
    public BaseResponse<MatCreateResponse> modifyMat(@PathVariable("id") Long id){

        return new BaseResponse<>(matService.changeMat(id));
    }

    @DeleteMapping("/{id}/admin")
    public BaseResponse<?> deleteMat(@PathVariable("id") Long id){
        matService.removeMat(id);
        return new BaseResponse<>(true, HttpStatus.NO_CONTENT.value(), "mat delete success");
    }
}
