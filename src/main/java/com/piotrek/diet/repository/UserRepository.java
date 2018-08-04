package com.piotrek.diet.repository;

import com.piotrek.diet.model.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface UserRepository extends ReactiveMongoRepository<User, String> {

    User findByEmail(String email);

}
