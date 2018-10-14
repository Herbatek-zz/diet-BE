package com.piotrek.diet.sample;

import com.piotrek.diet.cart.Cart;
import com.piotrek.diet.cart.CartDto;

import java.time.LocalDate;
import java.time.Month;
import java.util.UUID;

public class CartSample {

    private static final String CART1_ID = UUID.randomUUID().toString();
    private static final String CART2_ID = UUID.randomUUID().toString();

    public static Cart cart1() {
        var cart = new Cart();
        cart.setId(CART1_ID);
        cart.setDate(LocalDate.now());
        cart.setUserId(UserSample.johnWithId().getId());
        return cart;
    }

    public static CartDto cartDto1() {
        var cartDto = new CartDto();
        cartDto.setId(CART1_ID);
        cartDto.setDate(LocalDate.now());
        cartDto.setUserId(UserSample.johnWithId().getId());
        return cartDto;
    }

    public static Cart cart2() {
        var cart = new Cart();
        cart.setId(CART2_ID);
        cart.setDate(LocalDate.of(2002, Month.APRIL, 1));
        cart.setUserId(UserSample.baileyWithId().getId());
        return cart;
    }

    public static CartDto cartDto2() {
        var cartDto = new CartDto();
        cartDto.setId(CART2_ID);
        cartDto.setDate(LocalDate.of(2002, Month.APRIL, 1));
        cartDto.setUserId(UserSample.baileyWithId().getId());
        return cartDto;
    }
}
