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

    public Mono<Cart> findById(String id) {
        return cartRepository.findById(id)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Not found cart [id = " + id + "]"))));
    }

    public Mono<Cart> findByUserIdAndDate(String userId, LocalDate localDate) {
        return cartRepository.findByUserIdAndDate(userId, localDate)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Not found cart for user [id = " + userId +
                        " and date: " + localDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "]"))));
    }

    public Mono<Cart> save(Cart cart) {
        return cartRepository.save(cart);
    }

    public Mono<Void> deleteAll() {
        return cartRepository.deleteAll();
    }
}
