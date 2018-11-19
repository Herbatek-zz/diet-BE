package com.piotrek.diet.product;

import com.piotrek.diet.exceptions.NotFoundException;
import com.piotrek.diet.helpers.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.decimal4j.util.DoubleRounder;
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
    private final DoubleRounder doubleRounder;

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

    @PreAuthorize("@productService.findById(#id).block().getUserId().equals(principal)")
    Mono<ProductDto> updateProduct(String id, ProductDto productUpdate) {
        return findById(id)
                .doOnNext(product -> product.setName(productUpdate.getName()))
                .doOnNext(product -> product.setDescription(productUpdate.getDescription()))
                .doOnNext(product -> product.setDescription(productUpdate.getDescription()))
                .doOnNext(product -> product.setImageUrl(productUpdate.getImageUrl()))
                .doOnNext(product -> product.setProtein(productUpdate.getProtein()))
                .doOnNext(product -> product.setCarbohydrate(productUpdate.getCarbohydrate()))
                .doOnNext(product -> product.setFat(productUpdate.getFat()))
                .doOnNext(product -> product.setFibre(productUpdate.getFibre()))
                .doOnNext(product -> product.setKcal(productUpdate.getKcal()))
                .flatMap(this::save);
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

        product.setAmount(100);

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
        calculatedProduct.setProtein(doubleRounder.round(product.getProtein() * divider));
        calculatedProduct.setCarbohydrate(doubleRounder.round(product.getCarbohydrate() * divider));
        calculatedProduct.setFat(doubleRounder.round(product.getFat() * divider));
        calculatedProduct.setFibre(doubleRounder.round(product.getFibre() * divider));
        calculatedProduct.setKcal(doubleRounder.round(product.getKcal() * divider));
        calculatedProduct.setCarbohydrateExchange(doubleRounder.round(product.getCarbohydrateExchange() * divider));
        calculatedProduct.setProteinAndFatEquivalent(doubleRounder.round(product.getProteinAndFatEquivalent() * divider));
        calculatedProduct.setAmount(product.getAmount());
        calculatedProduct.setUserId(product.getUserId());
        calculatedProduct.setId(product.getId());
        calculatedProduct.setName(product.getName());
        calculatedProduct.setImageUrl(product.getImageUrl());
        calculatedProduct.setDescription(product.getDescription());

        return calculatedProduct;
    }
}
