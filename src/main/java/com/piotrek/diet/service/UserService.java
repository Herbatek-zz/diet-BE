package com.piotrek.diet.service;

import com.piotrek.diet.exception.NotFoundException;
import com.piotrek.diet.model.User;
import com.piotrek.diet.model.enumeration.Role;
import com.piotrek.diet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Mono<User> findById(String id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Not found user [id = " + id + "]"))));
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

    public Mono<Void> deleteAll() {
        userRepository.deleteAll();
        return Mono.empty();
    }

    public User createUser(Long facebookId, String firstAndLastName[]) {
        String firstName = firstAndLastName[0];
        String lastName = firstAndLastName[1];

        User user = new User();
        user.setFacebookId(facebookId);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(Role.ROLE_USER.name());
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

}
