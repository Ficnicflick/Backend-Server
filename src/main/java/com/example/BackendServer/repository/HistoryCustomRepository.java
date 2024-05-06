package com.example.BackendServer.repository;

import com.example.BackendServer.entity.History;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HistoryCustomRepository{
    Page<History> searchHistoryBy(History.Status status, Pageable pageable);
}
