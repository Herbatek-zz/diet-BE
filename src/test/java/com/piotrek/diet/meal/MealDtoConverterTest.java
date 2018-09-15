package com.piotrek.diet.meal;

import com.piotrek.diet.product.Product;
import com.piotrek.diet.product.ProductDto;
import com.piotrek.diet.product.ProductDtoConverter;
import com.piotrek.diet.sample.ProductSample;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.List;

import static com.piotrek.diet.sample.MealSample.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MealDtoConverterTest {

    private MealDtoConverter mealDtoConverter;

    private Meal meal;
    private MealDto meaLDto;

    @BeforeAll
    void setup() {
        ProductDtoConverter productDtoConverter = new ProductDtoConverter();
        mealDtoConverter = new MealDtoConverter(productDtoConverter);

        var listProducts = new ArrayList<Product>(2);
        listProducts.add(ProductSample.breadWithId());
        listProducts.add(ProductSample.bananaWithId());

        meal = dumplingsWithId();
        meal.setProducts(listProducts);


        var listProductsDto = new ArrayList<ProductDto>(2);
        listProductsDto.add(ProductSample.breadWithIdDto());
        listProductsDto.add(ProductSample.bananaWithIdDto());

        meaLDto = dumplingsWithIdDto();
        meaLDto.setProducts(listProductsDto);
    }

    @Test
    void toDto() {
        var convertedMeal = mealDtoConverter.toDto(meal);

        assertNotNull(convertedMeal);
        assertAll(
                () -> assertEquals(meaLDto.getId(), convertedMeal.getId()),
                () -> assertEquals(meaLDto.getName(), convertedMeal.getName()),
                () -> assertEquals(meaLDto.getDescription(), convertedMeal.getDescription()),
                () -> assertEquals(meaLDto.getRecipe(), convertedMeal.getRecipe()),
                () -> assertEquals(meaLDto.getProtein(), convertedMeal.getProtein()),
                () -> assertEquals(meaLDto.getCarbohydrate(), convertedMeal.getCarbohydrate()),
                () -> assertEquals(meaLDto.getFat(), convertedMeal.getFat()),
                () -> assertEquals(meaLDto.getFibre(), convertedMeal.getFibre()),
                () -> assertEquals(meaLDto.getKcal(), convertedMeal.getKcal()),
                () -> assertEquals(meaLDto.getCarbohydrateExchange(), convertedMeal.getCarbohydrateExchange()),
                () -> assertEquals(meaLDto.getProteinAndFatEquivalent(), convertedMeal.getProteinAndFatEquivalent()),
                () -> assertEquals(meaLDto.getImageUrl(), convertedMeal.getImageUrl()),
                () -> assertEquals(meaLDto.getProducts(), convertedMeal.getProducts()),
                () -> assertEquals(meaLDto.getUserId(), convertedMeal.getUserId())
        );
    }

    @Test
    void fromDto() {
        var convertedMeal = mealDtoConverter.fromDto(meaLDto);

        assertNotNull(convertedMeal);
        assertAll(
                () -> assertEquals(meal.getId(), convertedMeal.getId()),
                () -> assertEquals(meal.getName(), convertedMeal.getName()),
                () -> assertEquals(meal.getDescription(), convertedMeal.getDescription()),
                () -> assertEquals(meal.getRecipe(), convertedMeal.getRecipe()),
                () -> assertEquals(meal.getProtein(), convertedMeal.getProtein()),
                () -> assertEquals(meal.getCarbohydrate(), convertedMeal.getCarbohydrate()),
                () -> assertEquals(meal.getFat(), convertedMeal.getFat()),
                () -> assertEquals(meal.getFibre(), convertedMeal.getFibre()),
                () -> assertEquals(meal.getKcal(), convertedMeal.getKcal()),
                () -> assertEquals(meal.getCarbohydrateExchange(), convertedMeal.getCarbohydrateExchange()),
                () -> assertEquals(meal.getProteinAndFatEquivalent(), convertedMeal.getProteinAndFatEquivalent()),
                () -> assertEquals(meal.getImageUrl(), convertedMeal.getImageUrl()),
                () -> assertEquals(meal.getProducts(), convertedMeal.getProducts()),
                () -> assertEquals(meal.getUserId(), convertedMeal.getUserId())
        );
    }

    @Test
    void listToDto() {
        var products = new ArrayList<Meal>(2);
        products.add(dumplingsWithId());
        products.add(coffeeWithId());

        List<MealDto> convertedList = mealDtoConverter.listToDto(products);

        assertNotNull(convertedList);
        assertNotNull(convertedList.get(0));
        assertNotNull(convertedList.get(1));
        assertAll(
                () -> assertEquals(products.size(), convertedList.size()),
                () -> assertEquals(products.get(0).getId(), convertedList.get(0).getId()),
                () -> assertEquals(products.get(1).getId(), convertedList.get(1).getId())
        );
    }
}