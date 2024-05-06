package com.example.BackendServer.service;

import com.example.BackendServer.common.exception.BaseException;
import com.example.BackendServer.common.response.BaseResponseStatus;
import com.example.BackendServer.entity.History;
import com.example.BackendServer.entity.Pay;
import com.example.BackendServer.entity.mat.Mat;
import com.example.BackendServer.entity.mat.MatStatus;
import com.example.BackendServer.entity.user.User;
import com.example.BackendServer.kakaopay.request.PayInfoDto;
import com.example.BackendServer.kakaopay.request.RefundDto;
import com.example.BackendServer.kakaopay.response.KakaoApproveResponse;
import com.example.BackendServer.kakaopay.response.KakaoCancelResponse;
import com.example.BackendServer.kakaopay.response.KakaoReadyResponse;
import com.example.BackendServer.repository.HistoryRepository;
import com.example.BackendServer.repository.MatRepository;
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

import java.time.Duration;
import java.time.LocalDateTime;
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
    private final MatRepository matRepository;

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

        Optional<Mat> optionalMat = matRepository.findById(payInfoDto.getMatId());
        if (optionalMat.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NOT_EXIST_MAT);
        }
        
        // 사용 중인 돗자리는 대여 불가
        Mat mat = optionalMat.get();
        if (mat.getMatCheck().getMatStatus() == MatStatus.ACTIVE) {
            throw new BaseException(BaseResponseStatus.WRONG_MAT);
        }


        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("cid", cid);
        parameters.add("partner_order_id", "가맹점 주문 번호");
        parameters.add("partner_user_id", "가맹점 회원 ID");
        parameters.add("item_name", "PicnicFlick");
        parameters.add("quantity", String.valueOf(1));
        parameters.add("total_amount", String.valueOf(payInfoDto.getTotalAmount()));
        parameters.add("tax_free_amount", "0");
        parameters.add("approval_url", serverUrl +"/api/v1/payment/success" + "/" + socialId + '/' + payInfoDto.getMatId());      // user, mat 식별
        parameters.add("fail_url", serverUrl + "/api/v1/payment/fail");
        parameters.add("cancel_url", serverUrl + "/api/v1/payment/cancel");


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
    public KakaoApproveResponse ApproveResponse(String pgToken, String socialId, Long matId) throws BaseException {
        Optional<User> optional = userRepository.findBySocialId(socialId);
        if (optional.isEmpty()) {
            throw  new BaseException(BaseResponseStatus.NON_EXIST_USER);
        }
        User user = optional.get();

        Optional<Mat> optionalMat = matRepository.findById(matId);
        if (optionalMat.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NOT_EXIST_MAT);
        }


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
                requestEntity,
                KakaoApproveResponse.class
        );

        // 돗자리 상태 변경
        Mat mat = optionalMat.get();
        mat.getMatCheck().countPlus();
        mat.getMatCheck().changeMatStatus(MatStatus.ACTIVE);

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
         */
        History history = History.builder()
                .started_time(kakaoApproveResponse.getApproved_at())
                .returned_time(kakaoApproveResponse.getApproved_at())       // 일단 생성되면 returnedTime은 대여 시간과 동일하게 설정
                .cnt(kakaoApproveResponse.getQuantity())
                .status(History.Status.NOT_RETURNED)      // 지금 대여했으니까 not return
                .user(user)
                .pay(pay)
                .mat(mat)
                .build();
        user.addHistory(history);

        try {
            payRepository.save(pay);
            historyRepository.save(history);
            matRepository.save(mat);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_INSERT_ERROR);
        }

        return kakaoApproveResponse;
    }

    /**
     * 결제 환불
     */
    public KakaoCancelResponse kakaoCancel(RefundDto refundDto, String socialId) throws BaseException {
        Optional<Mat> optionalMat = matRepository.findById(refundDto.getMatId());
        if (optionalMat.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NOT_EXIST_MAT);
        }
        Mat mat = optionalMat.get();

        Optional<User> optionalUser = userRepository.findBySocialId(socialId);
        if (optionalUser.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NON_EXIST_USER);
        }
        User user = optionalUser.get();

        // 사용자 정보 + mat id + 돗자리 상태
        Optional<History> optionalHistory = historyRepository.findByUserSocialIdAndMatIdAndStatus(socialId, optionalMat.get().getId(), History.Status.NOT_RETURNED);
        if (optionalHistory.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NOT_EXIST_HISTORY);
        }
        History history = optionalHistory.get();

        Optional<Pay> optionalPay = payRepository.findById(history.getPay().getId());
        if (optionalPay.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NON_EXIST_PAYMENT);
        }
        Pay requestPay = optionalPay.get();

        // 올바르지 않은 돗자리 정보로 결제 취소 시
        if (mat.getMatCheck().getMatStatus() == MatStatus.AVAILABLE || mat.getMatCheck().getMatStatus() == MatStatus.UNAVAILABLE) {
            throw new BaseException(BaseResponseStatus.NON_EXIST_PAYMENT);
        }

        Duration duration = Duration.between(requestPay.getCreatedAt(), LocalDateTime.now());
        long hours = duration.toHours();
        int cancelAmount;
        if (hours <= 6) {
            cancelAmount = 5000;
            history.setStatus(History.Status.RETURNED);                 // history 반납 완료 상태로 변경
        }
        else if (hours <= 24) {
            cancelAmount = 3000;
            history.setStatus(History.Status.LATE_RETURNED);
        }
        else {
            cancelAmount = 0;
            history.setStatus(History.Status.LATE_RETURNED);
        }

        if (cancelAmount == 0) {
            user.plusWarningCnt();
            throw new BaseException(BaseResponseStatus.RETURNED);
        }
        if (requestPay.getTotal() < cancelAmount) {
            throw new BaseException(BaseResponseStatus.WRONG_CANCEL_PAYMENT);
        }

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("cid", cid);
        parameters.add("tid", requestPay.getTid());     // 결제 고유 번호
        parameters.add("cancel_amount",String.valueOf(cancelAmount));        // 환불, 취소 금액
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

        mat.getMatCheck().changeMatStatus(MatStatus.AVAILABLE);     // 돗자리 상태 사용가능으로 변경

        double echoRate = (historyRepository.countByUserSocialIdAndStatusReturned(socialId) / historyRepository.countByUserSocialId(socialId))*100;
        user.setEchoRate(echoRate);

        requestPay.setRent(requestPay.getTotal() - cancelAmount);
        requestPay.setDeposit(cancelAmount);        // 취소 금액 == 환불 금액 == 보증금
        history.setReturned_time(LocalDateTime.now());

        try {
            historyRepository.save(history);
            payRepository.save(requestPay);
            matRepository.save(mat);
            userRepository.save(user);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_INSERT_ERROR);
        }

        return kakaoCancelResponse;
    }


    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();

        String auth = "KakaoAK " + adminKey;

        headers.set("Authorization", auth);
        headers.set("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        return headers;
    }
}

