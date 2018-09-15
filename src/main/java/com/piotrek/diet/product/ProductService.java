package com.piotrek.diet.product;

import com.piotrek.diet.helpers.DiabetesCalculator;
import com.piotrek.diet.helpers.PageSupport;
import com.piotrek.diet.helpers.exceptions.NotFoundException;
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
    private final DiabetesCalculator diabetesCalculator;

    Mono<Product> findById(String id) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Not found product [id = " + id + "]"))));
    }

    Mono<PageSupport<ProductDto>> searchByName(Pageable pageable, String query) {
        return productRepository
                .findAllByNameIgnoreCaseContaining(query)
                .collectList()
                .map(list -> new PageSupport<>(
                        list
                                .stream()
                                .skip(pageable.getPageNumber() * pageable.getPageSize())
                                .limit(pageable.getPageSize())
                                .map(productDtoConverter::toDto)
                                .collect(Collectors.toList()),
                        pageable.getPageNumber(), pageable.getPageSize(), list.size()));
    }

    Mono<PageSupport<ProductDto>> findAllPageable(Pageable pageable) {
        return productRepository
                .findAll()
                .collectList()
                .map(list -> new PageSupport<>(
                        list
                                .stream()
                                .skip(pageable.getPageNumber() * pageable.getPageSize())
                                .limit(pageable.getPageSize())
                                .map(productDtoConverter::toDto)
                                .collect(Collectors.toList()),
                        pageable.getPageNumber(), pageable.getPageSize(), list.size()));
    }

    Flux<Product> findAllByUserId(String userId) {
        return productRepository.findAllByUserId(userId);
    }

    public Mono<Product> save(Product product) {
        var carbohydrateExchange = diabetesCalculator.calculateCarbohydrateExchange(product.getCarbohydrate(), product.getFibre());
        product.setCarbohydrateExchange(carbohydrateExchange);

        var proteinAndFatEquivalent = diabetesCalculator.calculateProteinAndFatEquivalent(product.getProtein(), product.getFat());
        product.setProteinAndFatEquivalent(proteinAndFatEquivalent);

        return productRepository.save(product);
    }

    Mono<Void> deleteById(String id) {
        return productRepository.deleteById(id);
    }

    Mono<Void> deleteAll() {
        return productRepository.deleteAll();
    }
}
