package com.piotrek.diet;

import com.piotrek.diet.config.DataBaseConfigIntegrationTests;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {DietApplication.class, DataBaseConfigIntegrationTests.class})
class DietApplicationTests {

    @Test
    void contextLoads() {
    }

}
