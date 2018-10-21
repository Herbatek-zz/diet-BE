package com.piotrek.diet.cart;

import com.piotrek.diet.meal.Meal;
import com.piotrek.diet.meal.MealService;
import com.piotrek.diet.product.Product;
import com.piotrek.diet.product.ProductService;
import com.piotrek.diet.user.UserValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CartFacade {

    private final UserValidation userValidation;
    private final CartService cartService;
    private final CartDtoConverter cartDtoConverter;
    private final MealService mealService;
    private final ProductService productService;


    Mono<CartDto> addMealToCart(String cartId, String mealId) {
        Cart cart = cartService.findById(cartId).block();
        userValidation.validateUserWithPrincipal(cart.getUserId());
        Meal meal = mealService.findById(mealId).block();
        cart.getMeals().add(meal);
        return cartService.save(cart).map(cartDtoConverter::toDto);
    }

    Mono<CartDto> deleteMealFromCart(String cartId, String mealId) {
        Cart cart = cartService.findById(cartId).block();
        userValidation.validateUserWithPrincipal(cart.getUserId());
        Meal meal = mealService.findById(mealId).block();
        if(cart.getMeals().contains(meal)) {
            cart.getMeals().remove(meal);
            cart = cartService.save(cart).block();
        }
        return Mono.just(cartDtoConverter.toDto(cart));
    }

    Mono<CartDto> addProductToCart(String cartId, String productId) {
        Cart cart = cartService.findById(cartId).block();
        userValidation.validateUserWithPrincipal(cart.getUserId());
        Product product = productService.findById(productId).block();
        cart.getProducts().add(product);
        return cartService.save(cart).map(cartDtoConverter::toDto);
    }

    Mono<CartDto> deleteProductFromCart(String cartId, String productId) {
        Cart cart = cartService.findById(cartId).block();
        userValidation.validateUserWithPrincipal(cart.getUserId());
        Product product = productService.findById(productId).block();
        if(cart.getProducts().contains(product)) {
            cart.getProducts().remove(product);
            cart = cartService.save(cart).block();
        }
        return Mono.just(cartDtoConverter.toDto(cart));
    }
}
