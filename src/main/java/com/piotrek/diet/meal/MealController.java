package com.piotrek.diet.meal;

import com.piotrek.diet.helpers.PageSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.piotrek.diet.helpers.PageSupport.DEFAULT_PAGE_SIZE;
import static com.piotrek.diet.helpers.PageSupport.FIRST_PAGE_NUM;
import static org.springframework.http.HttpStatus.NO_CONTENT;
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

    @GetMapping
    @ResponseStatus(OK)
    Mono<PageSupport<MealDto>> findAll(
            @RequestParam(defaultValue = FIRST_PAGE_NUM) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size) {
        return mealService.findAllPageable(PageRequest.of(page, size));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    Mono<Void> deleteById(@PathVariable String id) {
        return mealService.deleteById(id);
    }
}
