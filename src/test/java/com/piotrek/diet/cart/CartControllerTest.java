package com.piotrek.diet.cart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.piotrek.diet.DietApplication;
import com.piotrek.diet.helpers.config.DataBaseForIntegrationTestsConfiguration;
import com.piotrek.diet.helpers.exceptions.GlobalExceptionHandler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {DietApplication.class, DataBaseForIntegrationTestsConfiguration.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CartControllerTest {


    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    @Autowired
    private CartFacade cartFacade;

    @Autowired
    private CartService cartService;

    private WebTestClient webTestClient;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void beforeEach() {
        cartService.deleteAll().block();
        webTestClient = WebTestClient
                .bindToController(new CartController(cartFacade))
                .controllerAdvice(globalExceptionHandler)
                .build();
    }

    @AfterAll
    void afterAll() {
        cartService.deleteAll().block();
    }


}
