package com.piotrek.diet.cart;

import com.piotrek.diet.meal.Meal;
import com.piotrek.diet.meal.MealDtoConverter;
import com.piotrek.diet.product.ProductDtoConverter;
import com.piotrek.diet.helpers.CartEquals;
import com.piotrek.diet.helpers.CartSample;
import com.piotrek.diet.helpers.MealSample;
import com.piotrek.diet.helpers.ProductSample;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    void toDto_withMealsWithMealsAndProductsDuplicated() {
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

}