package com.piotrek.diet.cart;

import com.piotrek.diet.exceptions.NotFoundException;
import com.piotrek.diet.meal.Meal;
import com.piotrek.diet.meal.MealService;
import com.piotrek.diet.product.Product;
import com.piotrek.diet.product.ProductService;
import com.piotrek.diet.user.User;
import com.piotrek.diet.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CartFacade {

    private final CartService cartService;
    private final UserService userService;
    private final MealService mealService;
    private final ProductService productService;
    private final CartDtoConverter cartDtoConverter;

    @PreAuthorize("#userId.equals(principal)")
    public Mono<CartDto> findDtoCartByUserAndDate(String userId, LocalDate date) {
        return cartService.findByUserIdAndDate(userId, date).map(cartDtoConverter::toDto);
    }

    @PreAuthorize("#userId.equals(principal)")
    public Mono<CartDto> addMealToCart(String userId, String mealId, LocalDate date, int amount) {
        Cart cart;
        try {
            cart = cartService.findByUserIdAndDate(userId, date).block();
        } catch (NotFoundException e) {
            User user = userService.findById(userId).block();
            cart = new Cart(userId, date, user.getCaloriesPerDay(), user.getCarbohydratePerDay(),
                    user.getProteinPerDay(), user.getFatPerDay());
        }
        Meal meal = mealService.findById(mealId).block();
        if (cart.getMeals().contains(meal)) {
            int indexOfDuplicated = cart.getMeals().indexOf(meal);
            Meal duplicated = cart.getMeals().remove(indexOfDuplicated);
            amount += duplicated.getAmount();
        }

        double divider = (double) 100 / amount;
        double amountDivider = (double) amount / meal.getAmount();
        Objects.requireNonNull(meal).getProducts()
                .forEach(productDto -> {
                    productDto.setAmount((int) (productDto.getAmount() * amountDivider));
                    productDto.setProtein(productDto.getProtein() / divider);
                    productDto.setCarbohydrate(productDto.getCarbohydrate() / divider);
                    productDto.setFat(productDto.getFat() / divider);
                    productDto.setFibre(productDto.getFibre() / divider);
                    productDto.setProteinAndFatEquivalent(productDto.getProteinAndFatEquivalent() / divider);
                    productDto.setCarbohydrateExchange(productDto.getCarbohydrateExchange() / divider);
                    productDto.setKcal(productDto.getKcal() / divider);
                });
        mealService.calculateMealInformation(meal);
        meal.setAmount(amount);
        cart.getMeals().add(meal);
        return cartService.save(cart).map(cartDtoConverter::toDto);
    }

    @PreAuthorize("#userId.equals(principal)")
    public Mono<CartDto> deleteMealFromCart(String userId, String mealId, LocalDate date) {
        return cartService.findByUserIdAndDate(userId, date)
                .flatMap(cart -> {
                    Meal meal = new Meal(mealId);
                    if (cart.getMeals().contains(meal)) {
                        cart.getMeals().remove(meal);
                        return cartService.save(cart).map(cartDtoConverter::toDto);
                    } else
                        return Mono.just(cart).map(cartDtoConverter::toDto);
                });
    }

    @PreAuthorize("#userId.equals(principal)")
    public Mono<CartDto> addProductToCart(String userId, String productId, LocalDate date, int amount) {
        Cart cart;
        try {
            cart = cartService.findByUserIdAndDate(userId, date).block();
        } catch (NotFoundException e) {
            User user = userService.findById(userId).block();
            cart = new Cart(userId, date, user.getCaloriesPerDay(), user.getCarbohydratePerDay(),
                    user.getProteinPerDay(), user.getFatPerDay());
        }
        Product product = productService.findById(productId).block();
        product.setAmount(amount);
        if (cart.getProducts().contains(product)) {
            int indexOfDuplicated = cart.getProducts().indexOf(product);
            var duplicated = cart.getProducts().remove(indexOfDuplicated);
            product.setAmount(product.getAmount() + duplicated.getAmount());
        }
        product = productService.calculateProductInfoByAmount(product);
        cart.getProducts().add(product);
        return cartService.save(cart).map(cartDtoConverter::toDto);
    }

    @PreAuthorize("#userId.equals(principal)")
    public Mono<CartDto> deleteProductFromCart(String userId, String productId, LocalDate date) {
        Cart cart = cartService.findByUserIdAndDate(userId, date).block();
        Product product = productService.findById(productId).block();
        if (cart.getProducts().contains(product)) {
            cart.getProducts().remove(product);
            cart = cartService.save(cart).block();
        }
        return Mono.just(cart).map(cartDtoConverter::toDto);
    }
}
