package com.piotrek.diet.product;

import com.piotrek.diet.helpers.PageSupport;
import com.piotrek.diet.helpers.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static com.piotrek.diet.sample.ProductSample.bananaWithId;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductDtoConverter productDtoConverter = new ProductDtoConverter();

    private ProductService productService;

    private Product product;

    @BeforeEach
    void setup() {
        createProduct();
        MockitoAnnotations.initMocks(this);
        productService = new ProductService(productRepository, productDtoConverter);
    }

    @Test
    void findById_validId_shouldReturnProduct() {
        Mockito.when(productRepository.findById(product.getId())).thenReturn(Mono.just(product));

        Product productFromDB = productService.findById(product.getId()).block();

        assertNotNull(productFromDB);
        assertAll(
                () -> assertEquals(product.getId(), productFromDB.getId()),
                () -> assertEquals(product.getName(), productFromDB.getName()),
                () -> assertEquals(product.getDescription(), productFromDB.getDescription()),
                () -> assertEquals(product.getImageUrl(), productFromDB.getImageUrl()),
                () -> assertEquals(product.getProtein(), productFromDB.getProtein()),
                () -> assertEquals(product.getCarbohydrate(), productFromDB.getCarbohydrate()),
                () -> assertEquals(product.getFat(), productFromDB.getFat()),
                () -> assertEquals(product.getFibre(), productFromDB.getFibre()),
                () -> assertEquals(product.getKcal(), productFromDB.getKcal()),
                () -> assertEquals(product.getCarbohydrateExchange(), productFromDB.getCarbohydrateExchange()),
                () -> assertEquals(product.getProteinAndFatEquivalent(), productFromDB.getProteinAndFatEquivalent())
        );

        verify(productRepository, times(1)).findById(product.getId());
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void findById_invalidId_shouldThrowNotFoundException() {
        var id = "invalid@#@#@ID";
        Mockito.when(productRepository.findById(id)).thenReturn(Mono.empty());

        assertThrows(NotFoundException.class, () -> productService.findById(id).block());

        verify(productRepository, times(1)).findById(id);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void deleteById() {
        assertEquals(Mono.empty(), productService.deleteById(product.getId()));

        verify(productRepository, times(1)).deleteById(product.getId());
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void save() {
        Mockito.when(productRepository.save(product)).thenReturn(Mono.just(product));

        assertEquals(product, productService.save(product).block());

        verify(productRepository, times(1)).save(product);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void findAll_whenTotalElements20AndPageSize10AndPage0_shouldReturnFirstPage() {
        var page = 0;
        var pageSize = 10;
        var totalElements = 20;
        var productList = createProductList(totalElements);
        var expected = new PageSupport<>(productDtoConverter.listToDto(productList)
                .stream()
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);

        Mockito.when(productRepository.findAll()).thenReturn(Flux.fromStream(productList.stream()));

        PageSupport<ProductDto> firstPage = productService.findAll(PageRequest.of(page, pageSize)).block();

        assertEquals(expected, firstPage);
    }

    @Test
    void findAll_whenTotalElements20AndPageSize10AndPage1_shouldReturnSecondPage() {
        var page = 1;
        var pageSize = 10;
        var totalElements = 20;
        var productList = createProductList(totalElements);
        var expected = new PageSupport<>(productDtoConverter.listToDto(productList)
                .stream()
                .skip(pageSize)
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);

        Mockito.when(productRepository.findAll()).thenReturn(Flux.fromStream(productList.stream()));

        PageSupport<ProductDto> firstPage = productService.findAll(PageRequest.of(page, pageSize)).block();

        assertEquals(expected, firstPage);
    }

    @Test
    void findAll_whenTotalElements0AndPageSize10AndPage0_thenReturnFirstPageWithEmptyList() {
        var page = 0;
        var pageSize = 10;
        var totalElements = 0;
        var productList = createProductList(totalElements);
        var expected = new PageSupport<>(productDtoConverter.listToDto(productList)
                .stream()
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);

        Mockito.when(productRepository.findAll()).thenReturn(Flux.fromStream(productList.stream()));

        PageSupport<ProductDto> secondPage = productService.findAll(PageRequest.of(page, pageSize)).block();

        assertEquals(expected, secondPage);
    }

    private void createProduct() {
        product = bananaWithId();
    }

    private ArrayList<Product> createProductList(int size) {
        ArrayList<Product> arrayList = new ArrayList<>();

        for (int i = 0; i < size; i++)
            arrayList.add(bananaWithId());

        return arrayList;
    }
}