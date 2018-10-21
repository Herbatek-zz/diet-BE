package com.piotrek.diet.cart;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartFacade cartFacade;

    @PutMapping("/{cartId}/meals/{mealId}")
    @ResponseStatus(OK)
    Mono<CartDto> addMealToCart(@PathVariable String cartId, @PathVariable String mealId) {
        return cartFacade.addMealToCart(cartId, mealId);
    }

    @PutMapping("/{cartId}/products/{productId}")
    @ResponseStatus(OK)
    Mono<CartDto> addProductToCart(@PathVariable String cartId, @PathVariable String productId) {
        return cartFacade.addProductToCart(cartId, productId);
    }

    @DeleteMapping("/{cartId}/meals/{mealId}")
    @ResponseStatus(NO_CONTENT)
    Mono<CartDto> deleteMealFromCart(@PathVariable String cartId, @PathVariable String mealId) {
        return cartFacade.deleteMealFromCart(cartId, mealId);
    }

    @DeleteMapping("/{cartId}/products/{productId}")
    @ResponseStatus(NO_CONTENT)
    Mono<CartDto> deleteProductFromCart(@PathVariable String cartId, @PathVariable String productId) {
        return cartFacade.deleteProductFromCart(cartId, productId);
    }
}
