package com.personalproject.userservice.repository;

import com.personalproject.userservice.model.Conformation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConformationRepository extends JpaRepository<Conformation, Long> {
    Conformation findByToken(String token);
}
