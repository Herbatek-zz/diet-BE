package com.piotrek.diet.controller;

import com.piotrek.diet.DietApplication;
import com.piotrek.diet.config.DataBaseConfigIntegrationTests;
import com.piotrek.diet.model.Product;
import com.piotrek.diet.model.dto.ProductDto;
import com.piotrek.diet.model.dto.converter.ProductDtoConverter;
import com.piotrek.diet.service.ProductService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {DietApplication.class, DataBaseConfigIntegrationTests.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductControllerTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductDtoConverter productDtoConverter;

    private WebTestClient webTestClient;

    private Product product1;
    private Product product2;
    private ProductDto productDto1;
    private ProductDto productDto2;

    @BeforeEach
    void setUp() {
        productService.deleteAll().block();
        createUsers();
        webTestClient = WebTestClient.bindToController(new ProductController(productService, productDtoConverter)).build();
    }

    @AfterAll
    void tearDown() {
        productService.deleteAll().block();
    }

    @Test
    void findById() {
    }

    @Test
    void deleteById() {
    }

    private void createUsers() {
        product1 = new Product();
        product1.setName("Banana");
        product1.setDescription("Very yellow, so sweet, such tasty");
        product1.setImageUrl("http://banana-so-good.com");
        product1.setProtein(1.0);
        product1.setCarbohydrate(21.8);
        product1.setFat(0.3);
        product1.setFibre(1.7);
        product1.setKcal(97.0);
        product1.setCarbohydrateExchange(2.1);
        product1.setProteinAndFatEquivalent(0.067);

        product2 = new Product();
        product2.setName("Tesco Chleb Razowy");
        product2.setDescription("gwarancja 100% satysfakcji, 100% żytni, wypiekany na naturalnym zakwasie, bez substancji konserwujących i polepszaczy, Jestem z Polski");
        product2.setImageUrl("https://secure.ce-tescoassets.com/assets/PL/924/5051007036924/ShotType1_328x328.jpg");
        product2.setProtein(4.9);
        product2.setCarbohydrate(43.0);
        product2.setFat(1.6);
        product2.setFibre(0.0);
        product2.setKcal(216.0);
        product2.setCarbohydrateExchange(4.3);
        product2.setProteinAndFatEquivalent(0.34);

        product1 = productService.save(product1).block();
        product2 = productService.save(product2).block();

        productDto1 = productDtoConverter.toDto(product1);
        productDto2 = productDtoConverter.toDto(product2);
    }
}