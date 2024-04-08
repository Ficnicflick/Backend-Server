package com.example.BackendServer.repository;

import com.example.BackendServer.entity.user.Provider;
import com.example.BackendServer.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findBySocialIdAndProvider(String socialId, Provider provider);
    Optional<User> findBySocialId(String socialId);

    Optional<User> findByEmail(String email);
}
