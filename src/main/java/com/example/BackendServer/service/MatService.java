package com.example.BackendServer.service;

import com.example.BackendServer.common.exception.BaseException;
import com.example.BackendServer.common.response.BaseResponseStatus;
import com.example.BackendServer.dto.mat.request.MatCreateRequest;
import com.example.BackendServer.dto.mat.response.MatCreateResponse;
import com.example.BackendServer.entity.mat.Mat;
import com.example.BackendServer.entity.mat.MatCheck;
import com.example.BackendServer.entity.mat.MatStatus;
import com.example.BackendServer.entity.mat.Place;
import com.example.BackendServer.repository.MatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatService {
    private final MatRepository matRepository;

    /**
     * 돗자리 추가하기
     * 새로운 돗자리로 변경하기(사용횟수 초기화 등)
     */
    @Transactional
    public MatCreateResponse addMat(MatCreateRequest request){ // 돗자리 추가하기


        MatCheck matCheck = MatCheck.builder().build();
        Place place = Place.getLocation(request.getLogitude(), request.getLantitude());

        Mat mat = Mat.builder()
                .price(request.getPrice())
                .matCheck(matCheck)
                .place(place)
                .build();

        matRepository.save(mat);

        return MatCreateResponse.builder()
                .matId(mat.getId())
                .build();

    }
    @Transactional
    public void changeMat(Long id){ // 돗자리 바꾸기 -> 상태 초기화(새로운 돗자리로 기계에서 바꿈)
        Mat mat = matRepository.findById(id).orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_EXIST_MAT));
        MatCheck matCheck = mat.getMatCheck();
        matCheck.changeMatCount(0);
        matCheck.changeMatStatus(MatStatus.AVAILABLE);
    }

    @Transactional
    public void removeMat(Long id){
        Mat mat = matRepository.findById(id).orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_EXIST_MAT));
        matRepository.delete(mat);
    }
}