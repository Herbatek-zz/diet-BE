package com.piotrek.diet.helpers.loggers;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class UserControllerLogger {

    private final String PREFIX = "DIET | ";

    @Before("execution(* com.piotrek.diet.user.UserController.findUserById(String)) && args(id) ")
    public void logBeforeFindUserById(String id) {
        log.info(PREFIX + "Attempt to retrieve a user [id = " + id + "]");
    }

    @Before(value = "execution(* com.piotrek.diet.user.UserController.addMealToFavourite(String, String)) && args(userId, mealId) ",
            argNames = "userId,mealId")
    public void logBeforeAddMealToFavourite(String userId, String mealId) {
        log.info(PREFIX + "Attempt to add meal to favourite [id = " + mealId + "] for user [id = " + userId + "]");
    }

    @Before(value = "execution(* com.piotrek.diet.user.UserController.findUserProducts(String, int, int)) && args(userId, page, size) ",
            argNames = "userId,page,size")
    public void logBeforeRetrieveUserProducts(String userId, int page, int size) {
        log.info(PREFIX + "Attempt to retrieve products for user [id = " + userId + "]");
    }
}
