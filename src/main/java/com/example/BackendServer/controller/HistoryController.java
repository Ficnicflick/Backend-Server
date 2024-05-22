package com.example.BackendServer.controller;

import com.example.BackendServer.common.CurrentUser;
import com.example.BackendServer.common.exception.BaseException;
import com.example.BackendServer.common.response.BaseResponse;
import com.example.BackendServer.dto.history.request.HistoryStatusRequest;
import com.example.BackendServer.dto.history.response.ActiveMatInfoResponseDto;
import com.example.BackendServer.dto.history.response.DetailsHistoryDto;
import com.example.BackendServer.dto.history.response.HistoryResponse;
import com.example.BackendServer.dto.history.response.HistorySimpleDto;
import com.example.BackendServer.dto.user.UserHistoryDto;
import com.example.BackendServer.entity.History;
import com.example.BackendServer.service.HistoryService;
import com.example.BackendServer.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/history")
public class    HistoryController {
    private final HistoryService historyService;

    @GetMapping("/{id}")
    public BaseResponse<DetailsHistoryDto> findDetailHistory(@PathVariable(name = "id") Long id, @CurrentUser String socialId ){

        return new BaseResponse<>(historyService.getDetailsHistory(id, socialId));
    }

    // 이용내역
    @GetMapping("/log")
    public BaseResponse<List<UserHistoryDto>> getUserHistory(@CurrentUser String socialId) {
        try {
            List<UserHistoryDto> historyList = historyService.getUserHistory(socialId);
            return new BaseResponse<>(historyList);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    // 관리자 이용내역
    @GetMapping("/admin")
    public BaseResponse<HistoryResponse> findHistoryAllBy(@RequestParam(required = false) String status
            , @RequestParam(defaultValue = "0", name = "pageNumber", required = false) int pageNumber, @CurrentUser String socialId) {

        return new BaseResponse<>(historyService.getHistoryByCategory(status, pageNumber, socialId));
    }

    @GetMapping("/recent")
    public BaseResponse<History.Status> recentHistory(@CurrentUser String socialId) {
        try {
            History.Status status = historyService.recentMatStatus(socialId);
            return new BaseResponse<>(status);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @GetMapping("/active-mat")
    public BaseResponse<ActiveMatInfoResponseDto> activeMatInfo(@CurrentUser String socialId) {
        try {
            ActiveMatInfoResponseDto activeMatInfoResponseDto = historyService.activeMatInfo(socialId);
            return new BaseResponse<>(activeMatInfoResponseDto);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}

