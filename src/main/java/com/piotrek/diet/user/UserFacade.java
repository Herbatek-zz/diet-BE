package com.piotrek.diet.user;

import com.piotrek.diet.cart.Cart;
import com.piotrek.diet.cart.CartDto;
import com.piotrek.diet.cart.CartDtoConverter;
import com.piotrek.diet.cart.CartService;
import com.piotrek.diet.helpers.Page;
import com.piotrek.diet.meal.MealDto;
import com.piotrek.diet.meal.MealDtoConverter;
import com.piotrek.diet.meal.MealService;
import com.piotrek.diet.product.ProductDto;
import com.piotrek.diet.product.ProductDtoConverter;
import com.piotrek.diet.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@Component
@RequiredArgsConstructor
public class UserFacade {

    private final CartService cartService;
    private final CartDtoConverter cartDtoConverter;
    private final UserService userService;
    private final UserValidation userValidation;
    private final ProductDtoConverter productDtoConverter;
    private final ProductService productService;
    private final MealService mealService;
    private final MealDtoConverter mealDtoConverter;

    Mono<CartDto> findCart(String userId, LocalDate date) {
        return cartService.findByUserIdAndDate(userId, date)
                .switchIfEmpty(cartService.save(new Cart(userId, date)))
                .map(cartDtoConverter::toDto);

    }

    Mono<ProductDto> createProduct(String userId, ProductDto productDto) {
        userValidation.validateUserWithPrincipal(userId);
        var user = userService.findById(userId).block();
        var product = productDtoConverter.fromDto(productDto);
        product.setUserId(user.getId());

        return productService.save(product).map(productDtoConverter::toDto);
    }

    Mono<Page<ProductDto>> findAllProductsByUserId(String userId, Pageable pageable) {
        return userService.findById(userId)
                .then(productService
                        .findAllByUserId(userId)
                        .collectList()
                        .map(list -> new Page<>(
                                list
                                        .stream()
                                        .skip(pageable.getPageNumber() * pageable.getPageSize())
                                        .limit(pageable.getPageSize())
                                        .map(productDtoConverter::toDto)
                                        .collect(Collectors.toList()),
                                pageable.getPageNumber(), pageable.getPageSize(), list.size())));
    }

    Mono<MealDto> createMeal(String userId, MealDto mealDto) {
        userValidation.validateUserWithPrincipal(userId);
        userService.findById(userId).block();

        mealDto.setUserId(userId);

        return mealService
                .save(mealDto)
                .map(mealDtoConverter::toDto);
    }

    Mono<Page<MealDto>> findAllMealsByUser(String userId, Pageable pageable) {
        return userService.findById(userId).then(mealService
                .findAllByUserId(userId)
                .collectList()
                .map(list -> new Page<>(
                        list
                                .stream()
                                .skip(pageable.getPageNumber() * pageable.getPageSize())
                                .limit(pageable.getPageSize())
                                .map(mealDtoConverter::toDto)
                                .collect(Collectors.toList()),
                        pageable.getPageNumber(), pageable.getPageSize(), list.size())));
    }


    Mono<Page<MealDto>> findFavouriteMeals(String userId, Pageable pageable) {
        var favouriteMealListId = requireNonNull(userService.findById(userId).block()).getFavouriteMeals();

        var collect = favouriteMealListId
                .stream()
                .skip(pageable.getPageNumber() * pageable.getPageSize())
                .limit(pageable.getPageSize())
                .map(mealService::findById)
                .map(Mono::block)
                .map(mealDtoConverter::toDto)
                .collect(Collectors.toList());

        return Mono.just(new Page<>(collect, pageable.getPageNumber(), pageable.getPageSize(), favouriteMealListId.size()));
    }

    Mono<Void> addToFavourite(String userId, String mealId) {
        userValidation.validateUserWithPrincipal(userId);

        var user = userService.findById(userId).block();
        var meal = mealService.findById(mealId).block();

        user.getFavouriteMeals().add(meal.getId());

        return userService.save(user).then();
    }

    Mono<Void> deleteFromFavourite(String userId, String mealId) {
        userValidation.validateUserWithPrincipal(userId);

        return userService.findById(userId)
                .doOnNext(user -> requireNonNull(user).getFavouriteMeals().remove(mealId))
                .flatMap(userService::save)
                .then();
    }

    Mono<MealDto> findMealDtoById(String id) {
        return mealService.findById(id)
                .map(mealDtoConverter::toDto);
    }

    Mono<Boolean> isFavourite(String userId, String mealId) {
        userValidation.validateUserWithPrincipal(userId);
        return userService.findById(userId)
                .flatMap(user -> Mono.just(requireNonNull(user).getFavouriteMeals().contains(mealId)));
    }
}
