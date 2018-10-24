package com.piotrek.diet.cart;

import com.piotrek.diet.meal.Meal;
import com.piotrek.diet.meal.MealDtoConverter;
import com.piotrek.diet.product.Product;
import com.piotrek.diet.product.ProductDtoConverter;
import com.piotrek.diet.sample.CartEquals;
import com.piotrek.diet.sample.CartSample;
import com.piotrek.diet.sample.MealSample;
import com.piotrek.diet.sample.ProductSample;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CartDtoConverterTest {

    private ProductDtoConverter productDtoConverter = new ProductDtoConverter();
    private MealDtoConverter mealDtoConverter = new MealDtoConverter(productDtoConverter);
    private CartDtoConverter cartDtoConverter = new CartDtoConverter(mealDtoConverter, productDtoConverter);

    private Cart cart;
    private CartDto cartDto;

    @BeforeEach
    void beforeEach() {
        cart = CartSample.cart1();
        cartDto = CartSample.cartDto1();
    }

    @Test
    void toDto() {
        final var converted = cartDtoConverter.toDto(cart);
        assertTrue(CartEquals.cartDtoEquals(cartDto, converted));
    }

    @Test
    void toDto_withMealsAndProducts() {
        addProducts();
        addMeals();

        final var converted = cartDtoConverter.toDto(cart);
        assertTrue(CartEquals.cartDtoEquals(cartDto, converted));
    }

    @Test
    void toDto_withMealsWithProductsAndProducts() {
        addMealsWithProducts();
        addMeals();

        final var converted = cartDtoConverter.toDto(cart);
        assertTrue(CartEquals.cartDtoEquals(cartDto, converted));
    }

    @Test
    void toDto_withMeal2sWithProductsAndProducts() {
        addDuplicated();

        final var converted = cartDtoConverter.toDto(cart);
        assertTrue(CartEquals.cartDtoEquals(cartDto, converted));
    }


    @Test
    void fromDto() {
        final Cart converted = cartDtoConverter.fromDto(cartDto);
        assertTrue(CartEquals.cartEquals(cart, converted));
    }

    @Test
    void fromDto_withMealsAndProducts() {
        addMeals();
        addProducts();

        final Cart converted = cartDtoConverter.fromDto(cartDto);
        assertTrue(CartEquals.cartEquals(cart, converted));
    }

    @Test
    void fromDto_withMealsWithProductsAndProducts() {
        addMealsWithProducts();
        addProducts();

        final Cart converted = cartDtoConverter.fromDto(cartDto);
        assertTrue(CartEquals.cartEquals(cart, converted));
    }

    private void addProducts() {
        cart.getProducts().add(ProductSample.bananaWithId());
        cart.getProducts().add(ProductSample.breadWithId());

        cartDto.getProducts().add(ProductSample.bananaWithIdDto());
        cartDto.getProducts().add(ProductSample.breadWithIdDto());
        cartDto.setItemCounter(cartDto.getItemCounter() + 2);
        cartDto.getAllProducts().addAll(cartDto.getProducts());
    }

    private void addDuplicated() {
        cart.getProducts().add(ProductSample.breadWithId());
        cartDto.getProducts().add(ProductSample.breadWithIdDto());

        cart.getProducts().add(ProductSample.bananaWithId());
        cartDto.getProducts().add(ProductSample.bananaWithIdDto());

        cartDto.setItemCounter(cartDto.getItemCounter() + 2);

        Meal firstMeal = MealSample.coffeeWithId();
        firstMeal.getProducts().add(ProductSample.bananaWithId());
        cart.getMeals().add(firstMeal);
        cartDto.getMeals().add(MealSample.coffeeWithIdDto());

        Meal secondMeal = MealSample.dumplingsWithId();
        secondMeal.getProducts().add(ProductSample.bananaWithId());
        cart.getMeals().add(secondMeal);
        cartDto.getMeals().add(MealSample.dumplingsWithIdDto());
        cartDto.setItemCounter(cartDto.getItemCounter() + 2);

        cartDto.getAllProducts().addAll(cartDto.getProducts());
    }

    private void addMeals() {
        cart.getMeals().add(MealSample.coffeeWithId());
        cart.getMeals().add(MealSample.dumplingsWithId());

        cartDto.getMeals().add(MealSample.coffeeWithIdDto());
        cartDto.getMeals().add(MealSample.dumplingsWithIdDto());
        cartDto.setItemCounter(cartDto.getItemCounter() + 2);
    }

    private void addMealsWithProducts() {
        Product product = ProductSample.bananaWithId();
        product.setAmount(60);

        Meal meal = new Meal();
        meal.getProducts().add(product);
        meal.setAmount(product.getAmount());
        meal.setName("Banana salad");
        meal.setDescription("Some description");
        meal.setRecipe("Cut bananas, then mix with bananas");
        meal.setId(UUID.randomUUID().toString());
        meal.setKcal(product.getKcal());
        meal.setProteinAndFatEquivalent(product.getProteinAndFatEquivalent());
        meal.setProtein(product.getProtein());
        meal.setCarbohydrateExchange(product.getCarbohydrateExchange());
        meal.setCarbohydrate(product.getCarbohydrate());
        meal.setFat(product.getFat());
        meal.setFibre(product.getFibre());

        cart.getMeals().add(meal);

        cartDto.getMeals().add(mealDtoConverter.toDto(meal));
        cartDto.getAllProducts().add(productDtoConverter.toDto(product));
        cartDto.setItemCounter(cartDto.getItemCounter() + 1);
    }

}