package com.piotrek.diet.service;

import com.piotrek.diet.model.User;
import com.piotrek.diet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Mono<User> findById(String id) {
        return userRepository.findById(id);
    }

    public Flux<User> findAll() {
        return userRepository.findAll();
    }

    public Mono<User> findByFacebookId(Long facebookId) {
        return userRepository.findByFacebookId(facebookId);
    }

    public Mono<User> save(User user) {
        return userRepository.save(user);
    }

}
