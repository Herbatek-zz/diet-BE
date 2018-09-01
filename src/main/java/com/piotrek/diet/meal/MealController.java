package com.piotrek.diet.meal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/meals")
@RequiredArgsConstructor
public class MealController {

    private final MealService mealService;
    private final MealDtoConverter mealDtoConverter;

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    Mono<MealDto> findById(@PathVariable String id) {
        return mealService.findById(id)
                .map(mealDtoConverter::toDto);
    }
}
