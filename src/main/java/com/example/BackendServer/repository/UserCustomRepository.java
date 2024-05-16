package com.example.BackendServer.repository;

import com.example.BackendServer.entity.user.User;

import java.util.Optional;

public interface UserCustomRepository {
    public Optional<User> searchUserWithUsedHistories(String socialId);
}
