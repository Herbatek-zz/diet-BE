package com.piotrek.diet.meal;

import com.piotrek.diet.helpers.PageSupport;
import com.piotrek.diet.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MealFacade {

    private final MealService mealService;
    private final UserService userService;
    private final MealDtoConverter mealDtoConverter;

    public Mono<MealDto> createMeal(String id, MealDto mealDto) {
        return null;
    }

    public Mono<PageSupport<MealDto>> findAllByUserId(String userId, Pageable pageable) {
        userService.findById(userId).block();

        return mealService
                .findAllByUserId(userId)
                .map(mealDtoConverter::toDto)
                .collectList()
                .map(list -> new PageSupport<>(
                        list
                                .stream()
                                .skip(pageable.getPageNumber() * pageable.getPageSize())
                                .limit(pageable.getPageSize())
                                .collect(Collectors.toList()),
                        pageable.getPageNumber(), pageable.getPageSize(), list.size()));
    }
}
