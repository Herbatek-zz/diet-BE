package com.piotrek.diet.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.piotrek.diet.DietApplication;
import com.piotrek.diet.helpers.Page;
import com.piotrek.diet.helpers.config.DataBaseForIntegrationTestsConfiguration;
import com.piotrek.diet.helpers.exceptions.GlobalExceptionHandler;
import com.piotrek.diet.helpers.exceptions.NotFoundException;
import com.piotrek.diet.sample.ProductSample;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {DietApplication.class, DataBaseForIntegrationTestsConfiguration.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductControllerTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductDtoConverter productDtoConverter;

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    private WebTestClient webTestClient;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Product product1;
    private Product product2;
    private ProductDto productDto1;

    @BeforeEach
    void setUp() {
        productService.deleteAll().block();
        createProducts();
        webTestClient = WebTestClient
                .bindToController(new ProductController(productService))
                .controllerAdvice(globalExceptionHandler)
                .build();
    }

    @AfterAll
    void afterAll() {
        productService.deleteAll().block();
    }

    @Test
    @DisplayName("When findById and found a product, then return this product")
    void findById_whenFound_thenReturnProduct() {
        final var URI = "/products/" + product1.getId();
        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(ProductDto.class)
                .isEqualTo(productDto1);
    }

    @Test
    @DisplayName("When findById and not found a product, then throw NotFoundException")
    void findById_whenNotFound_thenThrowNotFoundException() {
        final var URI = "/products/badId";
        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(NotFoundException.class);
    }

    @Test
    void searchByName_when2ProductsAndQueryOneOfThem_thenReturnPageableWithOneProduct() throws JsonProcessingException {
        final var URI = "/products/search?query=" + product1.getName();
        final var expected = new Page<>(List.of(productDtoConverter.toDto(product1)), 0, 10, 1);

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    void searchByName_when2ProductsAndNoQuery_thenReturnPageableWithAllProduct() throws JsonProcessingException {
        final var URI = "/products/search";
        final var expected = new Page<>(new ArrayList<>(Arrays.asList(product1, product2)), 0, 10, 2);

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    void searchByName_when0ProductsAndNoQuery_thenReturnEmptyPageable() throws JsonProcessingException {
        final var URI = "/products/search";
        final var expected = new Page<>(new ArrayList<>(), 0, 10, 0);

        productService.deleteAll().block();

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    void searchByName_when2ProductsAndWrongQuery_thenReturnEmptyPageable() throws JsonProcessingException {
        final var URI = "/products/search?query=lol123";
        final var expected = new Page<>(new ArrayList<>(), 0, 10, 0);

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    void searchByName_when2ProductsAndNoQueryAndPageSize1_thenReturnFirstPageWithOneProduct() throws JsonProcessingException {
        final var URI = "/products/search?size=1";
        final var expected = new Page<>(List.of(product1), 0, 1, 2);

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }


    @Test
    void findAll_whenDefaultParamsTotalElements0_thenReturnPageSupportWithoutContent() throws JsonProcessingException {
        final var URI = "/products";
        final var expected = new Page<ProductDto>(new ArrayList<>(), 0, 10, 0);

        productService.deleteAll().block();

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    void findAll_whenDefaultParamsTotalElements2_thenReturnPageSupportWith2Products() throws JsonProcessingException {
        final var URI = "/products";
        final var expected = new Page<>(new ArrayList<>(Arrays.asList(product1, product2)), 0, 10, 2);

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    void findAll_whenPageNumber1PageSize1TotalElements2_returnSecondPageWithOneProduct() throws JsonProcessingException {
        final var URI = "/products?page=1&size=1";
        final var expected = new Page<>(new ArrayList<>(Arrays.asList(product1, product2))
                .stream()
                .skip(1)
                .limit(1)
                .collect(Collectors.toList()), 1, 1, 2);

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    void deleteById() {
        final var URI = "/products/" + product1.getId();
        webTestClient.delete().uri(URI)
                .exchange()
                .expectStatus().isNoContent();

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(NotFoundException.class);
    }

    private void createProducts() {
        product1 = ProductSample.bananaWithoutId();
        product2 = ProductSample.breadWithoutId();

        product1 = productService.save(product1).block();
        product2 = productService.save(product2).block();

        productDto1 = productDtoConverter.toDto(product1);
    }
}