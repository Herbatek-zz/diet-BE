package com.piotrek.diet.sample;

import com.piotrek.diet.product.Product;
import com.piotrek.diet.product.ProductDto;

public class ProductEquals {

    public static boolean productEquals(Product firstProduct, Product secondProduct) {
        if (!firstProduct.getId().equals(secondProduct.getId()))
            return false;
        if (!firstProduct.getName().equals(secondProduct.getName()))
            return false;
        if (!firstProduct.getDescription().equals(secondProduct.getDescription()))
            return false;
        if (!firstProduct.getImageUrl().equals(secondProduct.getImageUrl()))
            return false;
        if (!firstProduct.getUserId().equals(secondProduct.getUserId()))
            return false;
        if (firstProduct.getProtein() != secondProduct.getProtein())
            return false;
        if (firstProduct.getProteinAndFatEquivalent() != secondProduct.getProteinAndFatEquivalent())
            return false;
        if (firstProduct.getCarbohydrate() != secondProduct.getCarbohydrate())
            return false;
        if (firstProduct.getCarbohydrateExchange() != secondProduct.getCarbohydrateExchange())
            return false;
        if (firstProduct.getFat() != secondProduct.getFat())
            return false;
        if (firstProduct.getFibre() != secondProduct.getFibre())
            return false;
        if (firstProduct.getKcal() != secondProduct.getKcal())
            return false;
        if (firstProduct.getAmount() != secondProduct.getAmount())
            return false;
        return true;
    }

    public static boolean productDtoEquals(ProductDto firstProductDto, ProductDto secondProductDto) {
        if (!firstProductDto.getId().equals(secondProductDto.getId()))
            return false;
        if (!firstProductDto.getName().equals(secondProductDto.getName()))
            return false;
        if (!firstProductDto.getDescription().equals(secondProductDto.getDescription()))
            return false;
        if (!firstProductDto.getImageUrl().equals(secondProductDto.getImageUrl()))
            return false;
        if (!firstProductDto.getUserId().equals(secondProductDto.getUserId()))
            return false;
        if (firstProductDto.getProtein() != secondProductDto.getProtein())
            return false;
        if (firstProductDto.getProteinAndFatEquivalent() != secondProductDto.getProteinAndFatEquivalent())
            return false;
        if (firstProductDto.getCarbohydrate() != secondProductDto.getCarbohydrate())
            return false;
        if (firstProductDto.getCarbohydrateExchange() != secondProductDto.getCarbohydrateExchange())
            return false;
        if (firstProductDto.getFat() != secondProductDto.getFat())
            return false;
        if (firstProductDto.getFibre() != secondProductDto.getFibre())
            return false;
        if (firstProductDto.getKcal() != secondProductDto.getKcal())
            return false;
        if (firstProductDto.getAmount() != secondProductDto.getAmount())
            return false;
        return true;
    }
}
