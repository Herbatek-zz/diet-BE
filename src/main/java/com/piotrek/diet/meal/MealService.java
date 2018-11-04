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
import reactor.core.publisher.Mono;

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

    Mono<Void> deleteById(String id) {
        Meal block = findById(id).block();
        userValidation.validateUserWithPrincipal(block.getUserId());
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

        if (mealDto.getName() != null)
            meal.setName(mealDto.getName());
        if (mealDto.getRecipe() != null)
            meal.setRecipe(mealDto.getRecipe());
        if (mealDto.getImageUrl() != null)
            meal.setImageUrl(mealDto.getImageUrl());
        if (mealDto.getDescription() != null)
            meal.setDescription(mealDto.getDescription());
        if (mealDto.getProducts().size() != 0)
            addProductsToMeal(meal, mealDto);

        return save(meal).map(mealDtoConverter::toDto);
    }

    private void addProductsToMeal(Meal meal, MealDto update) {
        meal.setProducts(productDtoConverter.listFromDto(update.getProducts()));
        meal.getProducts()
                .forEach(product -> {
                    var divider = product.getAmount() / 100.0;
                    product.setProtein(product.getProtein() * divider);
                    product.setCarbohydrate(product.getCarbohydrate() * divider);
                    product.setFat(product.getFat() * divider);
                    product.setFibre(product.getFibre() * divider);
                    product.setProteinAndFatEquivalent(product.getProteinAndFatEquivalent() * divider);
                    product.setCarbohydrateExchange(product.getCarbohydrateExchange() * divider);
                    product.setKcal(product.getKcal() * divider);
                });
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

        meal.setProtein(proteinValue);
    }

    private void calculateFibre(Meal meal) {
        var fibreValue = meal.getProducts()
                .stream()
                .mapToDouble(Product::getFibre)
                .sum();

        meal.setFibre(fibreValue);
    }

    private void calculateFat(Meal meal) {
        var fatValue = meal.getProducts()
                .stream()
                .mapToDouble(Product::getFat)
                .sum();

        meal.setFat(fatValue);
    }

    private void calculateCarbohydrate(Meal meal) {
        var carbohydrateValue = meal.getProducts()
                .stream()
                .mapToDouble(Product::getCarbohydrate)
                .sum();

        meal.setCarbohydrate(carbohydrateValue);
    }

    private void calculateProteinAndFatEquivalent(Meal meal) {
        var proteinAndFatEquivalentValue = meal.getProducts()
                .stream()
                .mapToDouble(Product::getProteinAndFatEquivalent)
                .sum();

        meal.setProteinAndFatEquivalent(proteinAndFatEquivalentValue);
    }

    private void calculateCarbohydrateExchange(Meal meal) {
        var carbohydrateExchangeValue = meal.getProducts()
                .stream()
                .mapToDouble(Product::getCarbohydrateExchange)
                .sum();

        meal.setCarbohydrateExchange(carbohydrateExchangeValue);
    }

    private void calculateKcal(Meal meal) {
        double kcalValue = meal.getProducts()
                .stream()
                .mapToDouble(Product::getKcal)
                .sum();

        meal.setKcal(kcalValue);
    }
}
