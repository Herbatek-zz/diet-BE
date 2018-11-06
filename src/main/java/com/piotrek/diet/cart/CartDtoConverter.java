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
        var cartDto = new CartDto(entity.getUserId(), entity.getDate(), entity.getTargetUserCalories());
        cartDto.setId(entity.getId());
        cartDto.setMeals(mealDtoConverter.listToDto(entity.getMeals()));
        cartDto.setProducts(productDtoConverter.listToDto(entity.getProducts()));
        cartDto.setItemCounter(entity.getMeals().size() + entity.getProducts().size());

        var productsFromMeals = retrieveProductsFromMeals(cartDto.getMeals());
        var allProducts = sumProductsAndProductsFromMeals(cartDto.getProducts(), productsFromMeals);
        cartDto.setAllProducts(allProducts);

        return cartDto;
    }

    @Override
    public Cart fromDto(CartDto dto) {
        var cart = new Cart(dto.getUserId(), dto.getDate(), dto.getTargetUserCalories());
        cart.setId(dto.getId());
        cart.setMeals(mealDtoConverter.listFromDto(dto.getMeals()));
        cart.setProducts(productDtoConverter.listFromDto(dto.getProducts()));
        return cart;
    }

    private ArrayList<ProductDto> retrieveProductsFromMeals(ArrayList<MealDto> mealDtos) {
        var productsFromMeals = new ArrayList<ProductDto>();
        mealDtos.forEach(mealDto -> productsFromMeals.addAll(mealDto.getProducts()));
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

    private void sumAmountDuplicatedProduct(ArrayList<ProductDto> mainProductList, ProductDto productToAdd) {
        int indexOfDuplicatedProduct = mainProductList.indexOf(productToAdd);
        var duplicatedProduct = mainProductList.remove(indexOfDuplicatedProduct);

        var sumDuplicated = new ProductDto();
        sumDuplicated.setAmount(duplicatedProduct.getAmount() + productToAdd.getAmount());
        sumDuplicated.setProtein(duplicatedProduct.getProtein() + productToAdd.getProtein());
        sumDuplicated.setCarbohydrateExchange(duplicatedProduct.getCarbohydrateExchange() + productToAdd.getCarbohydrateExchange());
        sumDuplicated.setCarbohydrate(duplicatedProduct.getCarbohydrate() + productToAdd.getCarbohydrate());
        sumDuplicated.setFat(duplicatedProduct.getFat() + productToAdd.getFat());
        sumDuplicated.setFibre(duplicatedProduct.getFibre() + productToAdd.getFibre());
        sumDuplicated.setDescription(productToAdd.getDescription());
        sumDuplicated.setUserId(productToAdd.getUserId());
        sumDuplicated.setProteinAndFatEquivalent(duplicatedProduct.getProteinAndFatEquivalent() + productToAdd.getProteinAndFatEquivalent());
        sumDuplicated.setId(productToAdd.getId());
        sumDuplicated.setImageUrl(productToAdd.getImageUrl());
        sumDuplicated.setKcal(duplicatedProduct.getKcal() + productToAdd.getKcal());
        sumDuplicated.setName(productToAdd.getName());

        mainProductList.add(sumDuplicated);
    }
}
