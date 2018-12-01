package com.piotrek.diet.user;

import com.piotrek.diet.cart.Cart;
import com.piotrek.diet.cart.CartService;
import com.piotrek.diet.cloud.CloudStorageService;
import com.piotrek.diet.exceptions.BadRequestException;
import com.piotrek.diet.exceptions.NotFoundException;
import com.piotrek.diet.helpers.Page;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.piotrek.diet.helpers.Constants.IMAGE_CONTAINER_MEALS;
import static com.piotrek.diet.helpers.Constants.IMAGE_CONTAINER_PRODUCTS;

@Component
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;
    private final ProductService productService;
    private final MealService mealService;
    private final MealDtoConverter mealDtoConverter;
    private final TokenService tokenService;
    private final CartService cartService;
    private final CloudStorageService imageStorage;

    @PreAuthorize("#userId.equals(principal)")
    Mono<ProductDto> createProduct(String userId, ProductDto productDto) {
        return userService.findById(userId)
                .doOnSuccess(user -> {
                    String mealImageUrl = imageStorage.uploadImageBlob(IMAGE_CONTAINER_PRODUCTS, productDto.getId(), productDto.getImageToSave());
                    productDto.setImageUrl(mealImageUrl);
                })
                .map(user -> productDto)
                .flatMap(productService::save);
    }

    @PreAuthorize("#userId.equals(principal)")
    Mono<MealDto> createMeal(String userId, MealDto mealDto) {
        return userService.findById(userId)
                .doOnSuccess(user -> {
                    String mealImageUrl = imageStorage.uploadImageBlob(IMAGE_CONTAINER_MEALS, mealDto.getId(), mealDto.getImageToSave());
                    mealDto.setImageUrl(mealImageUrl);
                })
                .flatMap(user -> mealService.save(mealDto))
                .map(mealDtoConverter::toDto);

    }

    Mono<Page<ProductDto>> findAllProductsByUserId(String userId, Pageable pageable) {
        return userService.findById(userId)
                .then(productService.findAllByUserPageable(userId, pageable));
    }

    Mono<Page<MealDto>> findAllMealsByUser(String userId, Pageable pageable) {
        return userService.findById(userId)
                .then(mealService.findAllByUserId(userId, pageable));
    }

    Mono<UserDto> findDtoUser(String userId) {
        return userService.findDtoById(userId);
    }

    @PreAuthorize("#userId.equals(principal)")
    Mono<UserDto> updateUser(String userId, UserDto update) {
        UserDto userDto = userService.update(userId, update).block();

        String tokenValue = tokenService.generateToken(userDto);
        tokenService.update(tokenValue, tokenService.findByUserId(userId).block().getId()).block();
        try {
            Cart cart = cartService.findByUserIdAndDate(userDto.getId(), LocalDate.now()).block();
            cart.setTargetUserCalories(userDto.getCaloriesPerDay());
            cart.setTargetUserProtein(userDto.getProteinPerDay());
            cart.setTargetUserCarbohydrate(userDto.getCarbohydratePerDay());
            cart.setTargetUserFat(userDto.getFatPerDay());
            cartService.save(cart).block();
        } catch (NotFoundException e) {
        } finally {
            return Mono.just(userDto);
        }
    }

    Mono<Token> findToken(String userId) {
        return tokenService.findByUserId(userId);
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

    @PreAuthorize("#userId.equals(principal)")
    Mono<Void> addToFavourite(String userId, String mealId) {
        return userService.findById(userId)
                .flatMap(user -> mealService.findById(mealId)
                        .flatMap(meal -> user.getFavouriteMeals().contains(meal) ? Mono.error(new BadRequestException("bad request")) : Mono.just(meal))
                        .doOnSuccess(meal -> user.getFavouriteMeals().add(meal))
                        .doOnSuccess(meal -> meal.getFavouriteCounter().getAndIncrement())
                        .flatMap(mealService::save)
                        .flatMap(meal -> userService.save(user)))

                .then();
    }

    @PreAuthorize("#userId.equals(principal)")
    Mono<Void> deleteFromFavourite(String userId, String mealId) {
        return userService.findById(userId)
                .flatMap(user -> user.getFavouriteMeals().contains(new Meal(mealId)) ? Mono.just(user) : Mono.error(new BadRequestException("bad request")))
                .doOnSuccess(user -> user.getFavouriteMeals().remove(new Meal(mealId)))
                .flatMap(userService::save)
                .flatMap(user -> mealService.findById(mealId))
                .doOnSuccess(meal -> meal.getFavouriteCounter().decrementAndGet())
                .flatMap(mealService::save)
                .onErrorResume(error -> error instanceof BadRequestException ? Mono.error(error) : Mono.empty())
                .then();
    }

    Mono<MealDto> findMealDtoById(String id) {
        return mealService.findById(id).map(mealDtoConverter::toDto);
    }

    @PreAuthorize("#userId.equals(principal)")
    Mono<Boolean> isFavourite(String userId, String mealId) {
        return userService.findById(userId)
                .map(user -> user.getFavouriteMeals().contains(new Meal(mealId)));
    }

}
