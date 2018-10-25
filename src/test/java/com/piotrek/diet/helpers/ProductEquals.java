package com.piotrek.diet.helpers;

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
        if (!firstProductDto.getId().equals(secondProductDto.getId())) {
            System.err.println("Product: Id is not the same");
            System.out.println(firstProductDto.getId());
            System.out.println(secondProductDto.getId());
            return false;
        }
        if (!firstProductDto.getName().equals(secondProductDto.getName())){
            System.err.println("Product: Name is not the same");
            System.out.println(firstProductDto.getName());
            System.out.println(secondProductDto.getName());
            return false;
        }
        if (!firstProductDto.getDescription().equals(secondProductDto.getDescription())){
            System.err.println("Product: Description is not the same");
            System.out.println(firstProductDto.getDescription());
            System.out.println(secondProductDto.getDescription());
            return false;
        }
        if (!firstProductDto.getImageUrl().equals(secondProductDto.getImageUrl())){
            System.err.println("Product: ImageUrl is not the same");
            System.out.println(firstProductDto.getImageUrl());
            System.out.println(secondProductDto.getImageUrl());
            return false;
        }
        if (!firstProductDto.getUserId().equals(secondProductDto.getUserId())){
            System.err.println("Product: UserId is not the same");
            System.out.println(firstProductDto.getUserId());
            System.out.println(secondProductDto.getUserId());
            return false;
        }
        if (firstProductDto.getProtein() != secondProductDto.getProtein()){
            System.err.println("Product: Protein is not the same");
            System.out.println(firstProductDto.getProtein());
            System.out.println(secondProductDto.getProtein());
            return false;
        }
        if (firstProductDto.getProteinAndFatEquivalent() != secondProductDto.getProteinAndFatEquivalent()){
            System.err.println("Product: ProteinAndFatEquivalent is not the same");
            System.out.println(firstProductDto.getProteinAndFatEquivalent());
            System.out.println(secondProductDto.getProteinAndFatEquivalent());
            return false;
        }
        if (firstProductDto.getCarbohydrate() != secondProductDto.getCarbohydrate()){
            System.err.println("Product: Carbohydrate is not the same");
            System.out.println(firstProductDto.getCarbohydrate());
            System.out.println(secondProductDto.getCarbohydrate());
            return false;
        }
        if (firstProductDto.getCarbohydrateExchange() != secondProductDto.getCarbohydrateExchange()){
            System.err.println("Product: CarbohydrateExchange is not the same");
            System.out.println(firstProductDto.getCarbohydrateExchange());
            System.out.println(secondProductDto.getCarbohydrateExchange());
            return false;
        }
        if (firstProductDto.getFat() != secondProductDto.getFat()){
            System.err.println("Product: Fat is not the same");
            System.out.println(firstProductDto.getFat());
            System.out.println(secondProductDto.getFat());
            return false;
        }
        if (firstProductDto.getFibre() != secondProductDto.getFibre()){
            System.err.println("Product: Fibre is not the same");
            System.out.println(firstProductDto.getFibre());
            System.out.println(secondProductDto.getFibre());
            return false;
        }
        if (firstProductDto.getKcal() != secondProductDto.getKcal()){
            System.err.println("Product: Kcal are not the same");
            System.out.println(firstProductDto.getKcal());
            System.out.println(secondProductDto.getKcal());
            return false;
        }
        if (firstProductDto.getAmount() != secondProductDto.getAmount()){
            System.err.println("Product: Amount is not the same");
            System.out.println(firstProductDto.getAmount());
            System.out.println(secondProductDto.getAmount());
            return false;
        }
        return true;
    }
}
