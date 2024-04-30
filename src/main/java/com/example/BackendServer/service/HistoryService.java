package com.example.BackendServer.service;

import com.example.BackendServer.common.exception.BaseException;
import com.example.BackendServer.common.response.BaseResponse;
import com.example.BackendServer.common.response.BaseResponseStatus;
import com.example.BackendServer.dto.history.response.DetailsHistoryDto;
import com.example.BackendServer.entity.History;
import com.example.BackendServer.entity.Pay;
import com.example.BackendServer.entity.mat.Mat;
import com.example.BackendServer.entity.user.Provider;
import com.example.BackendServer.entity.user.User;
import com.example.BackendServer.repository.HistoryRepository;
import com.example.BackendServer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service @Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HistoryService {

    private final UserRepository userRepository;
    private final HistoryRepository historyRepository;


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
                .started_time(history.getStarted_time())
                .returned_time(history.getReturned_time())
                .status(history.getStatus())
                .location(mat.isPresent()? mat.get().getPlace().getLocation() : null)
                .itemName(pay.getItem_name())
                .cnt(pay.getQuantity())
                .totalPrice(pay.getTotal())
                .rentPrice(pay.getRent())
                .despositPrice(pay.getDeposit())
                .build();
    }
}
