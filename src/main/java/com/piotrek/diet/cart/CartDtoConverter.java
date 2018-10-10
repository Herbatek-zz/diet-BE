package com.piotrek.diet.cart;

import com.piotrek.diet.helpers.DtoConverter;
import com.piotrek.diet.meal.MealDto;
import com.piotrek.diet.meal.MealDtoConverter;
import com.piotrek.diet.product.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class CartDtoConverter implements DtoConverter<Cart, CartDto> {

    private final MealDtoConverter mealDtoConverter;

    @Override
    public CartDto toDto(Cart entity) {
        var cartDto = new CartDto();
        cartDto.setId(entity.getId());
        cartDto.setDate(entity.getDate());
        cartDto.setMeals(mealDtoConverter.listToDto(entity.getMeals()));

        var products = retrieveProductsFromMeals(cartDto.getMeals());
        cartDto.setProducts(products);

        cartDto.setUserId(entity.getUserId());

        return cartDto;
    }

    @Override
    public Cart fromDto(CartDto dto) {
        var cart = new Cart(dto.getUserId(), dto.getDate());
        cart.setId(dto.getId());
        cart.setMeals(mealDtoConverter.listFromDto(dto.getMeals()));
        return cart;
    }

    private ArrayList<ProductDto> retrieveProductsFromMeals(ArrayList<MealDto> mealDtos) {
        var products = new ArrayList<ProductDto>();

        mealDtos.forEach(mealDto -> mealDto.getProducts().forEach(productDto -> {
            if (products.contains(productDto)) {
                var index = products.indexOf(productDto);
                var productFromList = products.remove(index);
                productFromList.setAmount(productFromList.getAmount() + productDto.getAmount());
                products.add(productFromList);
            } else
                products.add(productDto);

        }));
        return products;
    }
}
