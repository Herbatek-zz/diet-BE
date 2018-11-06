package com.piotrek.diet.helpers;

import com.piotrek.diet.cart.Cart;
import com.piotrek.diet.cart.CartDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import static com.piotrek.diet.helpers.UserSample.johnWithId;

public class CartSample {

    private static final String CART1_ID = UUID.randomUUID().toString();
    private static final String CART1_USER_ID = johnWithId().getId();
    private static final int USER1_CALORIES_TARGET = johnWithId().getCaloriesPerDay();

    public static Cart cart1() {
        return new Cart(CART1_ID, CART1_USER_ID, USER1_CALORIES_TARGET, LocalDate.now(),
                new ArrayList<>(), new ArrayList<>());
    }

    public static CartDto cartDto1() {
        return new CartDto(CART1_ID, CART1_USER_ID, USER1_CALORIES_TARGET, LocalDate.now(), 0,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }
}
