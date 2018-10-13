package com.piotrek.diet.meal;

import com.piotrek.diet.helpers.Page;
import com.piotrek.diet.helpers.exceptions.NotFoundException;
import com.piotrek.diet.product.Product;
import com.piotrek.diet.product.ProductDtoConverter;
import com.piotrek.diet.user.UserValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MealService {

    private final MealRepository mealRepository;
    private final MealDtoConverter mealDtoConverter;
    private final ProductDtoConverter productDtoConverter;
    private final UserValidation userValidation;

    public Mono<Meal> findById(String id) {
        return mealRepository.findById(id)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Not found meal [id = " + id + "]"))));
    }

    Mono<MealDto> findDtoById(String id) {
        return findById(id).map(mealDtoConverter::toDto);
    }

    public Flux<Meal> findAllByUserId(String userId) {
        return mealRepository.findAllByUserId(userId);
    }

    Mono<Page<MealDto>> findAllPageable(Pageable pageable) {
        return mealRepository
                .findAll()
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

    public Mono<Meal> save(Meal meal) {
        return mealRepository.save(meal);
    }

    public Mono<Meal> save(MealDto mealDto) {
        return save(mealDtoConverter.fromDto(mealDto));
    }

    public Mono<Void> deleteAll() {
        return mealRepository.deleteAll();
    }

    Mono<Void> deleteById(String id) {
        return mealRepository.deleteById(id);
    }

    Mono<Page<MealDto>> searchByName(PageRequest pageRequest, String query) {
        return mealRepository.findAllByNameIgnoreCaseContaining(query)
                .collectList()
                .map(list -> new Page<>(
                        list
                                .stream()
                                .skip(pageRequest.getPageNumber() * pageRequest.getPageSize())
                                .limit(pageRequest.getPageSize())
                                .map(mealDtoConverter::toDto)
                                .collect(Collectors.toList()),
                        pageRequest.getPageNumber(), pageRequest.getPageSize(), list.size()));
    }

    Mono<MealDto> updateMeal(String mealId, MealDto mealDto) {
        Meal meal = findById(mealId).block();
        userValidation.validateUserWithPrincipal(meal.getUserId());

        Objects.requireNonNull(meal).setName(mealDto.getName());
        meal.setRecipe(mealDto.getRecipe());
        meal.setImageUrl(mealDto.getImageUrl());
        meal.setDescription(mealDto.getDescription());

        addProductsToMeal(meal, mealDto);

        return save(meal).map(mealDtoConverter::toDto);
    }

    private void addProductsToMeal(Meal meal, MealDto update) {
        meal.setProducts(productDtoConverter.listFromDto(update.getProducts()));
        updateMealInfoAfterAddProducts(meal);
    }

    private void updateMealInfoAfterAddProducts(Meal meal) {
        calculateProtein(meal);
        calculateCarbohydrate(meal);
        calculateFat(meal);
        calculateFibre(meal);
        calculateCarbohydrateExchange(meal);
        calculateProteinAndFatEquivalent(meal);
        calculateKcal(meal);
    }

    private void calculateProtein(Meal meal) {
        double protein = 0;

        for (Product product : meal.getProducts())
            protein += product.getProtein() * (double) (product.getAmount() / 100);

        meal.setProtein(protein);
    }

    private void calculateFibre(Meal meal) {
        double fibre = 0;

        for (Product product : meal.getProducts())
            fibre += product.getFibre() * (double) (product.getAmount() / 100);

        meal.setFibre(fibre);
    }

    private void calculateFat(Meal meal) {
        double fat = 0;

        for (Product product : meal.getProducts())
            fat += product.getFat() * (double) (product.getAmount() / 100);

        meal.setFat(fat);
    }

    private void calculateCarbohydrate(Meal meal) {
        double carbohydrate = 0;

        for (Product product : meal.getProducts())
            carbohydrate += product.getCarbohydrate() * (double) (product.getAmount() / 100);

        meal.setCarbohydrate(carbohydrate);
    }

    private void calculateProteinAndFatEquivalent(Meal meal) {
        double proteinAndFatEquivalent = 0;

        for (Product product : meal.getProducts())
            proteinAndFatEquivalent += product.getProteinAndFatEquivalent() * (double) (product.getAmount() / 100);

        meal.setProteinAndFatEquivalent(proteinAndFatEquivalent);
    }

    private void calculateCarbohydrateExchange(Meal meal) {
        double carbohydrateExchange = 0;

        for (Product product : meal.getProducts())
            carbohydrateExchange += product.getCarbohydrateExchange() * (double) (product.getAmount() / 100);

        meal.setCarbohydrateExchange(carbohydrateExchange);
    }

    private void calculateKcal(Meal meal) {
        double kcal = 0;

        for (Product product : meal.getProducts())
            kcal += product.getKcal() * (double) (product.getAmount() / 100);

        meal.setKcal(kcal);
    }
}
