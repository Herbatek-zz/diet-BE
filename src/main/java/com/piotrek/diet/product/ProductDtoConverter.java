package com.piotrek.diet.product;

import com.piotrek.diet.helpers.DtoConverter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductDtoConverter implements DtoConverter<Product, ProductDto> {

    @Override
    public ProductDto toDto(Product product) {
        var productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setDescription(product.getDescription());
        productDto.setImageUrl(product.getImageUrl());
        productDto.setPrivate(product.isPrivate());
        productDto.setProtein(product.getProtein());
        productDto.setCarbohydrate(product.getCarbohydrate());
        productDto.setFat(product.getFat());
        productDto.setFibre(product.getFibre());
        productDto.setKcal(product.getKcal());
        productDto.setAmount(product.getAmount());
        productDto.setCarbohydrateExchange(product.getCarbohydrateExchange());
        productDto.setProteinAndFatEquivalent(product.getProteinAndFatEquivalent());
        productDto.setUserId(product.getUserId());
        return productDto;
    }

    @Override
    public Product fromDto(ProductDto productDto) {
        var product = new Product();
        product.setId(productDto.getId());
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setImageUrl(productDto.getImageUrl());
        product.setPrivate(productDto.isPrivate());
        product.setProtein(productDto.getProtein());
        product.setCarbohydrate(productDto.getCarbohydrate());
        product.setFat(productDto.getFat());
        product.setFibre(productDto.getFibre());
        product.setKcal(productDto.getKcal());
        product.setAmount(productDto.getAmount());
        product.setCarbohydrateExchange(productDto.getCarbohydrateExchange());
        product.setProteinAndFatEquivalent(productDto.getProteinAndFatEquivalent());
        product.setUserId(productDto.getUserId());
        return product;
    }

    public ArrayList<ProductDto> listToDto(List<Product> products) {
        return products
                .stream()
                .map(this::toDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Product> listFromDto(List<ProductDto> productDtos) {
        return productDtos
                .stream()
                .map(this::fromDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
