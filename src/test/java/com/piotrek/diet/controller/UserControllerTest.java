package com.piotrek.diet.controller;

import com.piotrek.diet.DietApplication;
import com.piotrek.diet.config.DataBaseConfigIntegrationTests;
import com.piotrek.diet.model.User;
import com.piotrek.diet.model.dto.UserDto;
import com.piotrek.diet.model.dto.converter.UserDtoConverter;
import com.piotrek.diet.service.UserService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {DietApplication.class, DataBaseConfigIntegrationTests.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDtoConverter userDtoConverter;

    private WebTestClient webTestClient;

    private User user1;
    private User user2;
    private UserDto userDto1;
    private UserDto userDto2;

    @BeforeEach
    void setUp() {
        userService.deleteAll().block();
        createUsers();
        webTestClient = WebTestClient.bindToController(new UserController(userService, userDtoConverter)).build();
    }

    @AfterAll
    void tearDown() {
        userService.deleteAll().block();
    }

    @Test
    void findUserById() {
        webTestClient.get().uri("/users/" + user1.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody(UserDto.class)
                .isEqualTo(userDto1);
    }

    private void createUsers() {
        user1 = new User();
        user1.setUsername("username1");
        user1.setFacebookId(123456L);
        user1.setEmail("email1@email.com");
        user1.setFirstName("Name1");
        user1.setLastName("Surname1");

        user2 = new User();
        user2.setUsername("username2");
        user2.setFacebookId(123123L);
        user2.setEmail("email2@email.com");
        user2.setFirstName("Name2");
        user2.setLastName("Surname2");

        user1 = userService.save(user1).block();
        user2 = userService.save(user2).block();

        userDto1 = userDtoConverter.toDto(user1);
        userDto2 = userDtoConverter.toDto(user2);
    }
}