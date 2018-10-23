package com.piotrek.diet.cart;

import com.piotrek.diet.helpers.DtoConverter;
import com.piotrek.diet.meal.MealDto;
import com.piotrek.diet.meal.MealDtoConverter;
import com.piotrek.diet.product.ProductDto;
import com.piotrek.diet.product.ProductDtoConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class CartDtoConverter implements DtoConverter<Cart, CartDto> {

    private final MealDtoConverter mealDtoConverter;
    private final ProductDtoConverter productDtoConverter;

    @Override
    public CartDto toDto(Cart entity) {
        var cartDto = new CartDto();
        cartDto.setId(entity.getId());
        cartDto.setDate(entity.getDate());
        cartDto.setMeals(mealDtoConverter.listToDto(entity.getMeals()));
        cartDto.setProducts(productDtoConverter.listToDto(entity.getProducts()));
        cartDto.setItemCounter(entity.getMeals().size() + entity.getProducts().size());

        var productsFromMeals = retrieveProductsFromMeals(cartDto.getMeals());
        var allProducts = sumProductsAndProductsFromMeals(cartDto.getProducts(), productsFromMeals);
        cartDto.setAllProducts(allProducts);

        cartDto.setUserId(entity.getUserId());

        return cartDto;
    }

    @Override
    public Cart fromDto(CartDto dto) {
        var cart = new Cart(dto.getUserId(), dto.getDate());
        cart.setId(dto.getId());
        cart.setMeals(mealDtoConverter.listFromDto(dto.getMeals()));
        cart.setProducts(productDtoConverter.listFromDto(dto.getProducts()));
        return cart;
    }

    private ArrayList<ProductDto> retrieveProductsFromMeals(ArrayList<MealDto> mealDtos) {
        var productsFromMeals = new ArrayList<ProductDto>();

        mealDtos.forEach(mealDto -> mealDto.getProducts().forEach(productToAdd -> {
            if (productsFromMeals.contains(productToAdd))
                sumAmountDuplicatedProduct(productsFromMeals, productToAdd);
            else
                productsFromMeals.add(productToAdd);
        }));
        return productsFromMeals;
    }

    private ArrayList<ProductDto> sumProductsAndProductsFromMeals(ArrayList<ProductDto> products, ArrayList<ProductDto> productsFromMeals) {
        var allProducts = new ArrayList<ProductDto>();
        allProducts.addAll(products);
        allProducts.addAll(productsFromMeals);

        var productsWithoutDuplicates = new ArrayList<ProductDto>();

        allProducts.forEach(productToAdd -> {
            if (productsWithoutDuplicates.contains(productToAdd))
                sumAmountDuplicatedProduct(productsWithoutDuplicates, productToAdd);
            else
                productsWithoutDuplicates.add(productToAdd);
        });
        return productsWithoutDuplicates;
    }

    private void sumAmountDuplicatedProduct(ArrayList<ProductDto> mainProductList, ProductDto duplicatedProduct) {
        int indexOfDuplicatedProduct = mainProductList.indexOf(duplicatedProduct);
        duplicatedProduct = mainProductList.remove(indexOfDuplicatedProduct);
        duplicatedProduct.setAmount(duplicatedProduct.getAmount() + duplicatedProduct.getAmount());
        mainProductList.add(duplicatedProduct);
    }
}
