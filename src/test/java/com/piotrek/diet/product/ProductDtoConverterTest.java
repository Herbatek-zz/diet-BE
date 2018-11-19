package com.piotrek.diet.product;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.piotrek.diet.helpers.AssertEqualAllFields.assertProductFields;
import static com.piotrek.diet.helpers.ProductSample.*;
import static org.junit.jupiter.api.Assertions.*;

class ProductDtoConverterTest {

    private ProductDtoConverter productDtoConverter = new ProductDtoConverter();

    private Product product = banana();
    private ProductDto productDto = bananaDto();

    @Test
    void toDto() {
        var convertedProduct = productDtoConverter.toDto(product);
        assertProductFields(productDto, convertedProduct);
    }

    @Test
    void fromDto() {
        var convertedProduct = productDtoConverter.fromDto(productDto);
        assertProductFields(product, convertedProduct);
    }

    @Test
    void listToDto() {
        var products = List.of(banana(), bread());
        var convertedList = productDtoConverter.listToDto(products);

        assertAll(
                () -> assertProductFields(bananaDto(), convertedList.get(0)),
                () -> assertProductFields(breadDto(), convertedList.get(1))
        );
    }

    @Test
    void listFromDto() {
        var productDtos = List.of(bananaDto(), breadDto());
        var convertedList = productDtoConverter.listFromDto(productDtos);

        assertAll(
                () -> assertProductFields(banana(), convertedList.get(0)),
                () -> assertProductFields(bread(), convertedList.get(1))
        );
    }


}