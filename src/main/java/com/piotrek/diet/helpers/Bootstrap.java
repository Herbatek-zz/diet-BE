package com.piotrek.diet.helpers;

import com.piotrek.diet.cart.Cart;
import com.piotrek.diet.cart.CartService;
import com.piotrek.diet.meal.Meal;
import com.piotrek.diet.meal.MealService;
import com.piotrek.diet.product.Product;
import com.piotrek.diet.product.ProductService;
import com.piotrek.diet.user.User;
import com.piotrek.diet.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class Bootstrap implements CommandLineRunner {

    private final UserService userService;
    private final ProductService productService;
    private final MealService mealService;
    private final CartService cartService;

    @Override
    public void run(String... args) {
        cartService.deleteAll().block();
        productService.deleteAll().block();
        mealService.deleteAll().block();
        cartService.deleteAll().block();

        User user = userService.findByEmail("fake@mail.com").block();
        if (user == null) {
            user = new User(123L, "fake@mail.com", "Janusz", "Monkey");
            user.setPictureUrl("https://cdn.dribbble.com/users/199982/screenshots/4051304/burhan-avatar-dribbble-grayscale.png");
            user = userService.save(user).block();
        }

        Random random = new Random();

        for (int i = 0; i < 33; i++) {
            Product product = new Product();
            product.setName("Chlebek " + random.nextInt(100));
            product.setDescription("Dobry chlebek, jeszcze ciepły, bardzo pyszny i ładnie pachnie.");
            product.setFat(1.7);
            product.setKcal(227.0);
            product.setProtein(6.3);
            product.setCarbohydrate(42.9);
            product.setFibre(8.4);
            product.setImageUrl("http://static.ilewazy.pl/wp-content/uploads/chleb-zytni-razowy-600g.jpg");
            product.setUserId(user.getId());
            productService.save(product).block();
        }

        for (int i = 0; i < 11; i++) {
            Meal meal = new Meal();
            meal.setName("Kanapki" + random.nextInt(100));
            meal.setDescription("Dobry chlebek, dobra szyneczka i twarożek");
            meal.setRecipe("Rób dobre kanpaki jak chcesz lel");
            meal.setFat(1.7);
            meal.setKcal(227.0);
            meal.setProtein(6.3);
            meal.setCarbohydrate(42.9);
            meal.setFibre(8.4);
            meal.setImageUrl("http://static.ilewazy.pl/wp-content/uploads/chleb-zytni-razowy-600g.jpg");
            meal.setUserId(user.getId());

            ArrayList<Product> products = new ArrayList<>();
            int randomNumber = random.nextInt(33);
            products.addAll(productService.findAll(randomNumber, 33 - randomNumber).collectList().block());

            meal.setProducts(products);
            mealService.save(meal).block();
        }

        var cart = new Cart("123123", LocalDate.now());
        var list = new ArrayList<Meal>();
        var meal = new Meal();
        meal.setName("Dobre");
        list.add(meal);
        cart.setMeals(list);
        cartService.save(cart).block();

    }
}
