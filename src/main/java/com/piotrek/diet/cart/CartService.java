package com.piotrek.diet.cart;

import com.piotrek.diet.meal.Meal;
import com.piotrek.diet.user.UserValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    public Mono<Cart> findById(String id) {
        return cartRepository.findById(id);
    }

    public Mono<Cart> findByUserIdAndDate(String userId, LocalDate localDateTime) {
        return cartRepository.findByUserIdAndDate(userId, localDateTime);
    }

    public Mono<Cart> save(Cart cart) {
        return cartRepository.save(cart);
    }

    public Mono<Void> deleteAll() {
        return cartRepository.deleteAll();
    }
}
