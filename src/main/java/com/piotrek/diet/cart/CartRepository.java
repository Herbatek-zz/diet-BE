package com.piotrek.diet.cart;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface CartRepository extends ReactiveMongoRepository<Cart, String> {

    Mono<Cart> findByUserIdAndDate(String userId, LocalDate localDateTime);
}
