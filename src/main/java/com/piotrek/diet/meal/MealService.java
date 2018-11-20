package com.piotrek.diet.meal;

import com.piotrek.diet.helpers.Page;
import com.piotrek.diet.exceptions.NotFoundException;
import com.piotrek.diet.product.Product;
import com.piotrek.diet.product.ProductDtoConverter;
import lombok.RequiredArgsConstructor;
import org.decimal4j.util.DoubleRounder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MealService {

    private final MealRepository mealRepository;
    private final MealDtoConverter mealDtoConverter;
    private final ProductDtoConverter productDtoConverter;
    private final DoubleRounder doubleRounder;

    public Mono<Meal> findById(String id) {
        return mealRepository.findById(id)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Not found meal [id = " + id + "]"))));
    }

    Mono<MealDto> findDtoById(String id) {
        return findById(id).map(mealDtoConverter::toDto);
    }

    public Mono<Page<MealDto>> findAllByUserId(String userId, Pageable pageable) {
        return mealRepository.findAllByUserId(userId)
                .collectList()
                .map(list -> new Page<>(list
                        .stream()
                        .skip(pageable.getPageNumber() * pageable.getPageSize())
                        .limit(pageable.getPageSize())
                        .map(mealDtoConverter::toDto)
                        .collect(Collectors.toList()),
                        pageable.getPageNumber(), pageable.getPageSize(), list.size()));
    }

    Flux<MealDto> find10MostFavourites() {
        return mealRepository.findFirst10ByOrderByFavouriteCounterDesc()
                .map(mealDtoConverter::toDto);
    }

    Flux<MealDto> find10LatestCreate() {
        return mealRepository.findFirst10ByOrderByCreatedAtDesc()
                .map(mealDtoConverter::toDto);
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
        Meal meal = mealDtoConverter.fromDto(mealDto);
        return save(meal);
    }

    public Mono<Void> deleteAll() {
        return mealRepository.deleteAll();
    }

    @PreAuthorize("@mealService.findById(#id).block().getUserId().equals(principal)")
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

    @PreAuthorize("@mealService.findById(#mealId).block().getUserId().equals(principal)")
    public Mono<MealDto> updateMeal(String mealId, MealDto mealDto) {
        Meal meal = findById(mealId).block();
        meal.setName(mealDto.getName());
        meal.setRecipe(mealDto.getRecipe());
        meal.setImageUrl(mealDto.getImageUrl());
        meal.setDescription(mealDto.getDescription());
        addProductsToMeal(meal, mealDto);

        return save(meal).map(mealDtoConverter::toDto);
    }

    private void addProductsToMeal(Meal meal, MealDto update) {
        if (update.getProducts().size() == 0)
            meal.setProducts(new ArrayList<>());
        else {
            meal.setProducts(productDtoConverter.listFromDto(update.getProducts()));

            double allProductsAmount = meal.getProducts()
                    .stream()
                    .mapToDouble(Product::getAmount)
                    .sum();

            meal.getProducts()
                    .forEach(product -> {
                        var divider = product.getAmount() / (allProductsAmount / 100) / 100;
                        product.setProtein(doubleRounder.round((product.getProtein() * divider)));
                        product.setCarbohydrate(doubleRounder.round((product.getCarbohydrate() * divider)));
                        product.setFat(doubleRounder.round((product.getFat() * divider)));
                        product.setFibre(doubleRounder.round((product.getFibre() * divider)));
                        product.setProteinAndFatEquivalent(doubleRounder.round((product.getProteinAndFatEquivalent() * divider)));
                        product.setCarbohydrateExchange(doubleRounder.round((product.getCarbohydrateExchange() * divider)));
                        product.setKcal(doubleRounder.round((product.getKcal() * divider)));
                    });
        }
        calculateMealInformation(meal);
    }

    public void calculateMealInformation(Meal meal) {
        calculateProtein(meal);
        calculateCarbohydrate(meal);
        calculateFat(meal);
        calculateFibre(meal);
        calculateCarbohydrateExchange(meal);
        calculateProteinAndFatEquivalent(meal);
        calculateKcal(meal);
        calculateAmount(meal);
    }

    private void calculateAmount(Meal meal) {
        int amountValue = meal.getProducts()
                .stream()
                .mapToInt(Product::getAmount)
                .sum();

        meal.setAmount(amountValue);
    }

    private void calculateProtein(Meal meal) {
        var proteinValue = meal.getProducts()
                .stream()
                .mapToDouble(Product::getProtein)
                .sum();

        meal.setProtein(doubleRounder.round(proteinValue));
    }

    private void calculateFibre(Meal meal) {
        var fibreValue = meal.getProducts()
                .stream()
                .mapToDouble(Product::getFibre)
                .sum();

        meal.setFibre(doubleRounder.round(fibreValue));
    }

    private void calculateFat(Meal meal) {
        var fatValue = meal.getProducts()
                .stream()
                .mapToDouble(Product::getFat)
                .sum();

        meal.setFat(doubleRounder.round(fatValue));
    }

    private void calculateCarbohydrate(Meal meal) {
        var carbohydrateValue = meal.getProducts()
                .stream()
                .mapToDouble(Product::getCarbohydrate)
                .sum();

        meal.setCarbohydrate(doubleRounder.round(carbohydrateValue));
    }

    private void calculateProteinAndFatEquivalent(Meal meal) {
        var proteinAndFatEquivalentValue = meal.getProducts()
                .stream()
                .mapToDouble(Product::getProteinAndFatEquivalent)
                .sum();

        meal.setProteinAndFatEquivalent(doubleRounder.round(proteinAndFatEquivalentValue));
    }

    private void calculateCarbohydrateExchange(Meal meal) {
        var carbohydrateExchangeValue = meal.getProducts()
                .stream()
                .mapToDouble(Product::getCarbohydrateExchange)
                .sum();

        meal.setCarbohydrateExchange(doubleRounder.round(carbohydrateExchangeValue));
    }

    private void calculateKcal(Meal meal) {
        double kcalValue = meal.getProducts()
                .stream()
                .mapToDouble(Product::getKcal)
                .sum();

        meal.setKcal(doubleRounder.round(kcalValue));
    }
}
