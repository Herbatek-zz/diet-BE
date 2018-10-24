package com.piotrek.diet.cart;

import com.piotrek.diet.helpers.exceptions.NotFoundException;
import com.piotrek.diet.sample.CartEquals;
import com.piotrek.diet.sample.CartSample;
import com.piotrek.diet.sample.UserSample;
import com.piotrek.diet.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartService cartService;

    private Cart cart;
    private CartDto cartDto;
    private User user;


    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.initMocks(this);
        cart = CartSample.cart1();
        cartDto = CartSample.cartDto1();
        user = UserSample.johnWithId();
    }

    @Test
    @DisplayName("Find cart by id, when found, then return cart in Mono")
    void findById_whenFound_thenReturn() {
        when(cartRepository.findById(cart.getId())).thenReturn(Mono.just(cart));

        final Cart block = cartService.findById(cart.getId()).block();

        assertTrue(CartEquals.cartEquals(cart, block));
        verify(cartRepository, times(1)).findById(cart.getId());
        verifyNoMoreInteractions(cartRepository);
    }

    @Test
    @DisplayName("Find cart by id, when not found, then throw NotFoundException")
    void findById_whenNotFound_thenReturnMonoEmpty() {
        when(cartRepository.findById(cart.getId())).thenReturn(Mono.empty());

        assertThrows(NotFoundException.class, () -> cartService.findById(cart.getId()).block());
        verify(cartRepository, times(1)).findById(cart.getId());
        verifyNoMoreInteractions(cartRepository);
    }

    @Test
    void findByUserIdAndDate() {
        when(cartRepository.findByUserIdAndDate(user.getId(), cart.getDate())).thenReturn(Mono.just(cart));

        final Cart block = cartService.findByUserIdAndDate(user.getId(), cart.getDate()).block();

        assertTrue(CartEquals.cartEquals(cart, block));
        verify(cartRepository, times(1)).findByUserIdAndDate(user.getId(), cart.getDate());
        verifyNoMoreInteractions(cartRepository);
    }

    @Test
    @DisplayName("Find by userId and date, when not found, then throw NotFoundException")
    void findByUserIdAndDate_whenNotFound_thenThrowNotFoundException() {
        when(cartRepository.findByUserIdAndDate(user.getId(), cart.getDate())).thenReturn(Mono.empty());

        assertThrows(NotFoundException.class, () -> cartService.findByUserIdAndDate(user.getId(), cart.getDate()).block());

        verify(cartRepository, times(1)).findByUserIdAndDate(user.getId(), cart.getDate());
        verifyNoMoreInteractions(cartRepository);
    }

    @Test
    void save() {
        when(cartRepository.save(cart)).thenReturn(Mono.just(cart));

        final Cart block = cartService.save(cart).block();

        assertTrue(CartEquals.cartEquals(cart, block));
        verify(cartRepository, times(1)).save(cart);
        verifyNoMoreInteractions(cartRepository);
    }

    @Test
    void deleteAll() {
        when(cartRepository.deleteAll()).thenReturn(Mono.empty());

        cartService.deleteAll().block();

        verify(cartRepository, times(1)).deleteAll();
        verifyNoMoreInteractions(cartRepository);
    }
}