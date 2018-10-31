package com.piotrek.diet.cart;

import com.piotrek.diet.helpers.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartDtoConverter cartDtoConverter;

    public Mono<CartDto> findByUserIdAndDate(String userId, LocalDate localDate) {
        final var EXCEPTION_MESSAGE = "Not found cart for user [id = " + userId + " and date: " + localDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "]";
        return cartRepository.findByUserIdAndDate(userId, localDate)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException(EXCEPTION_MESSAGE))))
                .map(cartDtoConverter::toDto);
    }

    public Mono<CartDto> save(Cart cart) {
        return cartRepository.save(cart).map(cartDtoConverter::toDto);
    }

    public Mono<CartDto> save(CartDto cartDto) {
        return save(cartDtoConverter.fromDto(cartDto));
    }

    public Mono<Void> deleteAll() {
        return cartRepository.deleteAll();
    }

}
