package com.example.BackendServer.dto.history.response;

import com.example.BackendServer.entity.History;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class HistoryResponse {

    private int pageNumber;
    private int totalPages;
    private long totalCount;
    private List<DetailsHistoryDto> historyList;

    @Builder
    public HistoryResponse(int pageNumber, int totalPages, long totalCount, List<DetailsHistoryDto> historyList) {
        this.pageNumber = pageNumber;
        this.totalPages = totalPages;
        this.totalCount = totalCount;
        this.historyList = historyList;
    }
}
