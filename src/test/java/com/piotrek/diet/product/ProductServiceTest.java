package com.piotrek.diet.product;

import com.piotrek.diet.helpers.DiabetesCalculator;
import com.piotrek.diet.helpers.Page;
import com.piotrek.diet.helpers.exceptions.NotFoundException;
import com.piotrek.diet.sample.UserSample;
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

import static com.piotrek.diet.sample.ProductSample.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductDtoConverter productDtoConverter;

    @Mock
    private DiabetesCalculator diabetesCalculator;

    private ProductService productService;

    private Product product;

    @BeforeEach
    void setup() {
        product = bananaWithId();
        MockitoAnnotations.initMocks(this);
        productService = new ProductService(productRepository, productDtoConverter, diabetesCalculator);
    }

    @Test
    void findById_whenIdIsValid_thenReturnProduct() {
        Mockito.when(productRepository.findById(product.getId())).thenReturn(Mono.just(product));

        var productById = productService.findById(product.getId()).block();

        assertNotNull(productById);
        assertAll(
                () -> assertEquals(product.getId(), productById.getId()),
                () -> assertEquals(product.getName(), productById.getName()),
                () -> assertEquals(product.getDescription(), productById.getDescription()),
                () -> assertEquals(product.getImageUrl(), productById.getImageUrl()),
                () -> assertEquals(product.getProtein(), productById.getProtein()),
                () -> assertEquals(product.getCarbohydrate(), productById.getCarbohydrate()),
                () -> assertEquals(product.getFat(), productById.getFat()),
                () -> assertEquals(product.getFibre(), productById.getFibre()),
                () -> assertEquals(product.getKcal(), productById.getKcal()),
                () -> assertEquals(product.getCarbohydrateExchange(), productById.getCarbohydrateExchange()),
                () -> assertEquals(product.getProteinAndFatEquivalent(), productById.getProteinAndFatEquivalent()),
                () -> assertEquals(product.getUserId(), productById.getUserId()),
                () -> assertEquals(product.getAmount(), productById.getAmount())
        );

        verify(productRepository, times(1)).findById(product.getId());
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void findById_whenIdIsInvalid_thenThrowNotFoundException() {
        var id = "invalid@#@#@ID";
        Mockito.when(productRepository.findById(id)).thenReturn(Mono.empty());

        assertThrows(NotFoundException.class, () -> productService.findById(id).block());

        verify(productRepository, times(1)).findById(id);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void searchByName_whenNoProductsAndNoResult_thenReturnEmptyPagable() {
        var page = 0;
        var pageSize = 10;
        var totalElements = 0;
        var query = "name";
        var productList = createProductList(totalElements, BANANA);
        var productDtoList = createProductDtoList(totalElements, BANANA);
        var expected = new Page<>(productDtoList
                .stream()
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);

        Mockito.when(productRepository.findAllByNameIgnoreCaseContaining(query)).thenReturn(Flux.fromIterable(productList));
        Mockito.when(productDtoConverter.toDto(bananaWithId())).thenReturn(bananaWithIdDto());

        var firstPage = productService.searchByName(PageRequest.of(page, pageSize), query).block();

        assertEquals(expected, firstPage);

        verify(productRepository, times(1)).findAllByNameIgnoreCaseContaining(query);
        verify(productDtoConverter, times(0)).toDto(bananaWithId());
        verifyNoMoreInteractions(productRepository);
        verifyNoMoreInteractions(productDtoConverter);
    }

    @Test
    void searchByName_when2ProductsAnd2Results_thenReturnPagableWith2Results() {
        var page = 0;
        var pageSize = 10;
        var totalElements = 2;
        var query = bananaWithId().getName();
        var productList = createProductList(totalElements, BANANA);
        var productDtoList = createProductDtoList(totalElements, BANANA);
        var expected = new Page<>(productDtoList
                .stream()
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);

        Mockito.when(productRepository.findAllByNameIgnoreCaseContaining(query)).thenReturn(Flux.fromIterable(productList));
        Mockito.when(productDtoConverter.toDto(bananaWithId())).thenReturn(bananaWithIdDto());

        var firstPage = productService.searchByName(PageRequest.of(page, pageSize), query).block();

        assertEquals(expected, firstPage);

        verify(productRepository, times(1)).findAllByNameIgnoreCaseContaining(query);
        verify(productDtoConverter, times(2)).toDto(bananaWithId());
        verifyNoMoreInteractions(productRepository);
        verifyNoMoreInteractions(productDtoConverter);

    }

    @Test
    void searchByName_when22productsAnd12Results_thenReturnPagableWith10ResultsInFirstPage() {
        var page = 0;
        var pageSize = 10;
        var totalElements = 22;
        var query = bananaWithId().getName();
        var productList = createProductList(12, BANANA);
        productList.addAll(createProductList(10, BREAD));
        var productDtoList = createProductDtoList(12, BANANA);
        productDtoList.addAll(createProductDtoList(10, BREAD));
        var expected = new Page<>(productDtoList
                .stream()
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);

        Mockito.when(productRepository.findAllByNameIgnoreCaseContaining(query)).thenReturn(Flux.fromIterable(productList));
        Mockito.when(productDtoConverter.toDto(bananaWithId())).thenReturn(bananaWithIdDto());

        var firstPage = productService.searchByName(PageRequest.of(page, pageSize), query).block();

        assertEquals(expected, firstPage);

        verify(productRepository, times(1)).findAllByNameIgnoreCaseContaining(query);
        verify(productDtoConverter, times(pageSize)).toDto(bananaWithId());
        verifyNoMoreInteractions(productRepository);
        verifyNoMoreInteractions(productDtoConverter);
    }


    @Test
    void findAllPageable_whenTotalElements20PageSize10Page0_thenReturnFirstPageWith10Products() {
        var page = 0;
        var pageSize = 10;
        var totalElements = 20;
        var productList = createProductList(totalElements, BANANA);
        var productDtoList = createProductDtoList(totalElements, BANANA);
        var expected = new Page<>(productDtoList
                .stream()
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);

        Mockito.when(productRepository.findAll()).thenReturn(Flux.fromIterable(productList));
        Mockito.when(productDtoConverter.toDto(bananaWithId())).thenReturn(bananaWithIdDto());

        var firstPage = productService.findAllPageable(PageRequest.of(page, pageSize)).block();

        assertEquals(expected, firstPage);

        verify(productRepository, times(1)).findAll();
        verify(productDtoConverter, times(10)).toDto(bananaWithId());
        verifyNoMoreInteractions(productRepository);
        verifyNoMoreInteractions(productDtoConverter);
    }

    @Test
    void findAllPageable_whenTotalElements20PageSize10Page1_thenReturnSecondPageWith10Products() {
        var page = 1;
        var pageSize = 10;
        var totalElements = 20;
        var productList = createProductList(totalElements, BANANA);
        var productDtoList = createProductDtoList(totalElements, BANANA);
        var expected = new Page<>(productDtoList
                .stream()
                .skip(pageSize)
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);

        Mockito.when(productRepository.findAll()).thenReturn(Flux.fromIterable(productList));
        Mockito.when(productDtoConverter.toDto(bananaWithId())).thenReturn(bananaWithIdDto());

        var firstPage = productService.findAllPageable(PageRequest.of(page, pageSize)).block();

        assertEquals(expected, firstPage);

        verify(productRepository, times(1)).findAll();
        verify(productDtoConverter, times(10)).toDto(bananaWithId());
        verifyNoMoreInteractions(productRepository);
        verifyNoMoreInteractions(productDtoConverter);
    }

    @Test
    void findAllPageable_whenTotalElements0PageSize10Page0_thenReturnFirstPageWithEmptyList() {
        var page = 0;
        var pageSize = 10;
        var totalElements = 0;
        var productList = createProductList(totalElements, BANANA);
        var productDtoList = createProductDtoList(totalElements, BANANA);
        var expected = new Page<>(productDtoList
                .stream()
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);

        Mockito.when(productRepository.findAll()).thenReturn(Flux.fromIterable(productList));

        var secondPage = productService.findAllPageable(PageRequest.of(page, pageSize)).block();

        assertEquals(expected, secondPage);

        verify(productRepository, times(1)).findAll();
        verifyNoMoreInteractions(productRepository);
        verifyNoMoreInteractions(productDtoConverter);
    }

    @Test
    void findAllByUserId_whenUserHas5Products_thenReturn5Product() {
        var productList = createProductList(5, BANANA);
        var user = UserSample.johnWithId();

        when(productRepository.findAllByUserId(user.getId())).thenReturn(Flux.fromIterable(productList));

        productService.findAllByUserId(user.getId()).toStream().collect(Collectors.toList());

        verify(productRepository, times(1)).findAllByUserId(user.getId());
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void findAllByUserId_whenTotalProducts1AndUserHas0_thenReturn1Product() {
        var productList = new ArrayList<Product>();
        var user = UserSample.johnWithId();

        when(productRepository.findAllByUserId(user.getId())).thenReturn(Flux.empty());

        var allByUserId = productService.findAllByUserId(user.getId()).toStream().collect(Collectors.toList());

        assertAll(
                () -> assertEquals(productList, allByUserId),
                () -> assertEquals(productList.size(), allByUserId.size())
        );

        verify(productRepository, times(1)).findAllByUserId(user.getId());
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void save() {
        Mockito.when(productRepository.save(product)).thenReturn(Mono.just(product));

        assertEquals(product, productService.save(product).block());

        verify(productRepository, times(1)).save(product);
        verify(diabetesCalculator, times(1)).calculateProteinAndFatEquivalent(product.getProtein(), product.getFat());
        verify(diabetesCalculator, times(1)).calculateCarbohydrateExchange(product.getCarbohydrate(), product.getFibre());
        verifyNoMoreInteractions(productRepository);
        verifyNoMoreInteractions(diabetesCalculator);
    }

    @Test
    void deleteById() {
        assertEquals(Mono.empty().block(), productService.deleteById(product.getId()));

        verify(productRepository, times(1)).deleteById(product.getId());
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void deleteAll() {
        assertEquals(Mono.empty().block(), productService.deleteAll());

        verify(productRepository, times(1)).deleteAll();
        verifyNoMoreInteractions(productRepository);
    }


    private ArrayList<Product> createProductList(int size, String product) {
        var arrayList = new ArrayList<Product>();

        switch (product) {
            case BANANA:
                for (int i = 0; i < size; i++)
                    arrayList.add(bananaWithId());
                break;
            case BREAD:
                for (int i = 0; i < size; i++)
                    arrayList.add(breadWithId());
                break;
        }
        return arrayList;
    }

    private ArrayList<ProductDto> createProductDtoList(int size, String product) {
        var arrayList = new ArrayList<ProductDto>();

        switch (product) {
            case BANANA:
                for (int i = 0; i < size; i++)
                    arrayList.add(bananaWithIdDto());
                break;
            case BREAD:
                for (int i = 0; i < size; i++)
                    arrayList.add(breadWithIdDto());
                break;
        }
        return arrayList;
    }
}