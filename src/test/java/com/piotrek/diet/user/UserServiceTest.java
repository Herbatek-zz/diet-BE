package com.piotrek.diet.user;

import com.piotrek.diet.helpers.exceptions.NotFoundException;
import com.piotrek.diet.sample.UserSample;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

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
    @DisplayName("Find user by id, when found, then return")
    void findById_whenFound_thenReturn() {
        when(userRepository.findById(user.getId())).thenReturn(Mono.just(user));

        final var block = userService.findById(user.getId()).block();

        assertNotNull(block);
        assertAll(
                () -> assertEquals(user.getFirstName(), block.getFirstName()),
                () -> assertEquals(user.getLastName(), block.getLastName()),
                () -> assertEquals(user.getId(), block.getId()),
                () -> assertEquals(user.getFacebookId(), block.getFacebookId()),
                () -> assertEquals(user.getEmail(), block.getEmail()),
                () -> assertEquals(user.getUsername(), block.getUsername()),
                () -> assertEquals(user.getRole(), block.getRole()),
                () -> assertEquals(user.getCreatedAt(), block.getCreatedAt()),
                () -> assertEquals(user.getLastVisit(), block.getLastVisit())
        );
        verify(userRepository, times(1)).findById(user.getId());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Find by id, when not found, then throw NotFoundException")
    void findById_whenNotFound_thenThrowNotFoundException() {
        final var ID = "invalidId";
        when(userRepository.findById(ID)).thenReturn(Mono.empty());

        assertThrows(NotFoundException.class, () -> userService.findById(ID).block());
        verify(userRepository, times(1)).findById(ID);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Find dto by id, when found, then return")
    void findDtoById_whenFound_thenReturn() {
        when(userRepository.findById(user.getId())).thenReturn(Mono.just(user));
        when(userDtoConverter.toDto(user)).thenReturn(userDto);

        final var block = userService.findDtoById(user.getId()).block();

        assertNotNull(block);
        assertAll(
                () -> assertEquals(user.getFirstName(), block.getFirstName()),
                () -> assertEquals(user.getLastName(), block.getLastName()),
                () -> assertEquals(user.getId(), block.getId()),
                () -> assertEquals(user.getEmail(), block.getEmail()),
                () -> assertEquals(user.getUsername(), block.getUsername())
        );
        verify(userRepository, times(1)).findById(user.getId());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Find userDto by id, when not found then throw NotFoundException")
    void findDtoById_whenNotFound_thenReturn() {
        final var ID = "unknown#id";

        when(userRepository.findById(ID)).thenReturn(Mono.empty());
        when(userDtoConverter.toDto(user)).thenReturn(userDto);

        assertThrows(NotFoundException.class, () -> userService.findDtoById(ID).block());
        verify(userRepository, times(1)).findById(ID);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Find by facebook id, when found, then return")
    void findByFacebookId_whenFound_thenReturn() {
        when(userRepository.findByFacebookId(user.getFacebookId())).thenReturn(Mono.just(user));

        final var block = userService.findByFacebookId(user.getFacebookId()).block();

        assertNotNull(block);
        assertAll(
                () -> assertEquals(user.getFirstName(), block.getFirstName()),
                () -> assertEquals(user.getLastName(), block.getLastName()),
                () -> assertEquals(user.getId(), block.getId()),
                () -> assertEquals(user.getFacebookId(), block.getFacebookId()),
                () -> assertEquals(user.getEmail(), block.getEmail()),
                () -> assertEquals(user.getUsername(), block.getUsername()),
                () -> assertEquals(user.getRole(), block.getRole()),
                () -> assertEquals(user.getCreatedAt(), block.getCreatedAt()),
                () -> assertEquals(user.getLastVisit(), block.getLastVisit())
        );
        verify(userRepository, times(1)).findByFacebookId(user.getFacebookId());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Find by facebook id, when not found, then return null")
    void findByFacebookId_whenNotFound_thenReturnNull() {
        when(userRepository.findByFacebookId(user.getFacebookId())).thenReturn(Mono.empty());

        final var block = userService.findByFacebookId(user.getFacebookId()).block();

        assertNull(block);
        verify(userRepository, times(1)).findByFacebookId(user.getFacebookId());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Find by email, when found, then return")
    void findByEmail_whenFound_thenReturn() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Mono.just(user));

        final var block = userService.findByEmail(user.getEmail()).block();

        assertNotNull(block);
        assertAll(
                () -> assertEquals(user.getFirstName(), block.getFirstName()),
                () -> assertEquals(user.getLastName(), block.getLastName()),
                () -> assertEquals(user.getId(), block.getId()),
                () -> assertEquals(user.getFacebookId(), block.getFacebookId()),
                () -> assertEquals(user.getEmail(), block.getEmail()),
                () -> assertEquals(user.getUsername(), block.getUsername()),
                () -> assertEquals(user.getRole(), block.getRole()),
                () -> assertEquals(user.getCreatedAt(), block.getCreatedAt()),
                () -> assertEquals(user.getLastVisit(), block.getLastVisit())
        );
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Find by email, then not found, then return null")
    void findByEmail_whenNotFound_thenReturnNull() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Mono.empty());

        final var block = userService.findByEmail(user.getEmail()).block();

        assertNull(block);
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findAll_whenNoUsers_thenReturnEmptyFlux() {
        when(userRepository.findAll()).thenReturn(Flux.empty());

        final var users = userService.findAll().collectSortedList().block();

        assertNotNull(users);
        assertEquals(0, users.size());
        verify(userRepository, times(1)).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findAll_whenOneUser_thenReturnFluxWithOneUser() {
        when(userRepository.findAll()).thenReturn(Flux.just(user));

        final var users = userService.findAll().collectList().block();

        assertNotNull(users);
        assertAll(
                () -> assertNotNull(users.get(0)),
                () -> assertEquals(1, users.size()),
                () -> assertEquals(user, users.get(0))
        );
        verify(userRepository, times(1)).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findAll_whenTwoUsers_thenReturnFluxWithTwoUsers() {
        final var expectedList = new ArrayList<User>(2);
        expectedList.add(UserSample.johnWithId());
        expectedList.add(UserSample.baileyWithId());

        when(userRepository.findAll()).thenReturn(Flux.fromIterable(expectedList));

        final var users = userService.findAll().collectList().block();

        assertNotNull(users);
        assertAll(
                () -> assertNotNull(users.get(0)),
                () -> assertNotNull(users.get(1)),
                () -> assertEquals(2, users.size()),
                () -> assertEquals(expectedList.get(0), users.get(0)),
                () -> assertEquals(expectedList.get(1), users.get(1))
        );
        verify(userRepository, times(1)).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void save_whenSuccess_thenReturnSavedUser() {
        when(userRepository.save(user)).thenReturn(Mono.just(user));

        final var savedUser = userService.save(user).block();

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