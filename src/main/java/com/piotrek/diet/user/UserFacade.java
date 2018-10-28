package com.piotrek.diet.user;

import com.piotrek.diet.cart.Cart;
import com.piotrek.diet.cart.CartDto;
import com.piotrek.diet.cart.CartDtoConverter;
import com.piotrek.diet.cart.CartService;
import com.piotrek.diet.helpers.Page;
import com.piotrek.diet.helpers.exceptions.NotFoundException;
import com.piotrek.diet.meal.Meal;
import com.piotrek.diet.meal.MealDto;
import com.piotrek.diet.meal.MealDtoConverter;
import com.piotrek.diet.meal.MealService;
import com.piotrek.diet.product.Product;
import com.piotrek.diet.product.ProductDto;
import com.piotrek.diet.product.ProductDtoConverter;
import com.piotrek.diet.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserFacade {

    private final CartService cartService;
    private final CartDtoConverter cartDtoConverter;
    private final UserService userService;
    private final UserValidation userValidation;
    private final ProductDtoConverter productDtoConverter;
    private final ProductService productService;
    private final MealService mealService;
    private final MealDtoConverter mealDtoConverter;

    Mono<CartDto> findDtoCart(String userId, LocalDate date) {
        return findCart(userId, date)
                .map(cartDtoConverter::toDto);
    }

    Mono<Cart> findCart(String userId, LocalDate date) {
        return cartService.findByUserIdAndDate(userId, date);
    }

    Mono<ProductDto> createProduct(String userId, ProductDto productDto) {
        userValidation.validateUserWithPrincipal(userId);
        var user = userService.findById(userId).block();
        var product = productDtoConverter.fromDto(productDto);
        product.setUserId(user.getId());

        return productService.save(product).map(productDtoConverter::toDto);
    }

    Mono<Page<ProductDto>> findAllProductsByUserId(String userId, Pageable pageable) {
        return userService.findById(userId)
                .then(productService
                        .findAllByUserId(userId)
                        .collectList()
                        .map(list -> new Page<>(
                                list
                                        .stream()
                                        .skip(pageable.getPageNumber() * pageable.getPageSize())
                                        .limit(pageable.getPageSize())
                                        .map(productDtoConverter::toDto)
                                        .collect(Collectors.toList()),
                                pageable.getPageNumber(), pageable.getPageSize(), list.size())));
    }

    Mono<MealDto> createMeal(String userId, MealDto mealDto) {
        userValidation.validateUserWithPrincipal(userId);
        userService.findById(userId).block();

        mealDto.setUserId(userId);

        return mealService
                .save(mealDto)
                .map(mealDtoConverter::toDto);
    }

    Mono<Page<MealDto>> findAllMealsByUser(String userId, Pageable pageable) {
        return userService.findById(userId).then(mealService
                .findAllByUserId(userId)
                .collectList()
                .map(list -> new Page<>(
                        list
                                .stream()
                                .skip(pageable.getPageNumber() * pageable.getPageSize())
                                .limit(pageable.getPageSize())
                                .map(mealDtoConverter::toDto)
                                .collect(Collectors.toList()),
                        pageable.getPageNumber(), pageable.getPageSize(), list.size())));
    }


    Mono<Page<MealDto>> findFavouriteMeals(String userId, Pageable pageable) {
        return userService.findById(userId)
                .map(User::getFavouriteMeals)
                .flatMap(meals -> {
                    var size = meals.size();
                    var collection = meals
                            .stream()
                            .skip(pageable.getPageNumber() * pageable.getPageSize())
                            .limit(pageable.getPageSize())
                            .map(mealDtoConverter::toDto)
                            .collect(Collectors.toList());
                    return Mono.just(new Page<>(collection, pageable.getPageNumber(), pageable.getPageSize(), size));
                });
    }

    Mono<Void> addToFavourite(String userId, String mealId) {
        userValidation.validateUserWithPrincipal(userId);
        return userService.findById(userId)
                .flatMap(user -> mealService.findById(mealId)
                        .flatMap(meal -> {
                            user.getFavouriteMeals().add(meal);
                            return userService.save(user);
                        }))
                .then();
    }

    Mono<Void> deleteFromFavourite(String userId, String mealId) {
        userValidation.validateUserWithPrincipal(userId);
        return userService.findById(userId)
                .flatMap(user -> {
                    user.getFavouriteMeals().remove(new Meal(mealId));
                    return userService.save(user);
                })
                .then();
    }

    Mono<MealDto> findMealDtoById(String id) {
        return mealService.findById(id)
                .map(mealDtoConverter::toDto);
    }

    Mono<Boolean> isFavourite(String userId, String mealId) {
        userValidation.validateUserWithPrincipal(userId);
        return userService.findById(userId)
                .map(user -> user.getFavouriteMeals().contains(new Meal(mealId)));
    }

    Mono<CartDto> addMealToCart(String userId, String mealId, LocalDate date, int amount) {
        Cart cart = cartService.findByUserIdAndDate(userId, date)
                .onErrorReturn(new Cart(userId, date)).block();
        userValidation.validateUserWithPrincipal(cart.getUserId());
        Meal meal = mealService.findById(mealId).block();
        if (cart.getMeals().contains(meal)) {
            int indexOfDuplicated = cart.getMeals().indexOf(meal);
            Meal duplicated = cart.getMeals().remove(indexOfDuplicated);
            amount += duplicated.getAmount();
        }

        double divider = (double) meal.getAmount() / amount;
        meal.getProducts()
                .forEach(product -> {
                    product.setAmount((int) (product.getAmount() / divider));
                    product.setProtein(product.getProtein() / divider);
                    product.setCarbohydrate(product.getCarbohydrate() / divider);
                    product.setFat(product.getFat() / divider);
                    product.setFibre(product.getFibre() / divider);
                    product.setProteinAndFatEquivalent(product.getProteinAndFatEquivalent() / divider);
                    product.setCarbohydrateExchange(product.getCarbohydrateExchange() / divider);
                    product.setKcal(product.getKcal() / divider);
                });
        mealService.calculateMealInformation(meal);
        meal.setAmount(amount);
        cart.getMeals().add(meal);
        return cartService.save(cart).map(cartDtoConverter::toDto);
    }

    Mono<Void> deleteMealFromCart(String userId, String mealId, LocalDate date) {
        Cart cart = cartService.findByUserIdAndDate(userId, date)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Not found cart for user [id = " + userId +
                        " and date: " + date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "]")))).block();
        userValidation.validateUserWithPrincipal(cart.getUserId());
        Meal meal = mealService.findById(mealId).block();
        if (cart.getMeals().contains(meal)) {
            cart.getMeals().remove(meal);
            cartService.save(cart).block();
        }
        return Mono.empty();
    }

    Mono<CartDto> addProductToCart(String userId, String productId, LocalDate date, int amount) {
        Cart cart = cartService.findByUserIdAndDate(userId, date)
                .onErrorReturn(new Cart(userId, date)).block();
        userValidation.validateUserWithPrincipal(cart.getUserId());
        Product product = productService.findById(productId).block();
        product.setAmount(amount);
        if (cart.getProducts().contains(product)) {
            int indexOfDuplicated = cart.getProducts().indexOf(product);
            var duplicated = cart.getProducts().remove(indexOfDuplicated);
            product.setAmount(product.getAmount() + duplicated.getAmount());
        }
        cart.getProducts().add(product);
        return cartService.save(cart).map(cartDtoConverter::toDto);
    }

    Mono<Void> deleteProductFromCart(String userId, String productId, LocalDate date) {
        Cart cart = cartService.findByUserIdAndDate(userId, date)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Not found cart for user [id = " + userId +
                        " and date: " + date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "]")))).block();
        userValidation.validateUserWithPrincipal(cart.getUserId());
        Product product = productService.findById(productId).block();
        if (cart.getProducts().contains(product)) {
            cart.getProducts().remove(product);
            cartService.save(cart).block();
        }
        return Mono.empty();
    }

}
