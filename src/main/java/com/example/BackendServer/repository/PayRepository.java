package com.example.BackendServer.repository;

import com.example.BackendServer.entity.Pay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PayRepository extends JpaRepository<Pay, Long> {

    Optional<Pay> findByTid(String tid);
}
