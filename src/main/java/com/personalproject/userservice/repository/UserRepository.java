package com.personalproject.userservice.repository;

import com.personalproject.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmailIgnoreCase(String email);
    Boolean existsByEmail(String email);
}
