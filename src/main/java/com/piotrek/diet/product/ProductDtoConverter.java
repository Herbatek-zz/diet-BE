package com.piotrek.diet.product;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductDtoConverter {

    public ProductDto toDto(Product product) {
        var productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setDescription(product.getDescription());
        productDto.setImageUrl(product.getImageUrl());
        productDto.setProtein(product.getProtein());
        productDto.setCarbohydrate(product.getCarbohydrate());
        productDto.setFat(product.getFat());
        productDto.setFibre(product.getFibre());
        productDto.setKcal(product.getKcal());
        productDto.setCarbohydrateExchange(product.getCarbohydrateExchange());
        productDto.setProteinAndFatEquivalent(product.getProteinAndFatEquivalent());
        return productDto;
    }

    public Product fromDto(ProductDto productDto) {
        var product = new Product();
        product.setId(productDto.getId());
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setImageUrl(productDto.getImageUrl());
        product.setProtein(productDto.getProtein());
        product.setCarbohydrate(productDto.getCarbohydrate());
        product.setFat(productDto.getFat());
        product.setFibre(productDto.getFibre());
        product.setKcal(productDto.getKcal());
        product.setCarbohydrateExchange(productDto.getCarbohydrateExchange());
        product.setProteinAndFatEquivalent(productDto.getProteinAndFatEquivalent());
        return product;
    }

    public List<ProductDto> listToDto(List<Product> products) {
        return products
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
