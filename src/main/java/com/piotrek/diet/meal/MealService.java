package com.piotrek.diet.meal;

import com.piotrek.diet.helpers.Page;
import com.piotrek.diet.helpers.exceptions.NotFoundException;
import com.piotrek.diet.product.Product;
import com.piotrek.diet.product.ProductDto;
import com.piotrek.diet.product.ProductDtoConverter;
import com.piotrek.diet.user.UserValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
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

    Mono<Void> deleteAll() {
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

    Mono<MealDto> addProductsToMeal(String productId, List<ProductDto> productDtoList) {
        Meal meal = findById(productId).block();
        userValidation.validateUserWithPrincipal(meal.getUserId());

        meal.setProducts(productDtoConverter.listFromDto(productDtoList));

        updateMealInfoAfterAddProducts(meal);

        return save(meal).map(mealDtoConverter::toDto);
    }

    private void updateMealInfoAfterAddProducts(Meal meal) {
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
