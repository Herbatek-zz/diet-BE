package com.piotrek.diet.meal;

import com.piotrek.diet.helpers.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static com.piotrek.diet.helpers.Page.DEFAULT_PAGE_SIZE;
import static com.piotrek.diet.helpers.Page.FIRST_PAGE_NUM;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/meals")
@RequiredArgsConstructor
public class MealController {

    private final MealService mealService;

    @GetMapping
    Mono<Page<MealDto>> findAll(
            @RequestParam(defaultValue = FIRST_PAGE_NUM) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size) {
        return mealService.findAllPageable(PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    Mono<MealDto> findById(@PathVariable String id) {
        return mealService.findDtoById(id);
    }

    @GetMapping("/search")
    Mono<Page<MealDto>> searchByName(
            @RequestParam(defaultValue = FIRST_PAGE_NUM) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = "") String query) {
        return mealService.searchByName(PageRequest.of(page, size), query);
    }

    @PutMapping("/{id}")
    Mono<MealDto> editMeal(@PathVariable String id, @Valid @RequestBody MealDto mealDto) {
        return mealService.updateMeal(id, mealDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    Mono<Void> deleteById(@PathVariable String id) {
        return mealService.deleteById(id);
    }
}
