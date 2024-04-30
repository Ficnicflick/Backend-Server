package com.example.BackendServer.repository;

import com.example.BackendServer.entity.mat.Mat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatRepository extends JpaRepository<Mat, Long> {
}
