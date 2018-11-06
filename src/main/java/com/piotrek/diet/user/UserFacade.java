package com.piotrek.diet.user;

import com.piotrek.diet.cart.Cart;
import com.piotrek.diet.cart.CartService;
import com.piotrek.diet.helpers.Page;
import com.piotrek.diet.helpers.exceptions.NotFoundException;
import com.piotrek.diet.meal.Meal;
import com.piotrek.diet.meal.MealDto;
import com.piotrek.diet.meal.MealDtoConverter;
import com.piotrek.diet.meal.MealService;
import com.piotrek.diet.product.ProductDto;
import com.piotrek.diet.product.ProductService;
import com.piotrek.diet.security.token.Token;
import com.piotrek.diet.security.token.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;
    private final UserValidation userValidation;
    private final ProductService productService;
    private final MealService mealService;
    private final MealDtoConverter mealDtoConverter;
    private final TokenService tokenService;
    private final CartService cartService;

    Mono<UserDto> findDtoUser(String userId) {
        return userService.findDtoById(userId);
    }

    Mono<UserDto> updateUser(String userId, UserDto update) {
        UserDto userDto = userService.update(userId, update).block();

        String tokenValue = tokenService.generateToken(userDto);
        tokenService.update(tokenValue, tokenService.findByUserId(userId).block().getId()).block();

        Cart cart = null;
        try {
            cart = cartService.findByUserIdAndDate(userDto.getId(), LocalDate.now()).block();
        } catch (NotFoundException e) { }
        if (cart != null) {
            cart.setTargetUserCalories(userDto.getCaloriesPerDay());
            cartService.save(cart).block();
        }

        return Mono.just(userDto);
    }

    Mono<Token> findToken(String userId) {
        return tokenService.findByUserId(userId);
    }

    Mono<ProductDto> createProduct(String userId, ProductDto productDto) {
        userValidation.validateUserWithPrincipal(userId);
        return userService.findById(userId)
                .doOnNext(user -> productDto.setUserId(user.getId()))
                .map(user -> productDto)
                .flatMap(productService::save);
    }

    Mono<Page<ProductDto>> findAllProductsByUserId(String userId, Pageable pageable) {
        return userService.findById(userId)
                .then(productService
                        .findAllByUserPageable(userId, pageable));
    }

    Mono<MealDto> createMeal(String userId, MealDto mealDto) {
        userValidation.validateUserWithPrincipal(userId);
        return userService.findById(userId)
                .doOnNext(user -> mealDto.setUserId(user.getId()))
                .flatMap(user -> mealService.save(mealDto))
                .map(mealDtoConverter::toDto);
    }

    Mono<Page<MealDto>> findAllMealsByUser(String userId, Pageable pageable) {
        return userService.findById(userId).then(mealService
                .findAllByUserId(userId, pageable));
    }


    Mono<Page<MealDto>> findFavouriteMeals(String userId, Pageable pageable) {
        return userService.findById(userId)
                .map(User::getFavouriteMeals)
                .flatMap(meals -> Mono.just(new Page<>(meals
                        .stream()
                        .skip(pageable.getPageNumber() * pageable.getPageSize())
                        .limit(pageable.getPageSize())
                        .map(mealDtoConverter::toDto)
                        .collect(Collectors.toList()), pageable.getPageNumber(), pageable.getPageSize(), meals.size())));
    }

    Mono<Void> addToFavourite(String userId, String mealId) {
        userValidation.validateUserWithPrincipal(userId);
        return userService.findById(userId)
                .flatMap(user -> mealService.findById(mealId)
                        .doOnNext(meal -> user.getFavouriteMeals().add(meal))
                        .flatMap(meal -> userService.save(user)))
                .then();
    }

    Mono<Void> deleteFromFavourite(String userId, String mealId) {
        userValidation.validateUserWithPrincipal(userId);
        return userService.findById(userId)
                .doOnNext(user -> user.getFavouriteMeals().remove(new Meal(mealId)))
                .flatMap(userService::save)
                .then();
    }

    Mono<MealDto> findMealDtoById(String id) {
        return mealService.findById(id).map(mealDtoConverter::toDto);
    }

    Mono<Boolean> isFavourite(String userId, String mealId) {
        userValidation.validateUserWithPrincipal(userId);
        return userService.findById(userId)
                .map(user -> user.getFavouriteMeals().contains(new Meal(mealId)));
    }

}
