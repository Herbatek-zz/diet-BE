package com.piotrek.diet.product;

import com.piotrek.diet.helpers.Page;
import com.piotrek.diet.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
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

    public Mono<Product> findById(String id) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Not found product [id = " + id + "]"))));
    }

    Mono<ProductDto> findDtoById(String id) {
        return findById(id)
                .map(productDtoConverter::toDto);
    }

    Mono<Page<ProductDto>> searchByName(Pageable pageable, String query) {
        return productRepository
                .findAllByNameIgnoreCaseContaining(query)
                .collectList()
                .map(list -> new Page<>(list
                        .stream()
                        .skip(pageable.getPageNumber() * pageable.getPageSize())
                        .limit(pageable.getPageSize())
                        .map(productDtoConverter::toDto)
                        .collect(Collectors.toList()),
                        pageable.getPageNumber(), pageable.getPageSize(), list.size()));
    }

    Mono<Page<ProductDto>> findAllPageable(Pageable pageable) {
        return productRepository
                .findAll()
                .collectList()
                .map(list -> new Page<>(list
                        .stream()
                        .skip(pageable.getPageNumber() * pageable.getPageSize())
                        .limit(pageable.getPageSize())
                        .map(productDtoConverter::toDto)
                        .collect(Collectors.toList()),
                        pageable.getPageNumber(), pageable.getPageSize(), list.size()));
    }

    public Flux<Product> findAll(long skipNumber, int limitNumber) {
        return productRepository.findAll().skip(skipNumber).take(limitNumber);
    }

    public Mono<Page<ProductDto>> findAllByUserPageable(String userId, Pageable pageable) {
        return productRepository.findAllByUserId(userId)
                .collectList()
                .map(list -> new Page<>(list
                        .stream()
                        .skip(pageable.getPageNumber() * pageable.getPageSize())
                        .limit(pageable.getPageSize())
                        .map(productDtoConverter::toDto)
                        .collect(Collectors.toList()),
                        pageable.getPageNumber(), pageable.getPageSize(), list.size()));
    }

    public Mono<ProductDto> save(Product product) {
        var carbohydrateExchange = diabetesCalculator.calculateCarbohydrateExchange(product.getCarbohydrate(), product.getFibre());
        product.setCarbohydrateExchange(carbohydrateExchange);

        var proteinAndFatEquivalent = diabetesCalculator.calculateProteinAndFatEquivalent(product.getProtein(), product.getFat());
        product.setProteinAndFatEquivalent(proteinAndFatEquivalent);

        return productRepository.save(product).map(productDtoConverter::toDto);
    }

    public Mono<ProductDto> save(ProductDto productDto) {
        return save(productDtoConverter.fromDto(productDto));
    }

    @PreAuthorize("@productService.findById(#id).block().getUserId().equals(principal)")
    Mono<Void> deleteById(String id) {
        return productRepository.deleteById(id);
    }

    public Mono<Void> deleteAll() {
        return productRepository.deleteAll();
    }

    public Product calculateProductInfoByAmount(Product product) {
        double divider = (double) product.getAmount() / 100;

        var calculatedProduct = new Product();
        calculatedProduct.setProtein(product.getProtein() * divider);
        calculatedProduct.setCarbohydrate(product.getCarbohydrate() * divider);
        calculatedProduct.setFat(product.getFat() * divider);
        calculatedProduct.setFibre(product.getFibre() * divider);
        calculatedProduct.setKcal(product.getKcal() * divider);
        calculatedProduct.setCarbohydrateExchange(product.getCarbohydrateExchange() * divider);
        calculatedProduct.setProteinAndFatEquivalent(product.getProteinAndFatEquivalent() * divider);
        calculatedProduct.setAmount(product.getAmount());
        calculatedProduct.setUserId(product.getUserId());
        calculatedProduct.setId(product.getId());
        calculatedProduct.setName(product.getName());
        calculatedProduct.setImageUrl(product.getImageUrl());
        calculatedProduct.setDescription(product.getDescription());

        return calculatedProduct;
    }
}
