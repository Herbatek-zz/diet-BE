package com.piotrek.diet.meal;

import com.piotrek.diet.helpers.Page;
import com.piotrek.diet.product.Product;
import com.piotrek.diet.product.ProductDto;
import com.piotrek.diet.product.ProductDtoConverter;
import com.piotrek.diet.user.User;
import com.piotrek.diet.user.UserService;
import com.piotrek.diet.user.UserValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MealFacade {

    private final MealService mealService;
    private final UserService userService;
    private final MealDtoConverter mealDtoConverter;
    private final ProductDtoConverter productDtoConverter;
    private final UserValidation userValidation;

    public Mono<MealDto> createMeal(String userId, MealDto mealDto) {
        userValidation.validateUserWithPrincipal(userId);
        userService.findById(userId).block();
        var meal = mealDtoConverter.fromDto(mealDto);
        meal.setUserId(userId);

        return mealService.save(meal).map(mealDtoConverter::toDto);
    }

    public Mono<Page<MealDto>> findAllByUserId(String userId, Pageable pageable) {
        userService.findById(userId).block();

        return mealService
                .findAllByUserId(userId)
                .collectList()
                .map(list -> new Page<>(
                        list
                                .stream()
                                .skip(pageable.getPageNumber() * pageable.getPageSize())
                                .limit(pageable.getPageSize())
                                .map(mealDtoConverter::toDto)
                                .collect(Collectors.toList()),
                        pageable.getPageNumber(), pageable.getPageSize(), list.size()));
    }

    public Mono<Page<MealDto>> findFavouriteMeals(String userId, Pageable pageable) {
        ArrayList<String> favouriteMealListId = userService.findById(userId).block().getFavouriteMeals();

        List<MealDto> collect = favouriteMealListId
                .stream()
                .skip(pageable.getPageNumber() * pageable.getPageSize())
                .limit(pageable.getPageSize())
                .map(mealService::findById)
                .map(Mono::block)
                .map(mealDtoConverter::toDto)
                .collect(Collectors.toList());

        return Mono.just(new Page<>(collect, pageable.getPageNumber(), pageable.getPageSize(), favouriteMealListId.size()));
    }

    Mono<MealDto> addProductsToMeal(String productId, List<ProductDto> productDtoList) {
        Meal meal = mealService.findById(productId).block();
        userValidation.validateUserWithPrincipal(meal.getUserId());

        meal.setProducts(productDtoConverter.listFromDto(productDtoList));

        updateMealInfoAfterAddProducts(meal);

        return mealService.save(meal).map(mealDtoConverter::toDto);
    }

    public Mono<Void> addToFavourite(String userId, String mealId) {
        var user = userService.findById(userId).block();
        userValidation.validateUserWithPrincipal(user.getId());

        var meal = mealService.findById(mealId).block();

        var favouriteMeals = user.getFavouriteMeals();
        favouriteMeals.add(meal.getId());
        user.setFavouriteMeals(favouriteMeals);

        userService.save(user);
        return Mono.empty();
    }

    public Mono<Void> deleteFromFavourite(String userId, String mealId) {
        var user = userService.findById(userId).block();
        userValidation.validateUserWithPrincipal(user.getId());

        user.getFavouriteMeals().remove(mealId);

        userService.save(user);
        return Mono.empty();
    }

    void updateMealInfoAfterAddProducts(Meal meal) {
        countProteinFromProducts(meal);
        countCarbohydrateFromProducts(meal);
        countFatFromProducts(meal);
        countFibreFromProducts(meal);
        countCarbohydrateExchangeFromProducts(meal);
        countProteinAndFatEquivalentFromProducts(meal);
        countKcalFromProducts(meal);
    }

    private void countProteinFromProducts(Meal meal) {
        double protein = 0;

        for (Product product : meal.getProducts())
            protein += product.getProtein();

        meal.setProtein(protein);
    }

    private void countFibreFromProducts(Meal meal) {
        double fibre = 0;

        for (Product product : meal.getProducts())
            fibre += product.getFibre();

        meal.setFibre(fibre);
    }

    private void countFatFromProducts(Meal meal) {
        double fat = 0;

        for (Product product : meal.getProducts())
            fat += product.getFat();

        meal.setFat(fat);
    }

    private void countCarbohydrateFromProducts(Meal meal) {
        double carbohydrate = 0;

        for (Product product : meal.getProducts())
            carbohydrate += product.getCarbohydrate();

        meal.setCarbohydrate(carbohydrate);
    }

    private void countProteinAndFatEquivalentFromProducts(Meal meal) {
        double proteinAndFatEquivalent = 0;

        for (Product product : meal.getProducts())
            proteinAndFatEquivalent += product.getProteinAndFatEquivalent();

        meal.setProteinAndFatEquivalent(proteinAndFatEquivalent);
    }

    private void countCarbohydrateExchangeFromProducts(Meal meal) {
        double carbohydrateExchange = 0;

        for (Product product : meal.getProducts())
            carbohydrateExchange += product.getCarbohydrateExchange();

        meal.setCarbohydrateExchange(carbohydrateExchange);
    }

    private void countKcalFromProducts(Meal meal) {
        double kcal = 0;

        for (Product product : meal.getProducts())
            kcal += product.getKcal();

        meal.setKcal(kcal);
    }
}
