package com.piotrek.diet.service;

import com.piotrek.diet.model.User;
import com.piotrek.diet.model.enumeration.Role;
import com.piotrek.diet.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setup() {
        createUser();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void findById() {
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Mono.just(user));

        User userById = userService.findById(user.getId()).block();

        assertAll(
                () -> assertEquals(user.getFirstName(), userById.getFirstName()),
                () -> assertEquals(user.getLastName(), userById.getLastName()),
                () -> assertEquals(user.getId(), userById.getId()),
                () -> assertEquals(user.getFacebookId(), userById.getFacebookId()),
                () -> assertEquals(user.getEmail(), userById.getEmail()),
                () -> assertEquals(user.getUsername(), userById.getUsername()),
                () -> assertEquals(user.getRole(), userById.getRole())
        );
    }

    @Test
    void findAll() {
    }

    @Test
    void findByFacebookId() {
    }

    @Test
    void save() {
    }

    @Test
    void createUserTest() {
    }

    private void createUser() {
        user = new User();
        user.setFacebookId(12351L);
        user.setId("idSuchLong123");
        user.setFirstName("John");
        user.setLastName("TheGamer");
        user.setRole(Role.ROLE_USER.name());
        user.setEmail("JTG@email.com");
        user.setUsername("jtGAMER");
    }
}