package com.personalproject.userservice.service.impl;

import com.personalproject.userservice.model.Conformation;
import com.personalproject.userservice.model.User;
import com.personalproject.userservice.repository.ConformationRepository;
import com.personalproject.userservice.repository.UserRepository;
import com.personalproject.userservice.service.EmailService;
import com.personalproject.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ConformationRepository conformationRepository;
    private final EmailService emailService;
    @Override
    public User saveUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        user.setEnabled(false);
        userRepository.save(user);

        Conformation conformation = new Conformation(user);
        conformationRepository.save(conformation);

        emailService.sendSimpleMailMessage(user.getName(), user.getEmail(), conformation.getToken());

        return user;
    }

    @Override
    public Boolean verifyToken(String token) {
        Conformation conformation = conformationRepository.findByToken(token);
        User user = userRepository.findByEmailIgnoreCase(conformation.getUser().getEmail());
        user.setEnabled(true);
        userRepository.save(user);
        conformationRepository.delete(conformation);
        return true;
    }
}
