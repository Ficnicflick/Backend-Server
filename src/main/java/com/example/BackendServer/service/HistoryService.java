package com.example.BackendServer.service;

import com.example.BackendServer.common.exception.BaseException;
import com.example.BackendServer.common.response.BaseResponse;
import com.example.BackendServer.common.response.BaseResponseStatus;
import com.example.BackendServer.dto.history.response.ActiveMatInfoResponseDto;
import com.example.BackendServer.dto.history.response.DetailsHistoryDto;
import com.example.BackendServer.dto.history.response.HistoryResponse;
import com.example.BackendServer.dto.history.response.HistorySimpleDto;
import com.example.BackendServer.dto.user.UserHistoryDto;
import com.example.BackendServer.entity.History;
import com.example.BackendServer.entity.Pay;
import com.example.BackendServer.entity.mat.Mat;
import com.example.BackendServer.entity.user.Provider;
import com.example.BackendServer.entity.user.User;
import com.example.BackendServer.repository.HistoryRepository;
import com.example.BackendServer.repository.MatRepository;
import com.example.BackendServer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.BackendServer.common.response.BaseResponseStatus.*;

@Service @Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HistoryService {

    private final UserRepository userRepository;
    private final HistoryRepository historyRepository;
    private final MatRepository matRepository;

    private final static int HISTORY_PAGE_SIZE = 10;

    public DetailsHistoryDto getDetailsHistory(Long id, String socialId) {
        User user = userRepository.findBySocialIdAndProvider(socialId, Provider.KAKAO)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NON_EXIST_USER));

        History history = historyRepository.findHistoryWithMatAndPayAndUser(id)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_EXIST_HISTORY));

        if(user.equals(history.getUser())){ // User의 정보가 같은지 확인
            new BaseException(BaseResponseStatus.USER_MISMATCH);
        }
        Optional<Mat> mat = Optional.ofNullable(history.getMat());
        Pay pay = history.getPay();

        return DetailsHistoryDto.builder()
                .started_time(history.getStartedTime())
                .returned_time(history.getReturnedTime())
                .status(history.getStatus())
                .location(mat.isPresent()? mat.get().getPlace().getLocation() : null)
                .itemName(pay.getItem_name())
                .matId(mat.isPresent()? mat.get().getId(): null)
                .cnt(pay.getQuantity())
                .totalPrice(pay.getTotal())
                .rentPrice(pay.getRent())
                .despositPrice(pay.getDeposit())
                .build();
    }

    // history (이용내역)
    public List<UserHistoryDto> getUserHistory(String socialId) throws BaseException {
        Optional<User> optional = userRepository.findBySocialId(socialId);

        if (optional.isEmpty()) {
            throw new BaseException(NON_EXIST_USER);
        }

        User user = optional.get();

        List<History> historyEntityList = user.getHistories();
        log.info("history size = {}", historyEntityList.size());

        List<UserHistoryDto> historyList = historyEntityList.stream()
                .map(UserHistoryDto::HistoryEntityToHistoryRes)
                .collect(Collectors.toList());

        if (historyList.isEmpty()) {
            return Collections.emptyList();
        }
        return historyList;
    }

    public HistoryResponse getHistoryByCategory(String state, int pageNumber, String socialId) {
        User user = userRepository.findBySocialId(socialId)
                .orElseThrow(() -> new BaseException(NON_EXIST_USER));
        
        if(!user.getRoles().contains("ROLE_ADMIN")){
            throw new BaseException(FORBBIDEN_USER_ROLR);
        }
        History.Status status = History.Status.getStatus(state);
        Page<History> histories = historyRepository.searchHistoryBy(status, PageRequest.of(pageNumber, HISTORY_PAGE_SIZE));


        return HistoryResponse.builder()
                .pageNumber(pageNumber)
                .totalPages(histories.getTotalPages())
                .totalCount(histories.getTotalElements())
                .historyList(histories.stream()
                        .map(history -> DetailsHistoryDto
                                .builder()
                                .historyId(history.getId())
                                .nickname(history.getUser().getNickname())
                                .email(history.getUser().getEmail())
                                .matId(history.getMat().getId())
                                .started_time(history.getStartedTime())
                                .returned_time(history.getReturnedTime())
                                .cnt(history.getCnt())
                                .status(history.getStatus())
                                .location(history.getMat().getPlace().getLocation())
                                .itemName(history.getPay().getItem_name())
                                .totalPrice(history.getPay().getTotal())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    public History.Status recentMatStatus(String socialId) throws BaseException {
        User user = userRepository.findBySocialId(socialId)
                .orElseThrow(() -> new BaseException(NON_EXIST_USER));

        History history = historyRepository.findTopByUserSocialIdOrderByCreatedAtDesc(socialId);
        if (history == null || history.getStatus() == History.Status.RETURNED) {
            return History.Status.RETURNED;
        }
        else {
            Duration duration = Duration.between(history.getStartedTime(), LocalDateTime.now());
            long hours = duration.toHours();
            // 대여 시간 내로 대여 중
            if (hours <= 6) return History.Status.NOT_RETURNED;
            // 지각
            else return History.Status.LATE_RETURNED;
        }
    }

    public ActiveMatInfoResponseDto activeMatInfo(String socialId) throws BaseException {
        User user = userRepository.findBySocialId(socialId)
                .orElseThrow(() -> new BaseException(NON_EXIST_USER));

        History history = historyRepository.findTopByUserSocialIdOrderByCreatedAtDesc(socialId);
        if (history == null || history.getStatus() == History.Status.RETURNED) {
            return null;
        }

        Mat mat = matRepository.findById(history.getMat().getId())
                .orElseThrow(() -> new BaseException(NOT_EXIST_MAT));

        LocalDateTime endTime = mat.getCreatedAt().plusHours(6);
        long minutesLeft = Duration.between(LocalDateTime.now(), endTime).toMinutes();

        ActiveMatInfoResponseDto activeMatInfoResponseDto = ActiveMatInfoResponseDto.builder()
                .remainingTime(minutesLeft)
                .endTime(endTime)
                .place(mat.getPlace().getLocation())
                .build();

        return activeMatInfoResponseDto;
    }
}
