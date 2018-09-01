package com.piotrek.diet.meal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MealService {

    private final MealRepository mealRepository;

    Mono<Meal> findById(String id) {
        return mealRepository.findById(id);
    }

    Flux<Meal> findAllByUserId(String userId) {
        return mealRepository.findAllByUserId(userId);
    }
}
