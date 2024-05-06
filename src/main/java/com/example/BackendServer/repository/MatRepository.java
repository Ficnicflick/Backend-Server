package com.example.BackendServer.repository;

import com.example.BackendServer.entity.mat.Mat;
import com.example.BackendServer.entity.mat.MatCheck;
import com.example.BackendServer.entity.mat.MatStatus;
import com.example.BackendServer.entity.mat.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Optional;

public interface MatRepository extends JpaRepository<Mat, Long> {
    @Query("select m from Mat m where m.place =:place and m.matCheck.matStatus = :matStatus")
    List<Mat> findAllByPlaceAndAvailableMat(@Param("place") Place place, @Param("matStatus") MatStatus matStatus);

    List<Mat> findAllByPlace(Place place);
}
