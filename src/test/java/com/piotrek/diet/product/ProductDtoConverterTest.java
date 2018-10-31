package com.piotrek.diet.product;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.piotrek.diet.helpers.ProductSample.*;
import static org.junit.jupiter.api.Assertions.*;

class ProductDtoConverterTest {

    private ProductDtoConverter productDtoConverter = new ProductDtoConverter();

    private Product product = bananaWithId();
    private ProductDto productDto = bananaWithIdDto();

    @Test
    void toDto() {
        var convertedProduct = productDtoConverter.toDto(product);
        this.assertEqualsAllProductFields(productDto, convertedProduct);
    }

    @Test
    void fromDto() {
        var convertedProduct = productDtoConverter.fromDto(productDto);
        this.assertEqualsAllProductFields(product, convertedProduct);
    }

    @Test
    void listToDto() {
        var products = List.of(bananaWithId(), breadWithId());
        var convertedList = productDtoConverter.listToDto(products);

        assertAll(
                () -> this.assertEqualsAllProductFields(bananaWithIdDto(), convertedList.get(0)),
                () -> this.assertEqualsAllProductFields(breadWithIdDto(), convertedList.get(1))
        );
    }

    @Test
    void listFromDto() {
        var productDtos = List.of(bananaWithIdDto(), breadWithIdDto());
        var convertedList = productDtoConverter.listFromDto(productDtos);

        assertAll(
                () -> this.assertEqualsAllProductFields(bananaWithId(), convertedList.get(0)),
                () -> this.assertEqualsAllProductFields(breadWithId(), convertedList.get(1))
        );
    }

    private void assertEqualsAllProductFields(Product expected, Product actual) {
        assertNotNull(actual);
        assertAll(
                () -> assertEquals(expected.getId(), actual.getId()),
                () -> assertEquals(expected.getName(), actual.getName()),
                () -> assertEquals(expected.getDescription(), actual.getDescription()),
                () -> assertEquals(expected.getImageUrl(), actual.getImageUrl()),
                () -> assertEquals(expected.getProtein(), actual.getProtein()),
                () -> assertEquals(expected.getFat(), actual.getFat()),
                () -> assertEquals(expected.getCarbohydrate(), actual.getCarbohydrate()),
                () -> assertEquals(expected.getFibre(), actual.getFibre()),
                () -> assertEquals(expected.getKcal(), actual.getKcal()),
                () -> assertEquals(expected.getAmount(), actual.getAmount()),
                () -> assertEquals(expected.getCarbohydrateExchange(), actual.getCarbohydrateExchange()),
                () -> assertEquals(expected.getProteinAndFatEquivalent(), actual.getProteinAndFatEquivalent()),
                () -> assertEquals(expected.getUserId(), actual.getUserId())
        );
    }

    private void assertEqualsAllProductFields(ProductDto expected, ProductDto actual) {
        assertNotNull(actual);
        assertAll(
                () -> assertEquals(expected.getId(), actual.getId()),
                () -> assertEquals(expected.getName(), actual.getName()),
                () -> assertEquals(expected.getDescription(), actual.getDescription()),
                () -> assertEquals(expected.getImageUrl(), actual.getImageUrl()),
                () -> assertEquals(expected.getProtein(), actual.getProtein()),
                () -> assertEquals(expected.getFat(), actual.getFat()),
                () -> assertEquals(expected.getCarbohydrate(), actual.getCarbohydrate()),
                () -> assertEquals(expected.getFibre(), actual.getFibre()),
                () -> assertEquals(expected.getKcal(), actual.getKcal()),
                () -> assertEquals(expected.getAmount(), actual.getAmount()),
                () -> assertEquals(expected.getCarbohydrateExchange(), actual.getCarbohydrateExchange()),
                () -> assertEquals(expected.getProteinAndFatEquivalent(), actual.getProteinAndFatEquivalent()),
                () -> assertEquals(expected.getUserId(), actual.getUserId())
        );
    }
}