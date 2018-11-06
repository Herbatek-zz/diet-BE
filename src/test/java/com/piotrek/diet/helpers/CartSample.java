package com.piotrek.diet.helpers;

import com.piotrek.diet.cart.Cart;
import com.piotrek.diet.cart.CartDto;

import java.time.LocalDate;
import java.util.UUID;

import static com.piotrek.diet.helpers.UserSample.johnWithId;

public class CartSample {

    private static final String CART1_ID = UUID.randomUUID().toString();

    public static Cart cart1() {
        return Cart.builder()
                .id(CART1_ID)
                .userId(johnWithId().getId())
                .targetUserCalories(johnWithId().getCaloriesPerDay())
                .date(LocalDate.now())
                .build();
    }

    public static CartDto cartDto1() {
        return CartDto.builder()
                .id(CART1_ID)
                .userId(johnWithId().getId())
                .targetUserCalories(johnWithId().getCaloriesPerDay())
                .date(LocalDate.now())
                .build();
    }
}
