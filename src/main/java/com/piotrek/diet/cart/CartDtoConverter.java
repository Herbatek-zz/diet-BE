package com.piotrek.diet.cart;

import com.piotrek.diet.helpers.DtoConverter;
import com.piotrek.diet.meal.MealDtoConverter;
import com.piotrek.diet.product.ProductDtoConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CartDtoConverter implements DtoConverter<Cart, CartDto> {

    private final MealDtoConverter mealDtoConverter;
    private final ProductDtoConverter productDtoConverter;
    private final CartCalculator cartCalculator;

    @Override
    public CartDto toDto(Cart entity) {
        var cartDto = new CartDto(entity.getUserId(), entity.getDate(), entity.getTargetUserCalories(),
                entity.getTargetUserCarbohydrate(), entity.getTargetUserProtein(), entity.getTargetUserFat());
        cartDto.setId(entity.getId());
        cartDto.setMeals(mealDtoConverter.listToDto(entity.getMeals()));
        cartDto.setProducts(productDtoConverter.listToDto(entity.getProducts()));
        cartDto.setItemCounter(entity.getMeals().size() + entity.getProducts().size());

        var productsFromMeals = cartCalculator.retrieveProductsFromMeals(cartDto.getMeals());
        var allProducts = cartCalculator.sumProductsAndProductsFromMeals(cartDto.getProducts(), productsFromMeals);
        cartDto.setAllProducts(allProducts);
        cartCalculator.calculateCartInfo(cartDto);

        return cartDto;
    }

    @Override
    public Cart fromDto(CartDto dto) {
        var cart = new Cart(dto.getUserId(), dto.getDate(), dto.getTargetUserCalories(), dto.getTargetUserCarbohydrate(),
                dto.getTargetUserProtein(), dto.getTargetUserFat());
        cart.setId(dto.getId());
        cart.setMeals(mealDtoConverter.listFromDto(dto.getMeals()));
        cart.setProducts(productDtoConverter.listFromDto(dto.getProducts()));
        return cart;
    }
}
