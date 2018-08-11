package com.piotrek.diet.service;

import com.piotrek.diet.exception.NotFoundException;
import com.piotrek.diet.model.Product;
import com.piotrek.diet.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Mono<Product> findById(String id) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Not found product [id = " + id + "]"))));
    }

    public Mono<Product> save(Product product) {
        return productRepository.save(product);
    }

    public Mono<Void> deleteById(String id) {
        productRepository.deleteById(id);
        return Mono.empty();
    }

    public Mono<Void> deleteAll() {
        productRepository.deleteAll();
        return Mono.empty();
    }
}
