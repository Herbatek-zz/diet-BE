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

    CartDto calculateCartInfo(CartDto cartDto) {
        var productsFromMeals = retrieveProductsFromMeals(cartDto.getMeals());

        var allProducts = new ArrayList<ProductDto>();
        allProducts.addAll(cartDto.getProducts());
        allProducts.addAll(productsFromMeals);

        var allProductsWithoutDuplicates = reduceDuplicates(allProducts);
        cartDto.setAllProducts(allProductsWithoutDuplicates);

        cartDto.getAllProducts()
                .forEach(productDto -> {
                    cartDto.setProtein(doubleRounder.round(cartDto.getProtein() + productDto.getProtein()));
                    cartDto.setCarbohydrate(doubleRounder.round(cartDto.getCarbohydrate() + productDto.getCarbohydrate()));
                    cartDto.setFat(doubleRounder.round(cartDto.getFat() + productDto.getFat()));
                    cartDto.setKcal(doubleRounder.round(cartDto.getKcal() + productDto.getKcal()));
                });

        return cartDto;
    }

    private ArrayList<ProductDto> retrieveProductsFromMeals(ArrayList<MealDto> mealDtos) {
        var productsFromMeals = new ArrayList<ProductDto>();
        mealDtos.forEach(mealDto -> productsFromMeals.addAll(mealDto.getProducts()));
        return productsFromMeals;
    }

    private ArrayList<ProductDto> reduceDuplicates(ArrayList<ProductDto> allProducts) {
        var productsWithoutDuplicates = new ArrayList<ProductDto>();

        allProducts.forEach(productToAdd -> {
            if (productsWithoutDuplicates.contains(productToAdd))
                productToAdd = calculateDuplicatedProduct(productsWithoutDuplicates, productToAdd);
            productsWithoutDuplicates.add(productToAdd);
        });
        return productsWithoutDuplicates;
    }

    private ProductDto calculateDuplicatedProduct(ArrayList<ProductDto> withoutDuplicates, ProductDto duplicated) {
        int indexOfDuplicatedProduct = withoutDuplicates.indexOf(duplicated);
        var duplicatedProduct = withoutDuplicates.remove(indexOfDuplicatedProduct);

        var productToAdd = new ProductDto();
        productToAdd.setAmount(duplicatedProduct.getAmount() + duplicated.getAmount());
        productToAdd.setProtein(duplicatedProduct.getProtein() + duplicated.getProtein());
        productToAdd.setCarbohydrateExchange(duplicatedProduct.getCarbohydrateExchange() + duplicated.getCarbohydrateExchange());
        productToAdd.setCarbohydrate(duplicatedProduct.getCarbohydrate() + duplicated.getCarbohydrate());
        productToAdd.setFat(duplicatedProduct.getFat() + duplicated.getFat());
        productToAdd.setFibre(duplicatedProduct.getFibre() + duplicated.getFibre());
        productToAdd.setDescription(duplicated.getDescription());
        productToAdd.setUserId(duplicated.getUserId());
        productToAdd.setProteinAndFatEquivalent(duplicatedProduct.getProteinAndFatEquivalent() + duplicated.getProteinAndFatEquivalent());
        productToAdd.setId(duplicated.getId());
        productToAdd.setImageUrl(duplicated.getImageUrl());
        productToAdd.setKcal(duplicatedProduct.getKcal() + duplicated.getKcal());
        productToAdd.setName(duplicated.getName());

        return productToAdd;
    }
}
