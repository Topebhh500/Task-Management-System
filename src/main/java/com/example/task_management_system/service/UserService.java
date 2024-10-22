package com.example.task_management_system.service;

import com.example.task_management_system.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(User user);
    Optional<User> getUserById(Long id);
    Optional<User> getUserByEmail(String email);
    Optional<User> getUserByGoogleId(String googleId);
    List<User> getAllUsers();
    User updateUser(User user);
    void deleteUser(Long id);
}