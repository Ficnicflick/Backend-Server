package com.example.BackendServer.controller;

import com.example.BackendServer.common.CurrentUser;
import com.example.BackendServer.common.exception.BaseException;
import com.example.BackendServer.common.response.BaseResponse;
import com.example.BackendServer.service.KakaoPayService;
import com.example.BackendServer.kakaopay.request.PayInfoDto;
import com.example.BackendServer.kakaopay.request.RefundDto;
import com.example.BackendServer.kakaopay.response.KakaoApproveResponse;
import com.example.BackendServer.kakaopay.response.KakaoCancelResponse;
import com.example.BackendServer.kakaopay.response.KakaoReadyResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@Slf4j
public class KakaoPayController {
    private final KakaoPayService kakaoPayService;

    /**
     * 결제 요청
     */
    @PostMapping("/ready")
    public ResponseEntity<KakaoReadyResponse> readyToKakaoPay(@RequestBody PayInfoDto payInfoDto, @CurrentUser String socialId) {
        KakaoReadyResponse kakaoReadyResponse = kakaoPayService.kakaoPayReady(payInfoDto, socialId);
        return ResponseEntity.ok(kakaoReadyResponse);
    }

    /**
     * 결제 승인
     * BaseResponse<KakaoApproveResponse> -> void 리턴 타입 변경
     * todo
     */
    @GetMapping("/success/{id}/{matId}")
    public void afterPayRequest(HttpServletResponse response, @PathVariable("id")String socialId, @PathVariable("matId")Long matId, @RequestParam("pg_token") String pgToken) {
        try {
            KakaoApproveResponse kakaoApproveResponse = kakaoPayService.ApproveResponse(pgToken, socialId, matId);

            response.sendRedirect("http://localhost:3000/lental/3" + socialId);
//            return new BaseResponse<>(kakaoApproveResponse);
        } catch (BaseException e) {
            throw new BaseException(e.getStatus());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 결제 진행 중 취소
     */
    @GetMapping("/cancel")
    public BaseResponse<String> cancel() {
        return new BaseResponse<>("결제가 취소되었습니다.");
    }

    /**
     * 결제 실패
     */
    @GetMapping("/fail")
    public BaseResponse<String> fail() {
        return new BaseResponse<>("결제에 실패하였습니다.");
    }

    /**
     * 환불
     */
    @PostMapping("/refund")
    public BaseResponse<KakaoCancelResponse> refund(@RequestBody RefundDto refundDto, @CurrentUser String socialId) {
        KakaoCancelResponse kakaoCancelResponse = kakaoPayService.kakaoCancel(refundDto, socialId);
        return new BaseResponse<>(kakaoCancelResponse);
    }
}
