package com.piotrek.diet.helpers.loggers;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class UserControllerLogger {

    @AfterReturning("execution(* com.piotrek.diet.user.UserController.findUserById(String)) && args(id) ")
    public void logAfterFindUserById(String id) {
        log.info("DIET | Attempt to retrieve a user [id = " + id + "]");
    }

}
