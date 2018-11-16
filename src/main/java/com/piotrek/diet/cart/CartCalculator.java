package com.piotrek.diet.cart;

import com.piotrek.diet.meal.MealDto;
import com.piotrek.diet.product.ProductDto;
import lombok.RequiredArgsConstructor;
import org.decimal4j.util.DoubleRounder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class CartCalculator {

    private final DoubleRounder doubleRounder;

    void calculateCartInfo(CartDto cartDto) {
        cartDto.getAllProducts()
                .forEach(productDto -> {
                    cartDto.setProtein(doubleRounder.round(cartDto.getProtein() + productDto.getProtein()));
                    cartDto.setCarbohydrate(doubleRounder.round(cartDto.getCarbohydrate() + productDto.getCarbohydrate()));
                    cartDto.setFat(doubleRounder.round(cartDto.getFat() + productDto.getFat()));
                    cartDto.setKcal(doubleRounder.round(cartDto.getKcal() + productDto.getKcal()));
                });
    }

    ArrayList<ProductDto> retrieveProductsFromMeals(ArrayList<MealDto> mealDtos) {
        var productsFromMeals = new ArrayList<ProductDto>();
        mealDtos.forEach(mealDto -> productsFromMeals.addAll(mealDto.getProducts()));
        return productsFromMeals;
    }

    ArrayList<ProductDto> sumProductsAndProductsFromMeals(ArrayList<ProductDto> products, ArrayList<ProductDto> productsFromMeals) {
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
