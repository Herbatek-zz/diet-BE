package com.piotrek.diet.service;

import com.piotrek.diet.exception.NotFoundException;
import com.piotrek.diet.model.Product;
import com.piotrek.diet.model.dto.ProductDto;
import com.piotrek.diet.model.dto.converter.ProductDtoConverter;
import com.piotrek.diet.repository.ProductRepository;
import com.piotrek.diet.repository.pagination.PageSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductDtoConverter productDtoConverter;

    public Mono<Product> findById(String id) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Not found product [id = " + id + "]"))));
    }

    public Mono<PageSupport<ProductDto>> findAll(Pageable pageable) {
        return productRepository
                .findAll()
                .map(productDtoConverter::toDto)
                .collectList()
                .map(list -> new PageSupport<>(
                        list
                                .stream()
                                .skip(pageable.getPageNumber() * pageable.getPageSize())
                                .limit(pageable.getPageSize())
                                .collect(Collectors.toList()),
                        pageable.getPageNumber(), pageable.getPageSize(), list.size()));
    }

    public Flux<Product> findAllByUserId(String userId) {
        return productRepository.findAllByUserId(userId);
    }

    public Mono<Product> save(Product product) {
        return productRepository.save(product);
    }

    public Mono<Void> deleteById(String id) {
        productRepository.deleteById(id);
        return Mono.empty();
    }

    public Mono<Void> deleteAll() {
        return productRepository.deleteAll();
    }
}
