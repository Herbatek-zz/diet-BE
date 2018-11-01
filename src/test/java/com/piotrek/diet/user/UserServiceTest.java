package com.piotrek.diet.user;

import com.piotrek.diet.helpers.UserSample;
import com.piotrek.diet.helpers.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserValidation userValidation;

    @Mock
    private UserDtoConverter userDtoConverter;

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

        this.assertEqualAllUserFields(user, block);
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

        this.assertEqualAllUserFields(user, block);
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

        this.assertEqualAllUserFields(user, block);
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
                () -> this.assertEqualAllUserFields(user, userList.get(0))
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
                () -> this.assertEqualAllUserFields(expectedList.get(0), actualUserList.get(0)),
                () -> this.assertEqualAllUserFields(expectedList.get(1), actualUserList.get(1))
        );
        verify(userRepository, times(1)).findAll();
        verifyNoMoreInteractions(userRepository, userDtoConverter, userValidation);
    }

    @Test
    void save_whenSuccess_thenReturnSavedUser() {
        when(userRepository.save(user)).thenReturn(Mono.just(user));

        final var savedUser = userService.save(user).block();

        this.assertEqualAllUserFields(user, savedUser);
        verify(userRepository, times(1)).save(user);
        verifyNoMoreInteractions(userRepository, userDtoConverter, userValidation);
    }

    @Test
    void update_whenUpdate_thenUserHasUpdatedFields() {
        final var updatedUserDto = UserSample.johnWithIdDto();
        updatedUserDto.setUsername("Mr Kawek");
        updatedUserDto.setFirstName("Janusz");
        updatedUserDto.setLastName("Cisowki");
        updatedUserDto.setEmail("janusz123@mail.com");
        updatedUserDto.setPicture_url("www.images.com/so-good-image-for-tests.jpg");
        updatedUserDto.setAge(23);
        updatedUserDto.setHeight(175);
        updatedUserDto.setWeight(80);

        final var updatedUser = UserSample.johnWithId();
        updatedUser.setUsername("Mr Kawek");
        updatedUser.setFirstName("Janusz");
        updatedUser.setLastName("Cisowki");
        updatedUser.setEmail("janusz123@mail.com");
        updatedUser.setPictureUrl("www.images.com/so-good-image-for-tests.jpg");
        updatedUser.setAge(23);
        updatedUser.setHeight(175);
        updatedUser.setWeight(80);

        when(userRepository.findById(user.getId())).thenReturn(Mono.just(user));
        when(userRepository.save(updatedUser)).thenReturn(Mono.just(updatedUser));
        when(userDtoConverter.toDto(updatedUser)).thenReturn(updatedUserDto);

        UserDto actualUser = userService.update(user.getId(), updatedUserDto).block();

        this.assertEqualAllUserDtoFields(updatedUserDto, actualUser);
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verify(userRepository, times(1)).findById(user.getId());
        verify(userRepository, times(1)).save(updatedUser);
        verify(userDtoConverter, times(1)).toDto(updatedUser);
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

    private void assertEqualAllUserFields(User expected, User actual) {
        assertNotNull(actual);
        assertAll(
                () -> assertEquals(expected.getId(), actual.getId()),
                () -> assertEquals(expected.getFacebookId(), actual.getFacebookId()),
                () -> assertEquals(expected.getUsername(), actual.getUsername()),
                () -> assertEquals(expected.getEmail(), actual.getEmail()),
                () -> assertEquals(expected.getFirstName(), actual.getFirstName()),
                () -> assertEquals(expected.getLastName(), actual.getLastName()),
                () -> assertEquals(expected.getPictureUrl(), actual.getPictureUrl()),
                () -> assertEquals(expected.getAge(), actual.getAge()),
                () -> assertEquals(expected.getHeight(), actual.getHeight()),
                () -> assertEquals(expected.getWeight(), actual.getWeight()),
                () -> assertEquals(expected.getCreatedAt(), actual.getCreatedAt()),
                () -> assertEquals(expected.getLastVisit(), actual.getLastVisit()),
                () -> assertEquals(expected.getRole(), actual.getRole()),
                () -> assertEquals(expected.getFavouriteMeals().size(), actual.getFavouriteMeals().size())
        );
    }

    private void assertEqualAllUserDtoFields(UserDto expected, UserDto actual) {
        assertNotNull(actual);
        assertAll(
                () -> assertEquals(expected.getId(), actual.getId()),
                () -> assertEquals(expected.getUsername(), actual.getUsername()),
                () -> assertEquals(expected.getEmail(), actual.getEmail()),
                () -> assertEquals(expected.getFirstName(), actual.getFirstName()),
                () -> assertEquals(expected.getLastName(), actual.getLastName()),
                () -> assertEquals(expected.getAge(), actual.getAge()),
                () -> assertEquals(expected.getHeight(), actual.getHeight()),
                () -> assertEquals(expected.getWeight(), actual.getWeight())
        );
    }
}