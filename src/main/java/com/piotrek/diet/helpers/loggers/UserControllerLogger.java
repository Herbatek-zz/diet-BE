package com.piotrek.diet.helpers.loggers;

import com.piotrek.diet.meal.MealDto;
import com.piotrek.diet.product.ProductDto;
import com.piotrek.diet.user.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.piotrek.diet.helpers.loggers.Constants.LOGGER_PREFIX;

@Slf4j
@Aspect
@Component
public class UserControllerLogger {

    @Before("execution(* com.piotrek.diet.user.UserController.findUserById(String)) && args(id) ")
    public void logBeforeFindUserById(String id) {
        log.info(LOGGER_PREFIX + "Attempt to find a user [id = " + id + "]");
    }

    @Before(value = "execution(* com.piotrek.diet.user.UserController.updateUser(..)) && args(id, update, response)",
            argNames = "id,update,response")
    public void logBeforeUpdateUser(String id, UserDto update, HttpServletResponse response) {
        log.info(LOGGER_PREFIX + "User [id = " + id + "] attempt to update his profile");
    }

    @Before(value = "execution(* com.piotrek.diet.user.UserController.findUserProducts(String, int, int)) && args(userId, page, size) ",
            argNames = "userId,page,size")
    public void logBeforeRetrieveUserProducts(String userId, int page, int size) {
        log.info(LOGGER_PREFIX + "Attempt to find user products [userId = " + userId + ", page = " + page + " pageSize = " + size + "]");
    }

    @Before(value = "execution(* com.piotrek.diet.user.UserController.findUserMeals(..)) && args(id, page, size)",
            argNames = "id,page,size")
    public void logBeforeRetrieveUserMeals(String id, int page, int size) {
        log.info(LOGGER_PREFIX + "Attempt to find user meals [userId = " + id + ", page = " + page + " pageSize = " + size + "]");
    }

    @Before(value = "execution(* com.piotrek.diet.user.UserController.findUserCart(..)) && args(id, date)", argNames = "id, date")
    public void logBeforeFindUserCart(String id, LocalDate date) {
        log.info(LOGGER_PREFIX + "Attempt to find user cart [userId = " + id + ", date: "
                + date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "]");
    }

    @Before(value = "execution(* com.piotrek.diet.user.UserController.createProduct(..)) && args(id, productDto)", argNames = "id, productDto")
    public void logBeforeCreateProduct(String id, ProductDto productDto) {
        log.info(LOGGER_PREFIX + "Attempt to create a product [userId = " + id + ", product: " + productDto.getName() + "]");
    }

    @Before(value = "execution(* com.piotrek.diet.user.UserController.createMeal(..)) && args(id, mealDto)", argNames = "id, mealDto")
    public void logBeforeCreateMeal(String id, MealDto mealDto) {
        log.info(LOGGER_PREFIX + "Attempt to create a meal [userId = " + id + ", meal: " + mealDto.getName() + "]");
    }

    @Before(value = "execution(* com.piotrek.diet.user.UserController.getFavouriteMeals(..)) && args(id, page, size)",
            argNames = "id, page, size")
    public void logBeforeGetFavouriteMeals(String id, int page, int size) {
        log.info(LOGGER_PREFIX + "Attempt to find user favourite meals [userId = " + id + ", page = " + page + " pageSize = " + size + "]");
    }

    @Before(value = "execution(* com.piotrek.diet.user.UserController.addMealToFavourite(String, String)) && args(userId, mealId) ",
            argNames = "userId,mealId")
    public void logBeforeAddMealToFavourite(String userId, String mealId) {
        log.info(LOGGER_PREFIX + "Attempt to add meal to favourite [id = " + mealId + "] for user [id = " + userId + "]");
    }

    @Before(value = "execution(* com.piotrek.diet.user.UserController.deleteMealFromFavourite(..)) && args(userId, mealId)",
            argNames = "userId, mealId")
    public void logBeforeDeleteMealFromFavourite(String userId, String mealId) {
        log.info(LOGGER_PREFIX + "Attempt to delete a meal from favourite [userId = " + userId + ", mealId = " + mealId + "]");
    }

    @Before(value = "execution(* com.piotrek.diet.user.UserController.isFavouriteMeal(..)) && args(userId, mealId)",
            argNames = "userId, mealId")
    public void logBeforeIsFavouriteMeal(String userId, String mealId) {
        log.info(LOGGER_PREFIX + "Attempt to check if a meal is favourite for user [userId = " + userId + ", mealId = " + mealId + "]");
    }

    @Before(value = "execution(* com.piotrek.diet.user.UserController.addMealToCart(..)) && args(userId, mealId, date, amount)",
            argNames = "userId, mealId, date, amount")
    public void logBeforeAddMealToCart(String userId, String mealId, LocalDate date, int amount) {
        log.info(LOGGER_PREFIX + "Attempt to add a meal to cart [userId = " + userId + ", mealId = " + mealId + ", date: " +
                date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + " amount: " + amount + "]");
    }

    @Before(value = "execution(* com.piotrek.diet.user.UserController.addProductToCart(..)) && args(userId, productId, date, amount)",
            argNames = "userId, productId, date, amount")
    public void logBeforeAddProductToCart(String userId, String productId, LocalDate date, int amount) {
        log.info(LOGGER_PREFIX + "Attempt to add a product to cart [userId = " + userId + ", productId = " + productId + ", date: " +
                date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + " amount: " + amount + "]");
    }

    @Before(value = "execution(* com.piotrek.diet.user.UserController.deleteMealFromCart(..)) && args(userId, mealId, date)",
            argNames = "userId, mealId, date")
    public void logBeforeDeleteMealFromCart(String userId, String mealId, LocalDate date) {
        log.info(LOGGER_PREFIX + "Attempt to delete a meal from cart [userId = " + userId + ", mealId = " + mealId + ", date: " +
                date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "]");
    }

    @Before(value = "execution(* com.piotrek.diet.user.UserController.deleteProductFromCart(..)) && args(userId, productId, date)",
            argNames = "userId, productId, date")
    public void logBeforeDeleteProductFromCart(String userId, String productId, LocalDate date) {
        log.info(LOGGER_PREFIX + "Attempt to delete a product from cart [userId = " + userId + ", productId = " + productId + ", date: " +
                date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "]");
    }
}
