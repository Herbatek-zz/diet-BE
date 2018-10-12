package com.piotrek.diet.cart;

import com.piotrek.diet.sample.CartSample;
import com.piotrek.diet.sample.UserSample;
import com.piotrek.diet.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartService cartService;

    private Cart cart;
    private CartDto cartDto;
    private User user;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        cart = CartSample.cart1();
        cartDto = CartSample.cartDto1();
        user = UserSample.johnWithId();
    }


    @Test
    @DisplayName("Find cart by id, when found, then return cart in Mono")
    void findById_whenFound_thenReturn() {
        Mockito.when(cartRepository.findById(cart.getId())).thenReturn(Mono.just(cart));

        Cart block = cartService.findById(cart.getId()).block();

        assertAll(
                () -> assertEquals(cart.getId(), block.getId()),
                () -> assertEquals(cart.getMeals(), block.getMeals()),
                () -> assertEquals(cart.getDate(), block.getDate()),
                () -> assertEquals(cart.getUserId(), block.getUserId())
        );

        verify(cartRepository, times(1)).findById(cart.getId());
        verifyNoMoreInteractions(cartRepository);
    }

    @Test
    @DisplayName("Find cart by id, when not found, then return null in mono")
    void findById_whenNotFound_thenReturnMonoEmpty() {
        Mockito.when(cartRepository.findById(cart.getId())).thenReturn(Mono.empty());

        Cart block = cartService.findById(cart.getId()).block();

        assertNull(block);

        verify(cartRepository, times(1)).findById(cart.getId());
        verifyNoMoreInteractions(cartRepository);
    }

    //    public Mono<Cart> findByUserIdAndDate(String userId, LocalDate localDateTime) {
//        return cartRepository.findByUserIdAndDate(userId, localDateTime);
//    }
//
//    public Mono<Cart> findTodayByUserId(String userId) {
//        return findByUserIdAndDate(userId, LocalDate.now());
//    }

    @Test
    void findByUserIdAndDate() {
        Mockito.when(cartRepository.findByUserIdAndDate(user.getId(), cart.getDate())).thenReturn(Mono.just(cart));

        Cart block = cartService.findByUserIdAndDate(user.getId(), cart.getDate()).block();

        assertAll(
                () -> assertEquals(cart.getId(), block.getId()),
                () -> assertEquals(cart.getMeals(), block.getMeals()),
                () -> assertEquals(cart.getDate(), block.getDate()),
                () -> assertEquals(cart.getUserId(), block.getUserId())
        );

        verify(cartRepository, times(1)).findByUserIdAndDate(user.getId(), cart.getDate());
        verifyNoMoreInteractions(cartRepository);
    }

    @Test
    void findTodayByUserId() {
        Mockito.when(cartRepository.findByUserIdAndDate(user.getId(), LocalDate.now())).thenReturn(Mono.just(cart));

        Cart block = cartService.findTodayByUserId(user.getId()).block();

        assertAll(
                () -> assertEquals(cart.getId(), block.getId()),
                () -> assertEquals(cart.getMeals(), block.getMeals()),
                () -> assertEquals(cart.getDate(), block.getDate()),
                () -> assertEquals(cart.getUserId(), block.getUserId())
        );

        verify(cartRepository, times(1)).findByUserIdAndDate(user.getId(), LocalDate.now());
        verifyNoMoreInteractions(cartRepository);
    }

    @Test
    void save() {
        Mockito.when(cartRepository.save(cart)).thenReturn(Mono.just(cart));

        Cart block = cartService.save(cart).block();

        assertAll(
                () -> assertEquals(cart.getId(), block.getId()),
                () -> assertEquals(cart.getMeals(), block.getMeals()),
                () -> assertEquals(cart.getDate(), block.getDate()),
                () -> assertEquals(cart.getUserId(), block.getUserId())
        );

        verify(cartRepository, times(1)).save(cart);
        verifyNoMoreInteractions(cartRepository);
    }

    @Test
    void deleteAll() {
        Mockito.when(cartRepository.deleteAll()).thenReturn(Mono.empty());

        cartService.deleteAll().block();

        verify(cartRepository, times(1)).deleteAll();
        verifyNoMoreInteractions(cartRepository);
    }
}