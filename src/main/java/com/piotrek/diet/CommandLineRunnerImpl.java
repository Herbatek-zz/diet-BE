package com.piotrek.diet;

import com.piotrek.diet.model.User;
import com.piotrek.diet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CommandLineRunnerImpl implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) {
        User user = new User();
        user.setFirstName("Piotr");
        userRepository.save(user).block();

    }
}
