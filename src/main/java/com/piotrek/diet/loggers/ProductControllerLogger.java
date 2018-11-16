package com.piotrek.diet.loggers;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import static com.piotrek.diet.loggers.Constants.LOGGER_PREFIX;

@Slf4j
@Aspect
@Component
public class ProductControllerLogger {

    @Before(value = "execution(* com.piotrek.diet.product.ProductController.findAll(..)) && args(page, size)", argNames = "page,size")
    public void logBeforeFindAllProducts(int page, int size) {
        log.info(LOGGER_PREFIX + "Attempt to find all products [page = " + page + ", size = " + size + "]");
    }

    @Before(value = "execution(* com.piotrek.diet.product.ProductController.findById(..)) && args(id)")
    public void logBeforeFindByIdProduct(String id) {
        log.info(LOGGER_PREFIX + "Attempt to find a product [id = " + id + "]");
    }

    @Before(value = "execution(* com.piotrek.diet.product.ProductController.searchByName(..)) && args(page, size, query)", argNames = "page,size,query")
    public void logBeforeSearchByNameProducts(int page, int size, String query) {
        log.info(LOGGER_PREFIX + "Attempt to search by name a product [query = " + query + ", page = " +
                page + ", size = " + size + "]");
    }

    @Before(value = "execution(* com.piotrek.diet.product.ProductController.deleteById(..)) && args(id)")
    public void logBeforeDeleteByIdProduct(String id) {
        log.info(LOGGER_PREFIX + "Attempt to delete a product [id = " + id + "]");
    }
}
