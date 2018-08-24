package com.piotrek.diet.product;

import com.piotrek.diet.DietApplication;
import com.piotrek.diet.helpers.config.DataBaseConfigIntegrationTests;
import com.piotrek.diet.helpers.exceptions.GlobalExceptionHandler;
import com.piotrek.diet.sample.ProductSample;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {DietApplication.class, DataBaseConfigIntegrationTests.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductControllerTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductDtoConverter productDtoConverter;

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    private WebTestClient webTestClient;

    private Product product1;
    private Product product2;
    private ProductDto productDto1;
    private ProductDto productDto2;

    @BeforeEach
    void setUp() {
        productService.deleteAll().block();
        createUsers();
        webTestClient = WebTestClient
                .bindToController(new ProductController(productService, productDtoConverter))
                .controllerAdvice(globalExceptionHandler)
                .build();
    }

    @AfterAll
    void afterAll() {
        productService.deleteAll().block();
    }

    @Test
    void findById() {
        webTestClient.get().uri("/products/" + product1.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(ProductDto.class)
                .isEqualTo(productDto1);
    }

    @Test
    void deleteById() {
        webTestClient.delete().uri("/products/" + product1.getId())
                .exchange()
                .expectStatus().isNoContent();
    }

    private void createUsers() {
        product1 = ProductSample.bananaWithoutId();
        product2 = ProductSample.breadWithoutId();

        product1 = productService.save(product1).block();
        product2 = productService.save(product2).block();

        productDto1 = productDtoConverter.toDto(product1);
        productDto2 = productDtoConverter.toDto(product2);
    }
}