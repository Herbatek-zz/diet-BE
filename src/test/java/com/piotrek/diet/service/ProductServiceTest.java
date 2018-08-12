package com.piotrek.diet.service;

import com.piotrek.diet.exception.NotFoundException;
import com.piotrek.diet.model.Product;
import com.piotrek.diet.repository.ProductRepository;
import com.piotrek.diet.sample.SampleProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
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
        var id = "invalid@#@#@ID";
        Mockito.when(productRepository.findById(id)).thenReturn(Mono.empty());

        assertThrows(NotFoundException.class, () -> productService.findById(id).block());

        verify(productRepository, times(1)).findById(id);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void deleteById() {
        assertEquals(Mono.empty(), productService.deleteById(product.getId()));

        verify(productRepository, times(1)).deleteById(product.getId());
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void save() {
        Mockito.when(productRepository.save(product)).thenReturn(Mono.just(product));

        assertEquals(product, productService.save(product).block());

        verify(productRepository, times(1)).save(product);
        verifyNoMoreInteractions(productRepository);
    }

    private void createProduct() {
        product = SampleProduct.bananaWithId();
    }
}