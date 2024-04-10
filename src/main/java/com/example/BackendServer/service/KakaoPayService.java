package com.example.BackendServer.service;

import com.example.BackendServer.common.exception.BaseException;
import com.example.BackendServer.common.response.BaseResponseStatus;
import com.example.BackendServer.entity.History;
import com.example.BackendServer.entity.Pay;
import com.example.BackendServer.entity.user.User;
import com.example.BackendServer.kakaopay.request.PayInfoDto;
import com.example.BackendServer.kakaopay.request.RefundDto;
import com.example.BackendServer.kakaopay.response.KakaoApproveResponse;
import com.example.BackendServer.kakaopay.response.KakaoCancelResponse;
import com.example.BackendServer.kakaopay.response.KakaoReadyResponse;
import com.example.BackendServer.repository.HistoryRepository;
import com.example.BackendServer.repository.PayRepository;
import com.example.BackendServer.repository.UserRepository;
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

    @Value("${server.url}")
    private String serverUrl;

    private KakaoReadyResponse kakaoReadyResponse;
    private final PayRepository payRepository;
    private final HistoryRepository historyRepository;
    private final UserRepository userRepository;

    private String localUrl = "http://localhost:8080";

    /**
     * 결제 준비
     */
    public KakaoReadyResponse kakaoPayReady(PayInfoDto payInfoDto, String socialId) throws BaseException {
        Optional<User> optional = userRepository.findBySocialId(socialId);
        if (optional.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NON_EXIST_USER);
        }
        User user = optional.get();

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("cid", cid);
        parameters.add("partner_order_id", "가맹점 주문 번호");
        parameters.add("partner_user_id", "가맹점 회원 ID");
        parameters.add("item_name", "PicnicFlick");
        parameters.add("quantity", String.valueOf(payInfoDto.getQuantity()));
        parameters.add("total_amount", String.valueOf(payInfoDto.getTotal_amount()));
        parameters.add("tax_free_amount", "0");
        parameters.add("approval_url", localUrl +"/payment/success" + "/" + socialId);      // user 식별 가능하게 하기 위해서
        parameters.add("fail_url", localUrl + "/payment/fail");
        parameters.add("cancel_url", localUrl + "/payment/cancel");


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
    public KakaoApproveResponse ApproveResponse(String pgToken, String socialId) throws BaseException {
        Optional<User> optional = userRepository.findBySocialId(socialId);

        if (optional.isEmpty()) {
            throw  new BaseException(BaseResponseStatus.NON_EXIST_USER);
        }
        User user = optional.get();

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

        /**
         * 생성 시 반환 시간은 결제 승인 시간과 동일하게 설정함
         * 추후에 대여 여부 확인할 때 started_time == returned_time이면 미반납
         */
        History history = History.builder()
                .started_time(kakaoApproveResponse.getApproved_at())
                .returned_time(kakaoApproveResponse.getApproved_at())       // 일단 생성되면 returnedTime은 대여 시간과 동일하게 설정
                .cnt(kakaoApproveResponse.getQuantity())
                .status(History.Status.NOT_RETURNED)      // 지금 대여했으니까 not return
                .user(user)
                .pay(pay)
                .build();

        try {
            payRepository.save(pay);
            historyRepository.save(history);
            user.addHistory(history);
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

