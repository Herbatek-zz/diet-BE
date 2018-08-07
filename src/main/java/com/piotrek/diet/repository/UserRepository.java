package com.piotrek.diet.repository;

import com.piotrek.diet.model.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface UserRepository extends ReactiveMongoRepository<User, String> {

    Mono<User> findByFacebookId(Long facebookId);

}
