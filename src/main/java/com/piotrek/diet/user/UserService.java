package com.piotrek.diet.user;

import com.piotrek.diet.helpers.exceptions.NotFoundException;
import com.piotrek.diet.helpers.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Mono<User> findById(String id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Not found user [id = " + id + "]"))));
    }

    public Mono<User> findByFacebookId(Long facebookId) {
        return userRepository.findByFacebookId(facebookId);
    }

    Flux<User> findAll() {
        return userRepository.findAll();
    }

    public Mono<User> save(User user) {
        return userRepository.save(user);
    }

    Mono<Void> deleteAll() {
        return userRepository.deleteAll();
    }

    public User createUser(Long facebookId, String email, String firstName, String lastName) {
        var user = new User();
        user.setFacebookId(facebookId);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(Role.ROLE_USER.name());
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }
}
