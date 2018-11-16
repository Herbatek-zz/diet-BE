package com.piotrek.diet.cart;

import com.piotrek.diet.helpers.CartSample;
import com.piotrek.diet.helpers.UserSample;
import com.piotrek.diet.exceptions.NotFoundException;
import com.piotrek.diet.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static com.piotrek.diet.helpers.AssertEqualAllFields.assertCartFields;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartDtoConverter cartDtoConverter;

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
    @DisplayName("Find cart by userId and date, when found, then return")
    void findByUserIdAndDate_whenFound_thenReturn() {
        when(cartRepository.findByUserIdAndDate(user.getId(), cart.getDate())).thenReturn(Mono.just(cart));

        final Cart block = cartService.findByUserIdAndDate(user.getId(), cart.getDate()).block();

        assertCartFields(cart, block);
        verify(cartRepository, times(1)).findByUserIdAndDate(user.getId(), cart.getDate());
        verifyNoMoreInteractions(cartRepository, cartDtoConverter);
    }

    @Test
    @DisplayName("Find by userId and date, when not found, then throw NotFoundException")
    void findByUserIdAndDate_whenNotFound_thenThrowNotFoundException() {
        when(cartRepository.findByUserIdAndDate(user.getId(), cart.getDate())).thenReturn(Mono.empty());

        assertThrows(NotFoundException.class, () -> cartService.findByUserIdAndDate(user.getId(), cart.getDate()).block());

        verify(cartRepository, times(1)).findByUserIdAndDate(user.getId(), cart.getDate());
        verifyNoMoreInteractions(cartRepository, cartDtoConverter);
    }

    @Test
    void save() {
        when(cartRepository.save(cart)).thenReturn(Mono.just(cart));
        when(cartDtoConverter.fromDto(cartDto)).thenReturn(cart);

        final Cart block = cartService.save(cartDto).block();

        assertCartFields(cart, block);
        verify(cartRepository, times(1)).save(cart);
        verify(cartDtoConverter, times(1)).fromDto(cartDto);
        verifyNoMoreInteractions(cartRepository, cartDtoConverter);
    }

    @Test
    void saveDto() {
        when(cartRepository.save(cart)).thenReturn(Mono.just(cart));

        final Cart block = cartService.save(cart).block();

        assertCartFields(cart, block);
        verify(cartRepository, times(1)).save(cart);
        verifyNoMoreInteractions(cartRepository, cartDtoConverter);
    }

    @Test
    void deleteAll() {
        when(cartRepository.deleteAll()).thenReturn(Mono.empty());

        cartService.deleteAll().block();

        verify(cartRepository, times(1)).deleteAll();
        verifyNoMoreInteractions(cartRepository, cartDtoConverter);
    }
}