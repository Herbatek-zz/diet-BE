package com.piotrek.diet.meal;

import com.piotrek.diet.cloud.CloudStorageService;
import com.piotrek.diet.exceptions.NotFoundException;
import com.piotrek.diet.helpers.Page;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static com.piotrek.diet.helpers.Constants.IMAGE_CONTAINER_MEALS;

@Service
@RequiredArgsConstructor
public class MealService {

    private final MealRepository mealRepository;
    private final MealDtoConverter mealDtoConverter;
    private final ProductDtoConverter productDtoConverter;
    private final DoubleRounder doubleRounder;
    private final CloudStorageService imageStorage;

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
        uploadImageAndSetImageUrl(mealDto, meal);
        meal.setDescription(mealDto.getDescription());
        meal.setProducts(productDtoConverter.listFromDto(mealDto.getProducts()));
        var calculatedProductList = createCalculatedProductList(meal.getProducts());
        calculateMealInformation(meal, calculatedProductList);
        return save(meal).map(mealDtoConverter::toDto);
    }

    private void uploadImageAndSetImageUrl(MealDto mealDto, Meal meal) {
        if (mealDto.getImageToSave() != null) {
            var url = imageStorage.uploadImageBlob(IMAGE_CONTAINER_MEALS, mealDto.getId(), mealDto.getImageToSave());
            meal.setImageUrl(url + "#" + LocalDateTime.now());
        }
    }

    public ArrayList<Product> createCalculatedProductList(ArrayList<Product> products) {
        ArrayList<Product> listOfProducts = new ArrayList<>(products.size());

        double allProductsAmount = products
                .stream()
                .mapToDouble(Product::getAmount)
                .sum();

        products.forEach(product -> {
            var productCopy = new Product();
            var divider = product.getAmount() / (allProductsAmount / 100) / 100;
            productCopy.setId(product.getId());
            productCopy.setName(product.getName());
            productCopy.setAmount(product.getAmount());
            productCopy.setImageUrl(product.getImageUrl());
            productCopy.setUserId(product.getUserId());
            productCopy.setDescription(product.getDescription());
            productCopy.setProtein(doubleRounder.round((product.getProtein() * divider)));
            productCopy.setCarbohydrate(doubleRounder.round((product.getCarbohydrate() * divider)));
            productCopy.setFat(doubleRounder.round((product.getFat() * divider)));
            productCopy.setFibre(doubleRounder.round((product.getFibre() * divider)));
            productCopy.setProteinAndFatEquivalent(doubleRounder.round((product.getProteinAndFatEquivalent() * divider)));
            productCopy.setCarbohydrateExchange(doubleRounder.round((product.getCarbohydrateExchange() * divider)));
            productCopy.setKcal(doubleRounder.round((product.getKcal() * divider)));
            listOfProducts.add(productCopy);
        });
        return listOfProducts;
    }

    public void calculateMealInformation(Meal meal, ArrayList<Product> products) {
        calculateProtein(meal, products);
        calculateCarbohydrate(meal, products);
        calculateFat(meal, products);
        calculateFibre(meal, products);
        calculateCarbohydrateExchange(meal, products);
        calculateProteinAndFatEquivalent(meal, products);
        calculateKcal(meal, products);
        calculateAmount(meal, products);
    }

    private void calculateAmount(Meal meal, ArrayList<Product> products) {
        int amountValue = products
                .stream()
                .mapToInt(Product::getAmount)
                .sum();

        meal.setAmount(amountValue);
    }

    private void calculateProtein(Meal meal, ArrayList<Product> products) {
        var proteinValue = products
                .stream()
                .mapToDouble(Product::getProtein)
                .sum();

        meal.setProtein(doubleRounder.round(proteinValue));
    }

    private void calculateFibre(Meal meal, ArrayList<Product> products) {
        var fibreValue = products
                .stream()
                .mapToDouble(Product::getFibre)
                .sum();

        meal.setFibre(doubleRounder.round(fibreValue));
    }

    private void calculateFat(Meal meal, ArrayList<Product> products) {
        var fatValue = products
                .stream()
                .mapToDouble(Product::getFat)
                .sum();

        meal.setFat(doubleRounder.round(fatValue));
    }

    private void calculateCarbohydrate(Meal meal, ArrayList<Product> products) {
        var carbohydrateValue = products
                .stream()
                .mapToDouble(Product::getCarbohydrate)
                .sum();

        meal.setCarbohydrate(doubleRounder.round(carbohydrateValue));
    }

    private void calculateProteinAndFatEquivalent(Meal meal, ArrayList<Product> products) {
        var proteinAndFatEquivalentValue = products
                .stream()
                .mapToDouble(Product::getProteinAndFatEquivalent)
                .sum();

        meal.setProteinAndFatEquivalent(doubleRounder.round(proteinAndFatEquivalentValue));
    }

    private void calculateCarbohydrateExchange(Meal meal, ArrayList<Product> products) {
        var carbohydrateExchangeValue = products
                .stream()
                .mapToDouble(Product::getCarbohydrateExchange)
                .sum();

        meal.setCarbohydrateExchange(doubleRounder.round(carbohydrateExchangeValue));
    }

    private void calculateKcal(Meal meal, ArrayList<Product> products) {
        double kcalValue = products
                .stream()
                .mapToDouble(Product::getKcal)
                .sum();

        meal.setKcal(doubleRounder.round(kcalValue));
    }
}
