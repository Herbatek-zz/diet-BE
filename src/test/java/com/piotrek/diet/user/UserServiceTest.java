package com.piotrek.diet.user;

import com.piotrek.diet.helpers.UserSample;
import com.piotrek.diet.helpers.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.UUID;

import static com.piotrek.diet.helpers.AssertEqualAllFields.assertUserFields;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserValidation userValidation;

    @Mock
    private UserDtoConverter userDtoConverter;

    @Mock
    private CaloriesCalculator caloriesCalculator;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void beforeEach() {
        user = UserSample.johnWithId();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Find user by id, when found, then return")
    void findById_whenFound_thenReturn() {
        when(userRepository.findById(user.getId())).thenReturn(Mono.just(user));

        final var block = userService.findById(user.getId()).block();

        assertUserFields(user, block);
        verify(userRepository, times(1)).findById(user.getId());
        verifyNoMoreInteractions(userRepository, userDtoConverter, userValidation);
    }

    @Test
    @DisplayName("Find by id, when not found, then throw NotFoundException")
    void findById_whenNotFound_thenThrowNotFoundException() {
        final var WRONG_ID = UUID.randomUUID().toString();

        when(userRepository.findById(WRONG_ID)).thenReturn(Mono.empty());

        assertThrows(NotFoundException.class, () -> userService.findById(WRONG_ID).block());
        verify(userRepository, times(1)).findById(WRONG_ID);
        verifyNoMoreInteractions(userRepository, userDtoConverter, userValidation);
    }

    @Test
    @DisplayName("Find by facebook id, when found, then return")
    void findByFacebookId_whenFound_thenReturn() {
        when(userRepository.findByFacebookId(user.getFacebookId())).thenReturn(Mono.just(user));

        final var block = userService.findByFacebookId(user.getFacebookId()).block();

        assertUserFields(user, block);
        verify(userRepository, times(1)).findByFacebookId(user.getFacebookId());
        verifyNoMoreInteractions(userRepository, userDtoConverter, userValidation);
    }

    @Test
    @DisplayName("Find by facebook id, when not found, then return null")
    void findByFacebookId_whenNotFound_thenReturnNull() {
        when(userRepository.findByFacebookId(user.getFacebookId())).thenReturn(Mono.empty());

        final var block = userService.findByFacebookId(user.getFacebookId()).block();

        assertNull(block);
        verify(userRepository, times(1)).findByFacebookId(user.getFacebookId());
        verifyNoMoreInteractions(userRepository, userDtoConverter, userValidation);
    }

    @Test
    @DisplayName("Find by email, when found, then return")
    void findByEmail_whenFound_thenReturn() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Mono.just(user));

        final var block = userService.findByEmail(user.getEmail()).block();

        assertUserFields(user, block);
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verifyNoMoreInteractions(userRepository, userDtoConverter, userValidation);
    }

    @Test
    @DisplayName("Find by email, then not found, then return null")
    void findByEmail_whenNotFound_thenReturnNull() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Mono.empty());

        final var block = userService.findByEmail(user.getEmail()).block();

        assertNull(block);
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verifyNoMoreInteractions(userRepository, userDtoConverter, userValidation);
    }

    @Test
    void findAll_whenNoUsers_thenReturnEmptyFlux() {
        when(userRepository.findAll()).thenReturn(Flux.empty());

        final var users = userService.findAll().collectSortedList().block();

        assertNotNull(users);
        assertEquals(0, users.size());
        verify(userRepository, times(1)).findAll();
        verifyNoMoreInteractions(userRepository, userDtoConverter, userValidation);
    }

    @Test
    void findAll_whenOneUser_thenReturnFluxWithOneUser() {
        when(userRepository.findAll()).thenReturn(Flux.just(user));

        final var userList = userService.findAll().collectList().block();

        assertNotNull(userList);
        assertAll(
                () -> assertNotNull(userList.get(0)),
                () -> assertEquals(1, userList.size()),
                () -> assertUserFields(user, userList.get(0))
        );
        verify(userRepository, times(1)).findAll();
        verifyNoMoreInteractions(userRepository, userDtoConverter, userValidation);
    }

    @Test
    void findAll_whenTwoUsers_thenReturnFluxWithTwoUsers() {
        final var expectedList = new ArrayList<User>(2);
        expectedList.add(UserSample.johnWithId());
        expectedList.add(UserSample.baileyWithId());

        when(userRepository.findAll()).thenReturn(Flux.fromIterable(expectedList));

        final var actualUserList = userService.findAll().collectList().block();

        assertNotNull(actualUserList);
        assertAll(
                () -> assertEquals(2, actualUserList.size()),
                () -> assertNotNull(actualUserList.get(0)),
                () -> assertNotNull(actualUserList.get(1)),
                () -> assertUserFields(expectedList.get(0), actualUserList.get(0)),
                () -> assertUserFields(expectedList.get(1), actualUserList.get(1))
        );
        verify(userRepository, times(1)).findAll();
        verifyNoMoreInteractions(userRepository, userDtoConverter, userValidation);
    }

    @Test
    void save_whenSuccess_thenReturnSavedUser() {
        when(userRepository.save(user)).thenReturn(Mono.just(user));

        final var savedUser = userService.save(user).block();

        assertUserFields(user, savedUser);
        verify(userRepository, times(1)).save(user);
        verifyNoMoreInteractions(userRepository, userDtoConverter, userValidation);
    }

    @Test
    void update_whenUpdate_thenUserHasUpdatedFields() {
        final var userDto = UserSample.johnWithIdDto();
        userDto.setUsername("Mr Kawek");
        userDto.setFirstName("Janusz");
        userDto.setLastName("Cisowki");
        userDto.setEmail("janusz123@mail.com");
        userDto.setPicture_url("www.images.com/so-good-image-for-tests.jpg");
        userDto.setAge(23);
        userDto.setHeight(175);
        userDto.setWeight(80);

        final var user = UserSample.johnWithId();
        user.setUsername("Mr Kawek");
        user.setFirstName("Janusz");
        user.setLastName("Cisowki");
        user.setEmail("janusz123@mail.com");
        user.setPictureUrl("www.images.com/so-good-image-for-tests.jpg");
        user.setAge(23);
        user.setHeight(175);
        user.setWeight(80);


        when(userRepository.findById(user.getId())).thenReturn(Mono.just(user));
        when(userRepository.save(user)).thenReturn(Mono.just(user));
        when(userDtoConverter.toDto(user)).thenReturn(userDto);
        when(caloriesCalculator.calculateCaloriesPerDay(userDto)).thenCallRealMethod();

        UserDto actualUser = userService.update(user.getId(), userDto).block();

        assertUserFields(userDto, actualUser);
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verify(userRepository, times(1)).findById(user.getId());
        verify(userRepository, times(1)).save(user);
        verify(userDtoConverter, times(1)).toDto(user);
        verifyNoMoreInteractions(userRepository, userDtoConverter, userValidation);
    }
    @Test
    void deleteById() {
        assertEquals(Mono.empty().block(), userService.deleteById(user.getId()));
        verify(userRepository, times(1)).deleteById(user.getId());
        verifyNoMoreInteractions(userRepository, userDtoConverter, userValidation);
    }

    @Test
    void deleteAll() {
        assertEquals(Mono.empty().block(), userService.deleteAll());
        verify(userRepository, times(1)).deleteAll();
        verifyNoMoreInteractions(userRepository, userDtoConverter, userValidation);
    }
}