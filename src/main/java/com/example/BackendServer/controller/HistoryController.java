package com.example.BackendServer.controller;

import com.example.BackendServer.common.CurrentUser;
import com.example.BackendServer.common.exception.BaseException;
import com.example.BackendServer.common.response.BaseResponse;
import com.example.BackendServer.dto.history.response.DetailsHistoryDto;
import com.example.BackendServer.dto.user.UserHistoryDto;
import com.example.BackendServer.service.HistoryService;
import com.example.BackendServer.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/history")
public class HistoryController {
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
}
