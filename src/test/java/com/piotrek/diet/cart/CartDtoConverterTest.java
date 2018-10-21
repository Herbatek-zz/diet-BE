package com.piotrek.diet.cart;

import com.piotrek.diet.meal.MealDtoConverter;
import com.piotrek.diet.product.ProductDtoConverter;
import com.piotrek.diet.sample.CartSample;
import com.piotrek.diet.sample.MealSample;
import com.piotrek.diet.sample.ProductSample;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

        assertAll(
                () -> assertEquals(cartDto.getId(), converted.getId()),
                () -> assertEquals(cartDto.getDate(), converted.getDate()),
                () -> assertEquals(cartDto.getUserId(), converted.getUserId()),
                () -> assertEquals(cartDto.getMeals(), converted.getMeals()),
                () -> assertEquals(cartDto.getProducts(), converted.getProducts()),
                () -> assertEquals(cartDto.getAllProducts(), converted.getAllProducts())
        );
    }

    @Test
    void toDto_withMealsAndProducts() {
        addProducts();
        addMeals();

        final var converted = cartDtoConverter.toDto(cart);

        assertAll(
                () -> assertEquals(cartDto.getId(), converted.getId()),
                () -> assertEquals(cartDto.getDate(), converted.getDate()),
                () -> assertEquals(cartDto.getUserId(), converted.getUserId()),
                () -> assertEquals(cartDto.getMeals(), converted.getMeals()),
                () -> assertEquals(2, cartDto.getMeals().size()),
                () -> assertEquals(cartDto.getProducts(), converted.getProducts()),
                () -> assertEquals(2, converted.getProducts().size()),
                () -> assertNotEquals(cartDto.getAllProducts(), converted.getAllProducts()),
                () -> assertEquals(2, converted.getAllProducts().size())
        );
    }

    @Test
    void fromDto() {
        final Cart converted = cartDtoConverter.fromDto(cartDto);

        assertAll(
                () -> assertEquals(cart.getId(), converted.getId()),
                () -> assertEquals(cart.getDate(), converted.getDate()),
                () -> assertEquals(cart.getUserId(), converted.getUserId()),
                () -> assertEquals(cart.getMeals(), converted.getMeals()),
                () -> assertEquals(cart.getProducts(), converted.getProducts())
        );
    }

    @Test
    void fromDto_withMealsAndProducts() {
        addMeals();
        addProducts();

        final Cart converted = cartDtoConverter.fromDto(cartDto);

        assertAll(
                () -> assertEquals(cart.getId(), converted.getId()),
                () -> assertEquals(cart.getDate(), converted.getDate()),
                () -> assertEquals(cart.getUserId(), converted.getUserId()),
                () -> assertEquals(cart.getMeals(), converted.getMeals()),
                () -> assertEquals(cart.getProducts(), converted.getProducts()),
                () -> assertEquals(2, cart.getProducts().size()),
                () -> assertEquals(2, cart.getMeals().size())
        );
    }

    private void addProducts() {
        cart.getProducts().add(ProductSample.bananaWithId());
        cart.getProducts().add(ProductSample.breadWithId());

        cartDto.getProducts().add(ProductSample.bananaWithIdDto());
        cartDto.getProducts().add(ProductSample.breadWithIdDto());
    }

    private void addMeals() {
        cart.getMeals().add(MealSample.coffeeWithId());
        cart.getMeals().add(MealSample.dumplingsWithId());

        cartDto.getMeals().add(MealSample.coffeeWithIdDto());
        cartDto.getMeals().add(MealSample.dumplingsWithIdDto());
    }

}