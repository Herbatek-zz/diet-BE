package com.piotrek.diet.user;

import com.piotrek.diet.DietApplication;
import com.piotrek.diet.helpers.PageSupport;
import com.piotrek.diet.helpers.config.DataBaseConfigIntegrationTests;
import com.piotrek.diet.helpers.exceptions.GlobalExceptionHandler;
import com.piotrek.diet.product.ProductDto;
import com.piotrek.diet.product.ProductDtoConverter;
import com.piotrek.diet.product.ProductFacade;
import com.piotrek.diet.sample.ProductSample;
import com.piotrek.diet.sample.UserSample;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.ArrayList;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {DietApplication.class, DataBaseConfigIntegrationTests.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDtoConverter userDtoConverter;

    @Autowired
    private ProductFacade productFacade;

    @Autowired
    private ProductDtoConverter productDtoConverter;

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    private WebTestClient webTestClient;

    private User user1;
    private UserDto userDto1;

    @BeforeEach
    void setUp() {
        userService.deleteAll().block();
        createUsers();
        webTestClient = WebTestClient
                .bindToController(new UserController(userService, userDtoConverter, productDtoConverter, productFacade))
                .controllerAdvice(globalExceptionHandler)
                .build();
    }

    @AfterAll
    void afterAll() {
        userService.deleteAll().block();
    }

    @Test
    void findUserById_whenUserExists_thenReturnUserDto() {
        webTestClient.get().uri("/users/" + user1.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(UserDto.class)
                .isEqualTo(userDto1);
    }

    @Test
    void findUserById_whenUserDoesNotExist_thenReturn404() {
        var invalidId = "iDoesNotExist";

        webTestClient.get().uri("/users/" + invalidId)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void saveProduct_whenAllRequiredField_thenSaveProductAndReturnDto() {
        ProductDto productDto = productDtoConverter.toDto(ProductSample.bananaWithId());

        webTestClient.post().uri("/users/" + user1.getId() + "/products")
                .contentType(APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(productDto))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ProductDto.class)
                .isEqualTo(productDto);
    }

    @Test
    void findUserProducts_whenUserHasNoProducts_thenReturnPageSupportWithoutElements() {
        webTestClient.get().uri("/users/" + user1.getId() + "/products")
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageSupport.class)
                .isEqualTo(new PageSupport<>(new ArrayList<>(), 0, 10, 0));
    }

    @Test
    void findUserProducts_whenUserHasProducts_thenReturnArray() {

    }

    private void createUsers() {
        user1 = UserSample.baileyWithoutId();

        user1 = userService.save(user1).block();

        userDto1 = userDtoConverter.toDto(user1);
    }
}