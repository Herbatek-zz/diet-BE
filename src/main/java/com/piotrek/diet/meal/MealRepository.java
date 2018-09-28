package com.piotrek.diet.meal;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface MealRepository extends ReactiveMongoRepository<Meal, String> {

    Flux<Meal> findAllByUserId(String userId);

    Flux<Meal> findAllByNameIgnoreCaseContaining(String name);
}
