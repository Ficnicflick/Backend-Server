package com.example.BackendServer.kakaopay;

import com.example.BackendServer.common.exception.BaseException;
import com.example.BackendServer.common.response.BaseResponseStatus;
import com.example.BackendServer.entity.Pay;
import com.example.BackendServer.kakaopay.request.PayInfoDto;
import com.example.BackendServer.kakaopay.request.RefundDto;
import com.example.BackendServer.kakaopay.response.KakaoApproveResponse;
import com.example.BackendServer.kakaopay.response.KakaoCancelResponse;
import com.example.BackendServer.kakaopay.response.KakaoReadyResponse;
import com.example.BackendServer.repository.PayRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class KakaoPayService {
    static final String cid = "TC0ONETIME";

    @Value("${admin.key}")
    private String adminKey;

    private KakaoReadyResponse kakaoReadyResponse;
    private final PayRepository payRepository;


    /**
     * 결제 준비
     */
    public KakaoReadyResponse kakaoPayReady(PayInfoDto payInfoDto) throws BaseException {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("cid", cid);
        parameters.add("partner_order_id", "가맹점 주문 번호");
        parameters.add("partner_user_id", "가맹점 회원 ID");
        parameters.add("item_name", "PicnicFlick");
        parameters.add("quantity", String.valueOf(payInfoDto.getQuantity()));
        parameters.add("total_amount", String.valueOf(payInfoDto.getTotal_amount()));
        parameters.add("tax_free_amount", "0");
        parameters.add("approval_url", "http://localhost:8080/payment/success");
        parameters.add("fail_url", "http://localhost:8080/payment/fail");
        parameters.add("cancel_url", "http://localhost:8080/payment/cancel");


        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        try {
            kakaoReadyResponse = restTemplate.postForObject(
                "https://kapi.kakao.com/v1/payment/ready",
                    requestEntity,
                    KakaoReadyResponse.class
            );
        } catch (RestClientException e) {
            throw e;
        }

        return kakaoReadyResponse;
    }

    /**
     * 결제 완료 승인
     */
    public KakaoApproveResponse ApproveResponse(String pgToken) throws BaseException {
        // 카카오 요청
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("cid", cid);
        parameters.add("tid", kakaoReadyResponse.getTid());
        parameters.add("partner_order_id", "가맹점 주문 번호");
        parameters.add("partner_user_id", "가맹점 회원 ID");
        parameters.add("pg_token", pgToken);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

        RestTemplate restTemplate = new RestTemplate();

        KakaoApproveResponse kakaoApproveResponse = restTemplate.postForObject(
                "https://kapi.kakao.com/v1/payment/approve",
//                "https://open-api.kakaopay.com/online/v1/payment/approve",
                requestEntity,
                KakaoApproveResponse.class
        );


        Pay pay = Pay.builder()
                .tid(kakaoReadyResponse.getTid())
                .item_name(kakaoApproveResponse.getItem_name())
                .quantity(kakaoApproveResponse.getQuantity())
                .total(kakaoApproveResponse.getAmount().getTotal())
                .rent(0)
                .deposit(0)     // 대여한 순간은 환불된 금액이 없으므로 0
                .build();

        try {
            payRepository.save(pay);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_INSERT_ERROR);
        }

        return kakaoApproveResponse;
    }


    /**
     * 결제 환불
     * 지정한 금액만큼 환불
     */
    public KakaoCancelResponse kakaoCancel(RefundDto refundDto) throws BaseException {
        // 카카오페이 요청
        /**
         * todo: refundDto 받음
         * refundDto: String tid, int cancel_amount
         */

        Optional<Pay> optional = payRepository.findByTid(refundDto.getTid());
        if (optional.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NON_EXIST_PAYMENT);
        }

        Pay requestPay = optional.get();
        if (requestPay.getTotal() < refundDto.getCancel_amount()) {
            throw new BaseException(BaseResponseStatus.WRONG_CANCEL_PAYMENT);
        }


        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("cid", cid);
        parameters.add("tid", requestPay.getTid());     // 결제 고유 번호
        parameters.add("cancel_amount",String.valueOf(refundDto.getCancel_amount()));        // 환불, 취소 금액
        parameters.add("cancel_tax_free_amount", "0");  // 취소 비과세 금액

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

        // 외부에 보낼 url
        RestTemplate restTemplate = new RestTemplate();

        KakaoCancelResponse kakaoCancelResponse = null;
        try {
            kakaoCancelResponse = restTemplate.postForObject(
                    "https://kapi.kakao.com/v1/payment/cancel",
                    requestEntity,
                    KakaoCancelResponse.class
            );
        } catch (RestClientException e) {
            throw e;
        }

        // 변경 내역 db에 저장
        try {
            requestPay.setRent(requestPay.getTotal() - refundDto.getCancel_amount());
            requestPay.setDeposit(refundDto.getCancel_amount());        // 취소 금액 == 환불 금액 == 보증금

            payRepository.save(requestPay);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_INSERT_ERROR);
        }

        return kakaoCancelResponse;
    }


    /**
     * 구 카카오페이 헤더
     * admin_key
     * @return
     */
    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();

        String auth = "KakaoAK " + adminKey;

        headers.set("Authorization", auth);
        headers.set("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        return headers;
    }
}

