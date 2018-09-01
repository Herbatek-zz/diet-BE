package com.piotrek.diet.user;

import com.piotrek.diet.helpers.exceptions.BadRequestException;
import com.piotrek.diet.helpers.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    public Mono<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
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

    public void validateUserWithPrincipal(String userId) {
        String principalId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        if (!userId.equals(principalId))
            throw new BadRequestException("You cannot save the product, because your id does not match with id from your token");
    }
}
