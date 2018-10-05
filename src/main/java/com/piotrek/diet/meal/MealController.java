package com.piotrek.diet.meal;

import com.piotrek.diet.helpers.Page;
import com.piotrek.diet.product.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.piotrek.diet.helpers.Page.DEFAULT_PAGE_SIZE;
import static com.piotrek.diet.helpers.Page.FIRST_PAGE_NUM;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/meals")
@RequiredArgsConstructor
public class MealController {

    private final MealService mealService;
    private final MealFacade mealFacade;
    private final MealDtoConverter mealDtoConverter;

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    Mono<MealDto> findById(@PathVariable String id) {
        return mealService.findById(id)
                .map(mealDtoConverter::toDto);
    }

    @GetMapping("/search")
    @ResponseStatus(OK)
    Mono<Page<MealDto>> searchByName(
            @RequestParam(defaultValue = FIRST_PAGE_NUM) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = "") String query) {
        return mealService.searchByName(PageRequest.of(page, size), query);
    }

    @GetMapping
    @ResponseStatus(OK)
    Mono<Page<MealDto>> findAll(
            @RequestParam(defaultValue = FIRST_PAGE_NUM) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size) {
        return mealService.findAllPageable(PageRequest.of(page, size));
    }

    @PutMapping("/{id}")
    @ResponseStatus(OK)
    Mono<MealDto> addProducts(@PathVariable String id, @RequestBody List<ProductDto> productDtos) {
        return mealFacade.addProductsToMeal(id, productDtos);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    Mono<Void> deleteById(@PathVariable String id) {
        return mealService.deleteById(id);
    }
}
