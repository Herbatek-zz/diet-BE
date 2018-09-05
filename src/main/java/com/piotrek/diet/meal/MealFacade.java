package com.piotrek.diet.meal;

import com.piotrek.diet.helpers.PageSupport;
import com.piotrek.diet.user.UserService;
import com.piotrek.diet.user.UserValidation;
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
    private final UserValidation userValidation;

    public Mono<MealDto> createMeal(String userId, MealDto mealDto) {
        userValidation.validateUserWithPrincipal(userId);
        userService.findById(userId).block();
        var meal = mealDtoConverter.fromDto(mealDto);
        meal.setUserId(userId);

        return mealService.save(meal).map(mealDtoConverter::toDto);
    }

    public Mono<PageSupport<MealDto>> findAllByUserId(String userId, Pageable pageable) {
        userService.findById(userId).block();

        return mealService
                .findAllByUserId(userId)
                .collectList()
                .map(list -> new PageSupport<>(
                        list
                                .stream()
                                .skip(pageable.getPageNumber() * pageable.getPageSize())
                                .limit(pageable.getPageSize())
                                .map(mealDtoConverter::toDto)
                                .collect(Collectors.toList()),
                        pageable.getPageNumber(), pageable.getPageSize(), list.size()));
    }
}
