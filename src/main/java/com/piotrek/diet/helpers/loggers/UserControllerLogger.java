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
    public void logAfterFindUserById(String id) {
        log.info(PREFIX + "Attempt to retrieve a user [id = " + id + "]");
    }

    @Before(value = "execution(* com.piotrek.diet.user.UserController.addMealToFavourite(String, String)) && args(userId, mealId) ",
            argNames = "userId,mealId")
    public void logAfterAddMealToFavourite(String userId, String mealId) {
        log.info(PREFIX + "Attempt to add meal to favourite [id = " + mealId + "] for user [id = " + userId + "]");
    }
}
