package com.piotrek.diet.user;

import com.piotrek.diet.helpers.exceptions.NotFoundException;
import com.piotrek.diet.sample.UserSample;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDtoConverter userDtoConverter;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setup() {
        user = UserSample.johnWithId();
        userDto = UserSample.johnWithIdDto();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void findById_whenIdIsValid_thenReturnUser() {
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Mono.just(user));

        var userById = userService.findById(user.getId()).block();

        assertNotNull(userById);
        assertAll(
                () -> assertEquals(user.getFirstName(), userById.getFirstName()),
                () -> assertEquals(user.getLastName(), userById.getLastName()),
                () -> assertEquals(user.getId(), userById.getId()),
                () -> assertEquals(user.getFacebookId(), userById.getFacebookId()),
                () -> assertEquals(user.getEmail(), userById.getEmail()),
                () -> assertEquals(user.getUsername(), userById.getUsername()),
                () -> assertEquals(user.getRole(), userById.getRole()),
                () -> assertEquals(user.getCreatedAt(), userById.getCreatedAt()),
                () -> assertEquals(user.getLastVisit(), userById.getLastVisit())
        );

        verify(userRepository, times(1)).findById(user.getId());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findById_whenIdIsInvalid_thenThrowNotFoundException() {
        var id = "unknown#id";
        Mockito.when(userRepository.findById(id)).thenReturn(Mono.empty());

        assertThrows(NotFoundException.class, () -> userService.findById(id).block());

        verify(userRepository, times(1)).findById(id);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Find userDto by id, when found then return it")
    void findDtoById_whenFound_thenReturn() {
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Mono.just(user));
        Mockito.when(userDtoConverter.toDto(user)).thenReturn(userDto);

        var userById = userService.findDtoById(user.getId()).block();

        assertNotNull(userById);
        assertAll(
                () -> assertEquals(user.getFirstName(), userById.getFirstName()),
                () -> assertEquals(user.getLastName(), userById.getLastName()),
                () -> assertEquals(user.getId(), userById.getId()),
                () -> assertEquals(user.getEmail(), userById.getEmail()),
                () -> assertEquals(user.getUsername(), userById.getUsername())
        );

        verify(userRepository, times(1)).findById(user.getId());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Find userDto by id, when not found then throw NotFoundException")
    void findDtoById_whenNotFound_thenReturn() {
        final var ID = "unknown#id";

        Mockito.when(userRepository.findById(ID)).thenReturn(Mono.empty());
        Mockito.when(userDtoConverter.toDto(user)).thenReturn(userDto);

        assertThrows(NotFoundException.class, () -> userService.findDtoById(ID).block());
        verify(userRepository, times(1)).findById(ID);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findByFacebookId_whenFacebookIdIsValid_thenReturnUser() {
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
                () -> assertEquals(user.getRole(), userByFacebookId.getRole()),
                () -> assertEquals(user.getCreatedAt(), userByFacebookId.getCreatedAt()),
                () -> assertEquals(user.getLastVisit(), userByFacebookId.getLastVisit())
        );

        verify(userRepository, times(1)).findByFacebookId(user.getFacebookId());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findByFacebookId_whenFacebookIdIsInvalid_thenReturnNull() {
        Mockito.when(userRepository.findByFacebookId(user.getFacebookId())).thenReturn(Mono.empty());

        User userByFacebookId = userService.findByFacebookId(user.getFacebookId()).block();

        assertNull(userByFacebookId);

        verify(userRepository, times(1)).findByFacebookId(user.getFacebookId());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findByEmail_whenEmailIsCorrect_thenReturnUser() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Mono.just(user));

        User userByEmail = userService.findByEmail(user.getEmail()).block();

        assertNotNull(userByEmail);
        assertAll(
                () -> assertEquals(user.getFirstName(), userByEmail.getFirstName()),
                () -> assertEquals(user.getLastName(), userByEmail.getLastName()),
                () -> assertEquals(user.getId(), userByEmail.getId()),
                () -> assertEquals(user.getFacebookId(), userByEmail.getFacebookId()),
                () -> assertEquals(user.getEmail(), userByEmail.getEmail()),
                () -> assertEquals(user.getUsername(), userByEmail.getUsername()),
                () -> assertEquals(user.getRole(), userByEmail.getRole()),
                () -> assertEquals(user.getCreatedAt(), userByEmail.getCreatedAt()),
                () -> assertEquals(user.getLastVisit(), userByEmail.getLastVisit())
        );

        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findByEmail_whenEmailIsIncorrect_thenReturnNull() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Mono.empty());

        User userByEmail = userService.findByEmail(user.getEmail()).block();

        assertNull(userByEmail);

        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findAll_whenNoUsers_thenReturnEmptyFlux() {
        Mockito.when(userRepository.findAll()).thenReturn(Flux.empty());

        List<User> users = userService.findAll().collectSortedList().block();

        assertNotNull(users);
        assertEquals(0, users.size());

        verify(userRepository, times(1)).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findAll_whenOneUser_thenReturnFluxWithOneUser() {
        Mockito.when(userRepository.findAll()).thenReturn(Flux.just(user));

        var users = userService.findAll().collectList().block();

        assertNotNull(users);
        assertNotNull(users.get(0));
        assertAll(
                () -> assertEquals(1, users.size()),
                () -> assertEquals(user, users.get(0))
        );

        verify(userRepository, times(1)).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findAll_whenTwoUsers_thenReturnFluxWithTwoUsers() {
        var users = new ArrayList<User>(2);
        users.add(UserSample.johnWithId());
        users.add(UserSample.baileyWithId());

        Mockito.when(userRepository.findAll()).thenReturn(Flux.fromIterable(users));

        var blockUsers = userService.findAll().collectList().block();

        assertNotNull(blockUsers);
        assertNotNull(blockUsers.get(0));
        assertNotNull(blockUsers.get(1));
        assertAll(
                () -> assertEquals(2, blockUsers.size()),
                () -> assertEquals(users.get(0), blockUsers.get(0)),
                () -> assertEquals(users.get(1), blockUsers.get(1))
        );

        verify(userRepository, times(1)).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void save_whenSuccess_thenReturnSavedUser() {
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
                () -> assertEquals(user.getRole(), savedUser.getRole()),
                () -> assertEquals(user.getCreatedAt(), savedUser.getCreatedAt()),
                () -> assertEquals(user.getLastVisit(), savedUser.getLastVisit())

        );

        verify(userRepository, times(1)).save(user);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteById() {
        assertEquals(Mono.empty().block(), userService.deleteById(user.getId()));

        verify(userRepository, times(1)).deleteById(user.getId());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteAll() {
        assertEquals(Mono.empty().block(), userService.deleteAll());

        verify(userRepository, times(1)).deleteAll();
        verifyNoMoreInteractions(userRepository);
    }
}