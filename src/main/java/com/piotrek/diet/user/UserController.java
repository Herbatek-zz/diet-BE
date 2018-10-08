package com.piotrek.diet.user;

import com.piotrek.diet.helpers.Page;
import com.piotrek.diet.meal.MealDto;
import com.piotrek.diet.meal.MealFacade;
import com.piotrek.diet.product.ProductDto;
import com.piotrek.diet.product.ProductFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static com.piotrek.diet.helpers.Page.DEFAULT_PAGE_SIZE;
import static com.piotrek.diet.helpers.Page.FIRST_PAGE_NUM;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserDtoConverter userDtoConverter;
    private final ProductFacade productFacade;
    private final MealFacade mealFacade;

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    Mono<UserDto> findUserById(@PathVariable String id) {
        return userService.findById(id)
                .map(userDtoConverter::toDto);
    }

    @GetMapping("/{id}/products")
    @ResponseStatus(OK)
    Mono<Page<ProductDto>> findUserProducts(
            @PathVariable String id,
            @RequestParam(defaultValue = FIRST_PAGE_NUM) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size) {
        return productFacade.findAllByUserId(id, PageRequest.of(page, size));
    }

    @PostMapping("/{id}/products")
    @ResponseStatus(CREATED)
    Mono<ProductDto> createProduct(@PathVariable String id, @Valid @RequestBody ProductDto productDto) {
        return productFacade.createProduct(id, productDto);
    }

    @GetMapping("/{id}/meals")
    @ResponseStatus(OK)
    Mono<Page<MealDto>> findUserMeals(
            @PathVariable String id,
            @RequestParam(defaultValue = FIRST_PAGE_NUM) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size) {
        return mealFacade.findAllByUserId(id, PageRequest.of(page, size));
    }

    @GetMapping("/{id}/favourite/meals")
    @ResponseStatus(OK)
    Mono<Page<MealDto>> findFavouriteMeals(
            @PathVariable String id,
            @RequestParam(defaultValue = FIRST_PAGE_NUM) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size) {
        return mealFacade.findFavouriteMeals(id, PageRequest.of(page, size));
    }

    @PostMapping("/{userId}/favourite/meals/{mealId}")
    @ResponseStatus(CREATED)
    Mono<Void> addMealToFavourite(@PathVariable String userId, @PathVariable String mealId) {
        return mealFacade.addToFavourite(userId, mealId);
    }

    @DeleteMapping("/{userId}/favourite/meals/{mealId}")
    @ResponseStatus(NO_CONTENT)
    Mono<Void> deleteMealFromFavourite(@PathVariable String userId, @PathVariable String mealId) {
        return mealFacade.deleteFromFavourite(userId, mealId);
    }

    @PostMapping("/{id}/meals")
    @ResponseStatus(CREATED)
    Mono<MealDto> createMeal(@PathVariable String id, @Valid @RequestBody MealDto mealDto) {
        return mealFacade.createMeal(id, mealDto);
    }

    @GetMapping("/{userId}/meals/{mealId}")
    @ResponseStatus(OK)
    Mono<Boolean> isMealFavourite(@PathVariable String userId, @PathVariable String mealId) {
        return mealFacade.isFavourite(userId, mealId);
    }
}
