package com.piotrek.diet.product;

import com.piotrek.diet.cloud.CloudStorageService;
import com.piotrek.diet.exceptions.NotFoundException;
import com.piotrek.diet.helpers.Page;
import com.piotrek.diet.helpers.ProductSample;
import com.piotrek.diet.helpers.UserSample;
import org.decimal4j.util.DoubleRounder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.piotrek.diet.helpers.AssertEqualAllFields.assertProductFields;
import static com.piotrek.diet.helpers.Constants.IMAGE_CONTAINER_PRODUCTS;
import static com.piotrek.diet.helpers.ProductSample.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductDtoConverter productDtoConverter;

    @Mock
    private DiabetesCalculator diabetesCalculator;

    private DoubleRounder doubleRounder = new DoubleRounder(2);

    @Mock
    private CloudStorageService imageStorage;

    private ProductService productService;

    private Product product;
    private ProductDto productDto;

    @BeforeEach
    void beforeEach() {
        product = banana();
        productDto = bananaDto();
        MockitoAnnotations.initMocks(this);
        productService = new ProductService(productRepository, productDtoConverter, diabetesCalculator, doubleRounder, imageStorage);
    }

    @Test
    @DisplayName("Find product by id, when found, then return")
    void findById_whenFound_thenReturn() {
        when(productRepository.findById(product.getId())).thenReturn(Mono.just(product));

        final var actualProduct = productService.findById(this.product.getId()).block();

        assertProductFields(product, actualProduct);
        verify(productRepository, times(1)).findById(this.product.getId());
        verifyNoMoreInteractions(productRepository, productDtoConverter, diabetesCalculator, imageStorage);
    }

    @Test
    @DisplayName("Find by id, when not found, then throw NotFoundException")
    void findById_whenIdIsInvalid_thenThrowNotFoundException() {
        final var INVALID_ID = UUID.randomUUID().toString();
        when(productRepository.findById(INVALID_ID)).thenReturn(Mono.empty());

        assertThrows(NotFoundException.class, () -> productService.findById(INVALID_ID).block());
        verify(productRepository, times(1)).findById(INVALID_ID);
        verifyNoMoreInteractions(productRepository, productDtoConverter, diabetesCalculator, imageStorage);
    }

    @Test
    @DisplayName("Find productDto by id, when found, then return")
    void findDtoById_whenFound_thenReturn() {
        when(productRepository.findById(product.getId())).thenReturn(Mono.just(product));
        when(productDtoConverter.toDto(product)).thenReturn(productDto);

        final var actualProductDto = productService.findDtoById(product.getId()).block();

        assertProductFields(productDto, actualProductDto);
        verify(productRepository, times(1)).findById(product.getId());
        verify(productDtoConverter, times(1)).toDto(product);
        verifyNoMoreInteractions(productRepository, productDtoConverter, diabetesCalculator, imageStorage);
    }

    @Test
    @DisplayName("Find productDto, when not found, then return NotFoundException")
    void findDtoById_whenNotFound_thenThrowNotFoundException() {
        final var INVALID_ID = UUID.randomUUID().toString();
        when(productRepository.findById(INVALID_ID)).thenReturn(Mono.empty());

        assertThrows(NotFoundException.class, () -> productService.findDtoById(INVALID_ID).block());
        verify(productRepository, times(1)).findById(INVALID_ID);
        verifyNoMoreInteractions(productRepository, productDtoConverter, diabetesCalculator, imageStorage);
    }

    @Test
    @DisplayName("Search product by name, when no products, then return empty page")
    void searchByName_whenNoProducts_thenReturnEmptyPage() {
        final var page = 0;
        final var pageSize = 10;
        final var totalElements = 0;
        final var query = "name";
        final var productList = createProductList(totalElements, BANANA);
        final var productDtoList = createProductDtoList(totalElements, BANANA);
        final var expected = new Page<>(productDtoList
                .stream()
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);

        when(productRepository.findAllByNameIgnoreCaseContaining(query)).thenReturn(Flux.fromIterable(productList));

        final var actualPage = productService.searchByName(PageRequest.of(page, pageSize), query).block();

        assertEquals(expected, actualPage);
        verify(productRepository, times(1)).findAllByNameIgnoreCaseContaining(query);
        verifyNoMoreInteractions(productRepository, productDtoConverter, diabetesCalculator, imageStorage);
    }

    @Test
    @DisplayName("Search by name, when two products and query matches two of them, then return page with two products")
    void searchByName_whenTwoProductsAndQueryMatchesTwoOfThem_thenReturnPageWithTwoProducts() {
        final var page = 0;
        final var pageSize = 10;
        final var totalElements = 2;
        final var query = banana().getName();
        final var productList = createProductList(totalElements, BANANA);
        final var expected = new Page<>(createProductDtoList(totalElements, BANANA), page, pageSize, totalElements);

        when(productRepository.findAllByNameIgnoreCaseContaining(query)).thenReturn(Flux.fromIterable(productList));
        when(productDtoConverter.toDto(banana())).thenReturn(bananaDto());

        final var actualPage = productService.searchByName(PageRequest.of(page, pageSize), query).block();

        assertEquals(expected, actualPage);
        verify(productRepository, times(1)).findAllByNameIgnoreCaseContaining(query);
        verify(productDtoConverter, times(totalElements)).toDto(banana());
        verifyNoMoreInteractions(productRepository, productDtoConverter, diabetesCalculator, imageStorage);
    }

    @Test
    void searchByName_whenTwentyTwoProductsAndQueryMatchesThem_thenReturnPageWithTenResultsInFirstPage() {
        final var page = 0;
        final var pageSize = 10;
        final var totalElements = 22;
        final var query = banana().getName();
        final var productList = createProductList(22, BANANA);
        final var productDtoList = createProductDtoList(22, BANANA);
        final var expected = new Page<>(productDtoList
                .stream()
                .skip(page * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);

        when(productRepository.findAllByNameIgnoreCaseContaining(query)).thenReturn(Flux.fromIterable(productList));
        when(productDtoConverter.toDto(banana())).thenReturn(bananaDto());

        final var actualFirstPage = productService.searchByName(PageRequest.of(page, pageSize), query).block();

        assertEquals(expected, actualFirstPage);
        verify(productRepository, times(1)).findAllByNameIgnoreCaseContaining(query);
        verify(productDtoConverter, times(pageSize)).toDto(banana());
        verifyNoMoreInteractions(productRepository, productDtoConverter, diabetesCalculator, imageStorage);
    }

    @Test
    void updateProduct_whenOk_thenUpdateProduct() {
        productDto.setName("updated");
        productDto.setProtein(33);
        productDto.setCarbohydrate(50);
        productDto.setFibre(2);
        productDto.setFat(15);

        when(productRepository.findById(product.getId())).thenReturn(Mono.just(product));
        when(productRepository.save(product)).thenReturn(Mono.just(product));
        when(productDtoConverter.toDto(product)).thenReturn(productDto);

        var actual = productService.updateProduct(product.getId(), productDto).block();

        assertProductFields(productDto, actual);
        verify(productRepository, times(1)).findById(product.getId());
        verify(diabetesCalculator, times(1))
                .calculateProteinAndFatEquivalent(productDto.getProtein(), productDto.getFat());
        verify(diabetesCalculator, times(1))
                .calculateCarbohydrateExchange(productDto.getCarbohydrate(), productDto.getFibre());
        verify(productRepository, times(1)).save(product);
        verify(productDtoConverter, times(1)).toDto(product);
        verifyNoMoreInteractions(productRepository, productDtoConverter, diabetesCalculator, imageStorage);
    }

    @Test
    void updateProduct_whenImageFileIsNotNull_thenUpdateProductWithImage() {
        final String IMAGE_URL = "some-fake-image-url.com/so-funny-image";
        productDto.setImageToSave(new MockMultipartFile("fileName", new byte[23]));

        when(productRepository.findById(product.getId())).thenReturn(Mono.just(product));
        when(productRepository.save(product)).thenReturn(Mono.just(product));
        when(productDtoConverter.toDto(product)).thenReturn(productDto);
        when(imageStorage.uploadImageBlob(IMAGE_CONTAINER_PRODUCTS, productDto.getId(), productDto.getImageToSave())).thenReturn(IMAGE_URL);

        var actual = productService.updateProduct(product.getId(), productDto).block();



        assertProductFields(productDto, actual);
        verify(productRepository, times(1)).findById(product.getId());
        verify(imageStorage, times(1)).uploadImageBlob(IMAGE_CONTAINER_PRODUCTS, productDto.getId(), productDto.getImageToSave());
        verify(diabetesCalculator, times(1)).calculateProteinAndFatEquivalent(productDto.getProtein(), productDto.getFat());
        verify(diabetesCalculator, times(1)).calculateCarbohydrateExchange(productDto.getCarbohydrate(), productDto.getFibre());
        verify(productRepository, times(1)).save(product);
        verify(productDtoConverter, times(1)).toDto(product);
        verifyNoMoreInteractions(productRepository, productDtoConverter, diabetesCalculator, imageStorage);
    }

    @Test
    @DisplayName("Update product, when not no product, then return NotFoundException")
    void updateProduct_whenNotFound_thenReturnNotFoundException() {
        when(productRepository.findById(product.getId())).thenReturn(Mono.empty());

        assertThrows(NotFoundException.class, () -> productService.updateProduct(product.getId(), productDto).block());
        verify(productRepository, times(1)).findById(product.getId());
        verifyNoMoreInteractions(productRepository, productDtoConverter, diabetesCalculator, imageStorage);
    }

    @Test
    @DisplayName("Find all products, skip two and limit two, when found ten, then skip two and return 2 products")
    void findAll_whenTenProductsSkipTwoAndLimitTwo_thenReturnTwoProducts() {
        final var productsList = createProductList(10, BANANA);
        when(productRepository.findAll()).thenReturn(Flux.fromIterable(productsList));

        final var actualProducts = productService.findAll(2, 2).collectList().block();

        assertNotNull(actualProducts);
        assertAll(
                () -> assertProductFields(productsList.get(2), actualProducts.get(0)),
                () -> assertProductFields(productsList.get(3), actualProducts.get(1))
        );
        verify(productRepository, times(1)).findAll();
        verifyNoMoreInteractions(productRepository, productDtoConverter, diabetesCalculator, imageStorage);
    }

    @Test
    @DisplayName("Find all products pageable, when found twenty products, then return first page with ten products")
    void findAllPageable_whenFoundTwenty_thenReturnFirstPageWith10Products() {
        final var page = 0;
        final var pageSize = 10;
        final var totalElements = 20;
        final var productList = createProductList(totalElements, BANANA);
        final var productDtoList = createProductDtoList(totalElements, BANANA);
        final var expected = new Page<>(productDtoList
                .stream()
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);

        when(productRepository.findAll()).thenReturn(Flux.fromIterable(productList));
        when(productDtoConverter.toDto(banana())).thenReturn(bananaDto());

        final var firstPage = productService.findAllPageable(PageRequest.of(page, pageSize)).block();

        assertEquals(expected, firstPage);
        verify(productRepository, times(1)).findAll();
        verify(productDtoConverter, times(10)).toDto(banana());
        verifyNoMoreInteractions(productRepository, productDtoConverter, diabetesCalculator, imageStorage);
    }

    @Test
    @DisplayName("Find all products pageable, when found twenty products and page=1, then return second page with ten products")
    void findAllPageable_whenFoundTwentyProductsAndPageOne_thenReturnSecondPageWithTenProducts() {
        final var page = 1;
        final var pageSize = 10;
        final var totalElements = 20;
        final var productList = createProductList(totalElements, BANANA);
        final var productDtoList = createProductDtoList(totalElements, BANANA);
        final var expected = new Page<>(productDtoList
                .stream()
                .skip(pageSize * page)
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);

        when(productRepository.findAll()).thenReturn(Flux.fromIterable(productList));
        when(productDtoConverter.toDto(banana())).thenReturn(bananaDto());

        final var firstPage = productService.findAllPageable(PageRequest.of(page, pageSize)).block();

        assertEquals(expected, firstPage);
        verify(productRepository, times(1)).findAll();
        verify(productDtoConverter, times(10)).toDto(banana());
        verifyNoMoreInteractions(productRepository, productDtoConverter, diabetesCalculator, imageStorage);
    }

    @Test
    @DisplayName("Find all products pageable, when no products, then return empty page")
    void findAllPageable_whenNoProducts_thenReturnEmptyPage() {
        final var page = 0;
        final var pageSize = 10;
        final var totalElements = 0;
        final var productList = createProductList(totalElements, BANANA);
        final var productDtoList = createProductDtoList(totalElements, BANANA);
        final var expected = new Page<>(productDtoList, page, pageSize, totalElements);

        when(productRepository.findAll()).thenReturn(Flux.fromIterable(productList));

        final var secondPage = productService.findAllPageable(PageRequest.of(page, pageSize)).block();

        assertEquals(expected, secondPage);
        verify(productRepository, times(1)).findAll();
        verifyNoMoreInteractions(productRepository, productDtoConverter, diabetesCalculator, imageStorage);
    }

    @Test
    @DisplayName("Find all products by userId, when user has five products, then return page with 5 products")
    void findAllByUserId_whenUserHasFiveProducts_thenReturn() {
        final var page = 0;
        final var pageSize = 10;
        final var totalElements = 5;
        final var productList = createProductList(totalElements, BANANA);
        final var productDtoList = createProductDtoList(totalElements, BANANA);
        final var expected = new Page<>(productDtoList, page, pageSize, totalElements);
        final var user = UserSample.john();

        when(productRepository.findAllByUserId(user.getId())).thenReturn(Flux.fromIterable(productList));
        when(productDtoConverter.toDto(banana())).thenReturn(bananaDto());

        final var block = productService.findAllByUserPageable(user.getId(), PageRequest.of(page, pageSize)).block();

        assertNotNull(block);
        assertEquals(expected, block);
        verify(productRepository, times(1)).findAllByUserId(user.getId());
        verify(productDtoConverter, times(totalElements)).toDto(banana());
        verifyNoMoreInteractions(productRepository, productDtoConverter, diabetesCalculator, imageStorage);
    }

    @Test
    @DisplayName("Find all products by user id, when user has no products, then return empty page")
    void findAllByUserId_whenUserHasNoProducts_thenReturnEmptyList() {
        final var page = 0;
        final var pageSize = 10;
        final var totalElements = 0;
        final var expected = new Page<>(new ArrayList<ProductDto>(), page, pageSize, totalElements);
        final var user = UserSample.john();

        when(productRepository.findAllByUserId(user.getId())).thenReturn(Flux.empty());

        final var actualPage = productService.findAllByUserPageable(user.getId(), PageRequest.of(page, pageSize)).block();

        assertNotNull(actualPage);
        assertEquals(expected, actualPage);
        verify(productRepository, times(1)).findAllByUserId(user.getId());
        verifyNoMoreInteractions(productRepository, productDtoConverter, diabetesCalculator, imageStorage);
    }

    @Test
    void save() {
        when(productRepository.save(product)).thenReturn(Mono.just(product));
        when(productDtoConverter.toDto(product)).thenReturn(productDto);

        ProductDto saved = productService.save(product).block();

        assertProductFields(productDto, saved);
        verify(productRepository, times(1)).save(product);
        verify(productDtoConverter, times(1)).toDto(product);
        verify(diabetesCalculator, times(1)).calculateProteinAndFatEquivalent(product.getProtein(), product.getFat());
        verify(diabetesCalculator, times(1)).calculateCarbohydrateExchange(product.getCarbohydrate(), product.getFibre());
        verifyNoMoreInteractions(productRepository, productDtoConverter, diabetesCalculator, imageStorage);
    }

    @Test
    void saveDto() {
        when(productRepository.save(product)).thenReturn(Mono.just(product));
        when(productDtoConverter.fromDto(productDto)).thenReturn(product);
        when(productDtoConverter.toDto(product)).thenReturn(productDto);

        ProductDto saved = productService.save(productDto).block();

        assertProductFields(productDto, saved);
        verify(productRepository, times(1)).save(product);
        verify(productDtoConverter, times(1)).fromDto(productDto);
        verify(productDtoConverter, times(1)).toDto(product);
        verify(diabetesCalculator, times(1)).calculateProteinAndFatEquivalent(product.getProtein(), product.getFat());
        verify(diabetesCalculator, times(1)).calculateCarbohydrateExchange(product.getCarbohydrate(), product.getFibre());
        verifyNoMoreInteractions(productRepository, productDtoConverter, diabetesCalculator, imageStorage);
    }

    @Test
    void deleteById() {
        when(productRepository.findById(product.getId())).thenReturn(Mono.just(product));

        assertEquals(Mono.empty().block(), productService.deleteById(product.getId()));
        verify(productRepository, times(1)).deleteById(product.getId());
        verifyNoMoreInteractions(productRepository, productDtoConverter, diabetesCalculator, imageStorage);
    }

    @Test
    void deleteAll() {
        assertEquals(Mono.empty().block(), productService.deleteAll());

        verify(productRepository, times(1)).deleteAll();
        verifyNoMoreInteractions(productRepository, productDtoConverter, diabetesCalculator, imageStorage);
    }

    @Test
    void calculateProductInfoByAmount_whenProductAmount80_thenCalculate() {
        Product expected = ProductSample.banana();
        expected.setAmount(80);
        expected.setKcal(doubleRounder.round(expected.getKcal() * 0.8));
        expected.setCarbohydrate(doubleRounder.round(expected.getCarbohydrate() * 0.8));
        expected.setFat(doubleRounder.round(expected.getFat() * 0.8));
        expected.setProtein(doubleRounder.round(expected.getProtein() * 0.8));
        expected.setFibre(doubleRounder.round(expected.getFibre() * 0.8));
        expected.setProteinAndFatEquivalent(doubleRounder.round(expected.getProteinAndFatEquivalent() * 0.8));
        expected.setCarbohydrateExchange(doubleRounder.round(expected.getCarbohydrateExchange() * 0.8));

        Product actual = ProductSample.banana();
        actual.setAmount(80);

        actual = productService.calculateProductInfoByAmount(actual);

        assertProductFields(expected, actual);
        verifyNoMoreInteractions(productRepository, productDtoConverter, diabetesCalculator, imageStorage);
    }

    @Test
    void calculateProductInfoByAmount_whenProductAmount0_thenCalculate() {
        Product expected = ProductSample.banana();
        expected.setAmount(0);
        expected.setKcal(0);
        expected.setCarbohydrate(0);
        expected.setFat(0);
        expected.setProtein(0);
        expected.setFibre(0);
        expected.setProteinAndFatEquivalent(0);
        expected.setCarbohydrateExchange(0);

        Product actual = ProductSample.banana();
        actual.setAmount(0);

        actual = productService.calculateProductInfoByAmount(actual);

        assertProductFields(expected, actual);
        verifyNoMoreInteractions(productRepository, productDtoConverter, diabetesCalculator, imageStorage);
    }

    @Test
    void calculateProductInfoByAmount_whenProductAmount380_thenCalculate() {
        Product expected = ProductSample.banana();
        expected.setAmount(380);
        expected.setKcal(doubleRounder.round(expected.getKcal() * 3.8));
        expected.setCarbohydrate(doubleRounder.round(expected.getCarbohydrate() * 3.8));
        expected.setFat(doubleRounder.round(expected.getFat() * 3.8));
        expected.setProtein(doubleRounder.round(expected.getProtein() * 3.8));
        expected.setFibre(doubleRounder.round(expected.getFibre() * 3.8));
        expected.setProteinAndFatEquivalent(doubleRounder.round(expected.getProteinAndFatEquivalent() * 3.8));
        expected.setCarbohydrateExchange(doubleRounder.round(expected.getCarbohydrateExchange() * 3.8));

        Product actual = ProductSample.banana();
        actual.setAmount(380);

        actual = productService.calculateProductInfoByAmount(actual);

        assertProductFields(expected, actual);
        verifyNoMoreInteractions(productRepository, productDtoConverter, diabetesCalculator, imageStorage);
    }


    private ArrayList<Product> createProductList(int size, String product) {
        var arrayList = new ArrayList<Product>();

        switch (product) {
            case BANANA:
                for (int i = 0; i < size; i++)
                    arrayList.add(banana());
                break;
            case BREAD:
                for (int i = 0; i < size; i++)
                    arrayList.add(bread());
                break;
        }
        return arrayList;
    }

    private ArrayList<ProductDto> createProductDtoList(int size, String product) {
        var arrayList = new ArrayList<ProductDto>();

        switch (product) {
            case BANANA:
                for (int i = 0; i < size; i++)
                    arrayList.add(bananaDto());
                break;
            case BREAD:
                for (int i = 0; i < size; i++)
                    arrayList.add(breadDto());
                break;
        }
        return arrayList;
    }
}