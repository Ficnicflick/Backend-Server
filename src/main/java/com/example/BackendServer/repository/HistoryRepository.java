package com.example.BackendServer.repository;

import com.example.BackendServer.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {

    @Query("select h from History h join fetch h.pay left join fetch h.mat join fetch h.user where h.id = :historyId")
    Optional<History> findHistoryWithMatAndPayAndUser(@Param("historyId") Long historyId);
}
