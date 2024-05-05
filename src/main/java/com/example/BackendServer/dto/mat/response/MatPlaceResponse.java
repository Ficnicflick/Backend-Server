package com.example.BackendServer.dto.mat.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MatPlaceResponse {

    private int availableCount;
    private int totalCount;
    private List<Long> matIdList;

    @Builder
    public MatPlaceResponse(int availableCount, int totalCount, List<Long> matIdList) {
        this.availableCount = availableCount;
        this.totalCount = totalCount;
        this.matIdList = matIdList;
    }
}
