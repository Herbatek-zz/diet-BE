package com.piotrek.diet.helpers;

import com.piotrek.diet.cart.Cart;
import com.piotrek.diet.cart.CartDto;

public class CartEquals {

    public static boolean cartEquals(Cart firstCart, Cart secondCart) {
        if (!firstCart.getId().equals(secondCart.getId())) {
            System.err.println("Id is not the same");
            System.out.println(firstCart.getId());
            System.out.println(secondCart.getId());
            return false;
        }
        if (!firstCart.getDate().equals(secondCart.getDate())) {
            System.err.println("Date is not the same");
            System.out.println(firstCart.getDate());
            System.out.println(secondCart.getDate());
            return false;
        }
        if (!firstCart.getMeals().equals(secondCart.getMeals())) {
            System.err.println("Meals are not the same");
            System.out.println(firstCart.getMeals());
            System.out.println(secondCart.getMeals());
            return false;
        }
        if (!firstCart.getProducts().equals(secondCart.getProducts())) {
            System.err.println("Products are not the same");
            System.out.println(firstCart.getProducts());
            System.out.println(secondCart.getProducts());
            return false;
        }
        if (!firstCart.getUserId().equals(secondCart.getUserId())) {
            System.err.println("UserId is not the same");
            System.out.println(firstCart.getUserId());
            System.out.println(secondCart.getId());
            return false;
        }
        return true;
    }

    public static boolean cartDtoEquals(CartDto firstCartDto, CartDto secondCartDto) {
        if (!(firstCartDto.getId().equals(secondCartDto.getId()))) {
            System.err.println("Id is not the same");
            System.out.println(firstCartDto.getId());
            System.out.println(secondCartDto.getId());
            return false;
        }
        if (!firstCartDto.getDate().equals(secondCartDto.getDate())) {
            System.err.println("Date is not the same");
            System.out.println(firstCartDto.getDate());
            System.out.println(secondCartDto.getDate());
            return false;
        }
        if (!firstCartDto.getMeals().equals(secondCartDto.getMeals())) {
            System.err.println("Meals are not the same");
            System.out.println(firstCartDto.getMeals());
            System.out.println(secondCartDto.getMeals());
            return false;
        }
        if (!firstCartDto.getProducts().equals(secondCartDto.getProducts())) {
            System.err.println("Products are not the same");
            System.out.println(firstCartDto.getProducts());
            System.out.println(secondCartDto.getProducts());
            return false;
        }
        if (!firstCartDto.getUserId().equals(secondCartDto.getUserId())) {
            System.err.println("UserId is not the same");
            System.out.println(firstCartDto.getUserId());
            System.out.println(secondCartDto.getUserId());
            return false;
        }
        if (firstCartDto.getItemCounter() != secondCartDto.getItemCounter()) {
            System.err.println("ItemCounter is not the same");
            System.out.println(firstCartDto.getItemCounter());
            System.out.println(secondCartDto.getItemCounter());
            return false;
        }
        if (!firstCartDto.getAllProducts().equals(secondCartDto.getAllProducts())) {
            System.err.println("AlLProducts are not the same");
            System.out.println(firstCartDto.getAllProducts());
            System.out.println(secondCartDto.getAllProducts());
            return false;
        }
        return true;
    }
}
