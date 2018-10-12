package com.piotrek.diet.cart;

import com.piotrek.diet.meal.MealDtoConverter;
import com.piotrek.diet.product.ProductDtoConverter;
import com.piotrek.diet.sample.CartSample;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CartDtoConverterTest {

    private ProductDtoConverter productDtoConverter = new ProductDtoConverter();
    private MealDtoConverter mealDtoConverter = new MealDtoConverter(productDtoConverter);
    private CartDtoConverter cartDtoConverter = new CartDtoConverter(mealDtoConverter);

    private Cart cart;
    private CartDto cartDto;

    @BeforeEach
    void setUp() {
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
                () -> assertEquals(cartDto.getMeals(), converted.getMeals())
        );
    }

    @Test
    void fromDto() {
        final var converted = cartDtoConverter.fromDto(cartDto);

        assertAll(
                () -> assertEquals(cart.getId(), converted.getId()),
                () -> assertEquals(cart.getDate(), converted.getDate()),
                () -> assertEquals(cart.getUserId(), converted.getUserId()),
                () -> assertEquals(cart.getMeals(), converted.getMeals())
        );
    }
}