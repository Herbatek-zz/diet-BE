package com.piotrek.diet;

import com.piotrek.diet.config.DataBaseConfigIntegrationTests;
import com.piotrek.diet.model.User;
import com.piotrek.diet.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {DietApplication.class, DataBaseConfigIntegrationTests.class})
class TestConnection {

    @Autowired
    private UserRepository userRepository;

    @Test
    void test() {
        User user = new User();
        user.setFirstName("Maciek");
        userRepository.save(user).block();

        User user1 = userRepository.findAll().blockFirst();
        System.out.println(user.getFirstName());
    }
}
