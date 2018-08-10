package com.piotrek.diet.service;

import com.piotrek.diet.model.Product;
import com.piotrek.diet.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setup() {
        createProduct();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void findById_validId_shouldReturnProduct() {
        Mockito.when(productRepository.findById(product.getId())).thenReturn(Mono.just(product));

        Product productFromDB = productService.findById(product.getId()).block();

        assertNotNull(productFromDB);
        assertAll(
                () -> assertEquals(product.getId(), productFromDB.getId()),
                () -> assertEquals(product.getName(), productFromDB.getName()),
                () -> assertEquals(product.getDescription(), productFromDB.getDescription()),
                () -> assertEquals(product.getImageUrl(), productFromDB.getImageUrl()),
                () -> assertEquals(product.getProtein(), productFromDB.getProtein()),
                () -> assertEquals(product.getCarbohydrate(), productFromDB.getCarbohydrate()),
                () -> assertEquals(product.getFat(), productFromDB.getFat()),
                () -> assertEquals(product.getFibre(), productFromDB.getFibre()),
                () -> assertEquals(product.getKcal(), productFromDB.getKcal()),
                () -> assertEquals(product.getCarbohydrateExchange(), productFromDB.getCarbohydrateExchange()),
                () -> assertEquals(product.getProteinAndFatEquivalent(), productFromDB.getProteinAndFatEquivalent())
        );

        verify(productRepository, times(1)).findById(product.getId());
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void findById_invalidId_shouldThrowNotFoundException() {

    }

    @Test
    void deleteById() {
        assertEquals(Mono.empty(), productService.deleteById(product.getId()));

        verify(productRepository, times(1)).deleteById(product.getId());
        verifyNoMoreInteractions(productRepository);
    }

    private void createProduct() {
        product = new Product();
        product.setId("ProductWow123");
        product.setName("Banana");
        product.setDescription("Very yellow, so sweet, such tasty");
        product.setImageUrl("http://banana-so-good.com");
        product.setProtein(1.0);
        product.setCarbohydrate(21.8);
        product.setFat(0.3);
        product.setFibre(1.7);
        product.setKcal(97.0);
        product.setCarbohydrateExchange(2.1);
        product.setProteinAndFatEquivalent(0.067);
    }
}