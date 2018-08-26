package com.piotrek.diet.user;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, String> {

    Mono<User> findByFacebookId(Long facebookId);

    Mono<User> findByEmail(String email);

}
