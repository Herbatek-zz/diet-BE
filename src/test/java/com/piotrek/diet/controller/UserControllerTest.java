package com.piotrek.diet.controller;

import com.piotrek.diet.DietApplication;
import com.piotrek.diet.config.DataBaseConfigIntegrationTests;
import com.piotrek.diet.exception.GlobalExceptionHandler;
import com.piotrek.diet.facade.ProductFacade;
import com.piotrek.diet.model.User;
import com.piotrek.diet.model.dto.ProductDto;
import com.piotrek.diet.model.dto.UserDto;
import com.piotrek.diet.model.dto.converter.ProductDtoConverter;
import com.piotrek.diet.model.dto.converter.UserDtoConverter;
import com.piotrek.diet.sample.SampleProduct;
import com.piotrek.diet.sample.SampleUser;
import com.piotrek.diet.service.UserService;
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
    private User user2;
    private UserDto userDto1;
    private UserDto userDto2;

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
        ProductDto productDto = productDtoConverter.toDto(SampleProduct.bananaWithId());

        webTestClient.post().uri("/users/" + user1.getId() + "/products")
                .contentType(APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(productDto))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ProductDto.class)
                .isEqualTo(productDto);
    }

    @Test
    void findUserProducts_whenUserHasNoProducts_thenReturnEmptyArray() {
        webTestClient.get().uri("/users/" + user1.getId() + "/products")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .isEmpty();
    }

    @Test
    void findUserProducts_whenUserHasProducts_thenReturnArray() {

    }

    private void createUsers() {
        user1 = SampleUser.baileyWithoutId();
        user2 = SampleUser.johnWithoutId();

        user1 = userService.save(user1).block();
        user2 = userService.save(user2).block();

        userDto1 = userDtoConverter.toDto(user1);
        userDto2 = userDtoConverter.toDto(user2);
    }
}