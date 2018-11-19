package com.piotrek.diet.helpers;

import com.piotrek.diet.cart.Cart;
import com.piotrek.diet.cart.CartDto;

import java.time.LocalDate;
import java.util.UUID;

import static com.piotrek.diet.helpers.UserSample.john;

public class CartSample {

    private static final String CART1_ID = UUID.randomUUID().toString();
    private static final String CART1_USER_ID = john().getId();
    private static final int USER1_CALORIES_TARGET = john().getCaloriesPerDay();

    public static Cart cart1() {
        return new Cart(CART1_ID, CART1_USER_ID, USER1_CALORIES_TARGET, LocalDate.now());
    }

    public static CartDto cartDto1() {
        return new CartDto(CART1_ID, CART1_USER_ID, USER1_CALORIES_TARGET, LocalDate.now());
    }
}
