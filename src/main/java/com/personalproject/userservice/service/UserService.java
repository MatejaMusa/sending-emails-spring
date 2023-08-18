package com.personalproject.userservice.service;

import com.personalproject.userservice.model.User;

public interface UserService {
    User saveUser(User user);
    Boolean verifyToken(String token);
}
