package com.piotrek.diet.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.piotrek.diet.DietApplication;
import com.piotrek.diet.helpers.Page;
import com.piotrek.diet.helpers.PrincipalProvider;
import com.piotrek.diet.helpers.ProductSample;
import com.piotrek.diet.helpers.config.DataBaseForIntegrationTestsConfiguration;
import com.piotrek.diet.helpers.exceptions.GlobalExceptionHandler;
import com.piotrek.diet.helpers.exceptions.NotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {DietApplication.class, DataBaseForIntegrationTestsConfiguration.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductControllerTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    private Product product;
    private Product product2;
    private ProductDto productDto;
    private ProductDto productDto2;

    @BeforeEach
    void beforeEach() {
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
    @DisplayName("Find product by id, when found, then return")
    void findById_whenFound_thenReturn() throws JsonProcessingException {
        final var URI = "/products/" + product.getId();
        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(product));
    }

    @Test
    @DisplayName("Find product by id, when not found, then throw NotFoundException")
    void findById_whenNotFound_thenThrowNotFoundException() {
        final var URI = "/products/" + UUID.randomUUID().toString();
        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(NotFoundException.class);
    }

    @Test
    @DisplayName("Search products by name, when two products and query matches for one of them, then return page with one product")
    void searchByName_whenTwoProductsAndQueryMatchForOne_thenReturnPageWithOne() throws JsonProcessingException {
        final var URI = "/products/search?query=" + product.getName();
        final var expected = new Page<>(List.of(productDto), 0, 10, 1);

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    @DisplayName("Search products by name, when two products and query is missing, then return page with all products")
    void searchByName_whenTwoProductAndNoQuery_thenReturnPageableWithAllProduct() throws JsonProcessingException {
        final var URI = "/products/search";
        final var expected = new Page<>(List.of(product, product2), 0, 10, 2);

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    @DisplayName("Search products by name, when no products and no query, then return empty page")
    void searchByName_whenNoProductsAndNoQuery_thenReturnEmptyPage() throws JsonProcessingException {
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
    @DisplayName("Search products by name, when two products and query doesn't match them, then return empty page")
    void searchByName_whenTwoProductsAndWrongQuery_thenReturnEmptyPage() throws JsonProcessingException {
        final var URI = "/products/search?query=lol123";
        final var expected = new Page<>(new ArrayList<>(), 0, 10, 0);

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    @DisplayName("Search products by name, when two products, no query and pageSize=1, then return page with one product")
    void searchByName_whenTwoProductsNoQueryAndPageSizeEqualOne_thenReturnPageWithOneProduct() throws JsonProcessingException {
        final var URI = "/products/search?size=1";
        final var expected = new Page<>(List.of(product), 0, 1, 2);

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    @DisplayName("Search products by name, when two products, no query, pageSize=1 and page=1, then return page with one product")
    void searchByName_whenTwoProductsNoQueryPageSizeEqualOneAndPageTwo_thenReturnSecondPageWithOneProduct() throws JsonProcessingException {
        final var URI = "/products/search?size=1&page=1";
        final var expected = new Page<>(List.of(product2), 1, 1, 2);

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    @DisplayName("Find all products, when no products, then return empty page")
    void findAll_whenNoProducts_thenReturnEmptyPage() throws JsonProcessingException {
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
    @DisplayName("Find all products, when two products, then return page with two products")
    void findAll_whenTwoProducts_thenReturnPageWithTwoProducts() throws JsonProcessingException {
        final var URI = "/products";
        final var expected = new Page<>(List.of(product, product2), 0, 10, 2);

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    @DisplayName("Find all products, when two products, pageSize=1 and page=1, then return second page with one product")
    void findAll_whenPageNumberOnePageSizeOneTotalElementsTwo_returnSecondPageWithOneProduct() throws JsonProcessingException {
        final var URI = "/products?page=1&size=1";
        final var expected = new Page<>(List.of(product2), 1, 1, 2);

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    void deleteById() {
        PrincipalProvider.provide(product.getUserId());
        final var URI = "/products/" + product.getId();
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
        product = ProductSample.bananaWithId();
        product2 = ProductSample.breadWithId();

        productDto = productService.save(product).block();
        productDto2 = productService.save(product2).block();
    }
}