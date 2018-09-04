package com.piotrek.diet.product;

import com.piotrek.diet.sample.ProductSample;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProductDtoConverterTest {

    private ProductDtoConverter productDtoConverter = new ProductDtoConverter();

    private Product product = ProductSample.bananaWithId();
    private ProductDto productDto = ProductSample.bananaWithIdDto();

    @Test
    void toDto() {
        var convertedProduct = productDtoConverter.toDto(product);

        assertNotNull(convertedProduct);
        assertAll(
                () -> assertEquals(productDto.getId(), convertedProduct.getId()),
                () -> assertEquals(productDto.getDescription(), convertedProduct.getDescription()),
                () -> assertEquals(productDto.getImageUrl(), convertedProduct.getImageUrl()),
                () -> assertEquals(productDto.getProtein(), convertedProduct.getProtein()),
                () -> assertEquals(productDto.getFat(), convertedProduct.getFat()),
                () -> assertEquals(productDto.getCarbohydrate(), convertedProduct.getCarbohydrate()),
                () -> assertEquals(productDto.getFibre(), convertedProduct.getFibre()),
                () -> assertEquals(productDto.getKcal(), convertedProduct.getKcal()),
                () -> assertEquals(productDto.getAmount(), convertedProduct.getAmount()),
                () -> assertEquals(productDto.getCarbohydrateExchange(), convertedProduct.getCarbohydrateExchange()),
                () -> assertEquals(productDto.getProteinAndFatEquivalent(), convertedProduct.getProteinAndFatEquivalent())
        );
    }

    @Test
    void fromDto() {
        var convertedProduct = productDtoConverter.fromDto(productDto);

        assertNotNull(convertedProduct);
        assertAll(
                () -> assertEquals(product.getId(), convertedProduct.getId()),
                () -> assertEquals(product.getDescription(), convertedProduct.getDescription()),
                () -> assertEquals(product.getImageUrl(), convertedProduct.getImageUrl()),
                () -> assertEquals(product.getProtein(), convertedProduct.getProtein()),
                () -> assertEquals(product.getFat(), convertedProduct.getFat()),
                () -> assertEquals(product.getCarbohydrate(), convertedProduct.getCarbohydrate()),
                () -> assertEquals(product.getFibre(), convertedProduct.getFibre()),
                () -> assertEquals(product.getKcal(), convertedProduct.getKcal()),
                () -> assertEquals(product.getAmount(), convertedProduct.getAmount()),
                () -> assertEquals(product.getCarbohydrateExchange(), convertedProduct.getCarbohydrateExchange()),
                () -> assertEquals(product.getProteinAndFatEquivalent(), convertedProduct.getProteinAndFatEquivalent())
        );
    }

    @Test
    void listToDto() {
        var products = new ArrayList<Product>(2);
        products.add(ProductSample.bananaWithId());
        products.add(ProductSample.breadWithId());

        List<ProductDto> convertedList = productDtoConverter.listToDto(products);

        assertNotNull(convertedList);
        assertNotNull(convertedList.get(0));
        assertNotNull(convertedList.get(1));
        assertAll(
                () -> assertEquals(products.size(), convertedList.size()),
                () -> assertEquals(products.get(0).getId(), convertedList.get(0).getId()),
                () -> assertEquals(products.get(1).getId(), convertedList.get(1).getId())
        );
    }
}