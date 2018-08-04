package com.piotrek.diet.service;

import com.piotrek.diet.model.User;
import com.piotrek.diet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}
