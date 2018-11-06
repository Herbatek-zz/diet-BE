package com.piotrek.diet.product;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.piotrek.diet.helpers.AssertEqualAllFields.assertProductFields;
import static com.piotrek.diet.helpers.ProductSample.*;
import static org.junit.jupiter.api.Assertions.*;

class ProductDtoConverterTest {

    private ProductDtoConverter productDtoConverter = new ProductDtoConverter();

    private Product product = bananaWithId();
    private ProductDto productDto = bananaWithIdDto();

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
        var products = List.of(bananaWithId(), breadWithId());
        var convertedList = productDtoConverter.listToDto(products);

        assertAll(
                () -> assertProductFields(bananaWithIdDto(), convertedList.get(0)),
                () -> assertProductFields(breadWithIdDto(), convertedList.get(1))
        );
    }

    @Test
    void listFromDto() {
        var productDtos = List.of(bananaWithIdDto(), breadWithIdDto());
        var convertedList = productDtoConverter.listFromDto(productDtos);

        assertAll(
                () -> assertProductFields(bananaWithId(), convertedList.get(0)),
                () -> assertProductFields(breadWithId(), convertedList.get(1))
        );
    }


}