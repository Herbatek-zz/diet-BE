package com.piotrek.diet.security.token;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface TokenRepository extends ReactiveMongoRepository<Token, String> {

    Mono<Token> findByToken(String token);

    Mono<Token> findByUserId(String userId);
}
