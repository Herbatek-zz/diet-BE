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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

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
    void findById_validId_shouldReturnUser() {
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Mono.just(user));

        User userById = userService.findById(user.getId()).block();

        assertNotNull(userById);
        assertAll(
                () -> assertEquals(user.getFirstName(), userById.getFirstName()),
                () -> assertEquals(user.getLastName(), userById.getLastName()),
                () -> assertEquals(user.getId(), userById.getId()),
                () -> assertEquals(user.getFacebookId(), userById.getFacebookId()),
                () -> assertEquals(user.getEmail(), userById.getEmail()),
                () -> assertEquals(user.getUsername(), userById.getUsername()),
                () -> assertEquals(user.getRole(), userById.getRole())
        );

        verify(userRepository, times(1)).findById(user.getId());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findById_invalidId_shouldThrowNotFoundException() {

    }

    @Test
    void findAll() {
        Mockito.when(userRepository.findAll()).thenReturn(Flux.just(user));

        List<User> users = userService.findAll().collectSortedList().block();

        assertNotNull(users);
        assertAll(
                () -> assertEquals(1, users.size()),
                () -> assertEquals(user, users.get(0))
        );

        verify(userRepository, times(1)).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findByFacebookId() {
        Mockito.when(userRepository.findByFacebookId(user.getFacebookId())).thenReturn(Mono.just(user));

        User userByFacebookId = userService.findByFacebookId(user.getFacebookId()).block();

        assertNotNull(userByFacebookId);
        assertAll(
                () -> assertEquals(user.getFirstName(), userByFacebookId.getFirstName()),
                () -> assertEquals(user.getLastName(), userByFacebookId.getLastName()),
                () -> assertEquals(user.getId(), userByFacebookId.getId()),
                () -> assertEquals(user.getFacebookId(), userByFacebookId.getFacebookId()),
                () -> assertEquals(user.getEmail(), userByFacebookId.getEmail()),
                () -> assertEquals(user.getUsername(), userByFacebookId.getUsername()),
                () -> assertEquals(user.getRole(), userByFacebookId.getRole())
        );

        verify(userRepository, times(1)).findByFacebookId(user.getFacebookId());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void save() {
        Mockito.when(userRepository.save(user)).thenReturn(Mono.just(user));

        User savedUser = userService.save(user).block();

        assertNotNull(savedUser);
        assertAll(
                () -> assertEquals(user.getFirstName(), savedUser.getFirstName()),
                () -> assertEquals(user.getLastName(), savedUser.getLastName()),
                () -> assertEquals(user.getId(), savedUser.getId()),
                () -> assertEquals(user.getFacebookId(), savedUser.getFacebookId()),
                () -> assertEquals(user.getEmail(), savedUser.getEmail()),
                () -> assertEquals(user.getUsername(), savedUser.getUsername()),
                () -> assertEquals(user.getRole(), savedUser.getRole())
        );

        verify(userRepository, times(1)).save(user);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void createUserTest() {
        String[] names = {user.getFirstName(), user.getLastName()};

        User createdUser = userService.createUser(this.user.getFacebookId(), names);

        assertAll(
                () -> assertEquals(user.getFirstName(), createdUser.getFirstName()),
                () -> assertEquals(user.getLastName(), createdUser.getLastName()),
                () -> assertEquals(user.getFacebookId(), createdUser.getFacebookId())
        );
        verifyNoMoreInteractions(userRepository);
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