package com.piotrek.diet.meal;

import com.piotrek.diet.helpers.Page;
import com.piotrek.diet.helpers.exceptions.NotFoundException;
import com.piotrek.diet.product.Product;
import com.piotrek.diet.product.ProductDto;
import com.piotrek.diet.product.ProductDtoConverter;
import com.piotrek.diet.sample.UserSample;
import com.piotrek.diet.user.UserValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import static com.piotrek.diet.sample.MealSample.*;
import static com.piotrek.diet.sample.ProductSample.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MealServiceTest {

    @Mock
    private MealRepository mealRepository;

    @Mock
    private MealDtoConverter mealDtoConverter;

    @Mock
    private ProductDtoConverter productDtoConverter;

    @Mock
    private UserValidation userValidation;

    private MealService mealService;

    private Meal meal1;
    private MealDto meal1Dto;

    @BeforeEach
    void beforeEach() {
        meal1 = dumplingsWithId();
        meal1Dto = dumplingsWithIdDto();
        MockitoAnnotations.initMocks(this);
        mealService = new MealService(mealRepository, mealDtoConverter, productDtoConverter, userValidation);
    }

    @Test
    @DisplayName("When findById and found meal, then the meal should be returned")
    void findById_whenSuccess_thenReturnMeal() {
        Mockito.when(mealRepository.findById(meal1.getId())).thenReturn(Mono.just(meal1));

        final var byId = mealService.findById(meal1.getId()).block();

        assertNotNull(byId);
        assertAll(
                () -> assertEquals(meal1.getId(), byId.getId()),
                () -> assertEquals(meal1.getName(), byId.getName()),
                () -> assertEquals(meal1.getDescription(), byId.getDescription()),
                () -> assertEquals(meal1.getRecipe(), byId.getRecipe()),
                () -> assertEquals(meal1.getImageUrl(), byId.getImageUrl()),
                () -> assertEquals(meal1.getCarbohydrate(), byId.getCarbohydrate()),
                () -> assertEquals(meal1.getFibre(), byId.getFibre()),
                () -> assertEquals(meal1.getFat(), byId.getFat()),
                () -> assertEquals(meal1.getProtein(), byId.getProtein()),
                () -> assertEquals(meal1.getProteinAndFatEquivalent(), byId.getProteinAndFatEquivalent()),
                () -> assertEquals(meal1.getCarbohydrateExchange(), byId.getCarbohydrateExchange()),
                () -> assertEquals(meal1.getProducts(), byId.getProducts()),
                () -> assertEquals(meal1.getUserId(), byId.getUserId()),
                () -> assertEquals(meal1.getKcal(), byId.getKcal())
        );

        verify(mealRepository, times(1)).findById(meal1.getId());
        verifyNoMoreInteractions(mealRepository);
    }

    @Test
    @DisplayName("When findById with id that doesn't exist, NotFoundException should be thrown")
    void findById_whenNotFoundMeal_thenThrowNotFoundException() {
        final var ID = "@#@#@ID";
        Mockito.when(mealRepository.findById(ID)).thenReturn(Mono.empty());

        assertThrows(NotFoundException.class, () -> mealService.findById(ID).block());

        verify(mealRepository, times(1)).findById(ID);
        verifyNoMoreInteractions(mealRepository);
    }

    @Test
    @DisplayName("When findDtoById, and found meal, then the meal should be returned")
    void findDtoById_whenSuccess_thenReturnMeal() {
        Mockito.when(mealRepository.findById(meal1.getId())).thenReturn(Mono.just(meal1));
        Mockito.when(mealDtoConverter.toDto(meal1)).thenReturn(meal1Dto);

        final var byId = mealService.findDtoById(meal1.getId()).block();

        assertNotNull(byId);
        assertAll(
                () -> assertEquals(meal1.getId(), byId.getId()),
                () -> assertEquals(meal1.getName(), byId.getName()),
                () -> assertEquals(meal1.getDescription(), byId.getDescription()),
                () -> assertEquals(meal1.getRecipe(), byId.getRecipe()),
                () -> assertEquals(meal1.getImageUrl(), byId.getImageUrl()),
                () -> assertEquals(meal1.getCarbohydrate(), byId.getCarbohydrate()),
                () -> assertEquals(meal1.getFibre(), byId.getFibre()),
                () -> assertEquals(meal1.getFat(), byId.getFat()),
                () -> assertEquals(meal1.getProtein(), byId.getProtein()),
                () -> assertEquals(meal1.getProteinAndFatEquivalent(), byId.getProteinAndFatEquivalent()),
                () -> assertEquals(meal1.getCarbohydrateExchange(), byId.getCarbohydrateExchange()),
                () -> assertEquals(productDtoConverter.listToDto(meal1.getProducts()), byId.getProducts()),
                () -> assertEquals(meal1.getUserId(), byId.getUserId()),
                () -> assertEquals(meal1.getKcal(), byId.getKcal())
        );

        verify(mealRepository, times(1)).findById(meal1.getId());
        verify(mealDtoConverter, times(1)).toDto(meal1);
        verifyNoMoreInteractions(mealRepository);
    }

    @Test
    @DisplayName("When findDtoById with id that doesn't exist, NotFoundException should be thrown")
    void findDtoById_whenNotFoundMeal_thenThrowNotFoundException() {
        final var ID = "@#@#@ID";
        Mockito.when(mealRepository.findById(ID)).thenReturn(Mono.empty());

        assertThrows(NotFoundException.class, () -> mealService.findDtoById(ID).block());

        verify(mealRepository, times(1)).findById(ID);
        verifyNoMoreInteractions(mealRepository);
    }

    @Test
    @DisplayName("When search meals and there are not meals in db, then return empty page")
    void searchByName_whenNoMealsAndNoResult_thenReturnEmptyPage() {
        final var page = 0;
        final var pageSize = 10;
        final var totalElements = 0;
        final var query = "name";
        final var mealList = createMealList(totalElements, COFFEE);
        final var productDtoList = createMealDtoList(totalElements, COFFEE);
        final var expected = new Page<>(productDtoList
                .stream()
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);


        Mockito.when(mealRepository.findAllByNameIgnoreCaseContaining(query)).thenReturn(Flux.fromIterable(mealList));
        Mockito.when(mealDtoConverter.toDto(coffeeWithId())).thenReturn(coffeeWithIdDto());


        final var firstPage = mealService.searchByName(PageRequest.of(page, pageSize), query).block();


        assertEquals(expected, firstPage);
        verify(mealRepository, times(1)).findAllByNameIgnoreCaseContaining(query);
        verify(mealDtoConverter, times(0)).toDto(coffeeWithId());
        verifyNoMoreInteractions(mealRepository, mealDtoConverter);
    }

    @Test
    @DisplayName("Search meals, when there are two meals in db, should be returned page with 2 meals")
    void searchByName_when2ProductsAnd2Results_thenReturnPageWithTwoMeals() {
        final var page = 0;
        final var pageSize = 10;
        final var totalElements = 2;
        final var query = coffeeWithId().getName();
        final var productList = createMealList(totalElements, COFFEE);
        final var productDtoList = createMealDtoList(totalElements, COFFEE);
        final var expected = new Page<>(productDtoList
                .stream()
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);


        Mockito.when(mealRepository.findAllByNameIgnoreCaseContaining(query)).thenReturn(Flux.fromIterable(productList));
        Mockito.when(mealDtoConverter.toDto(coffeeWithId())).thenReturn(coffeeWithIdDto());


        final var firstPage = mealService.searchByName(PageRequest.of(page, pageSize), query).block();


        assertEquals(expected, firstPage);
        verify(mealRepository, times(1)).findAllByNameIgnoreCaseContaining(query);
        verify(mealDtoConverter, times(2)).toDto(coffeeWithId());
        verifyNoMoreInteractions(mealRepository, mealDtoConverter);
    }

    @Test
    @DisplayName("Search meals, when there are twenty two meals in db but query matches for 12, then should be returned first page with ten meals")
    void searchByName_when22MealsAnd12Results_thenReturnPageWith10ResultsInFirstPage() {
        final var page = 0;
        final var pageSize = 10;
        final var totalElements = 22;
        final var query = coffeeWithId().getName();
        final var productList = createMealList(12, COFFEE);
        productList.addAll(createMealList(10, DUMPLINGS));
        final var productDtoList = createMealDtoList(12, COFFEE);
        productDtoList.addAll(createMealDtoList(10, DUMPLINGS));
        final var expected = new Page<>(productDtoList
                .stream()
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);


        Mockito.when(mealRepository.findAllByNameIgnoreCaseContaining(query)).thenReturn(Flux.fromIterable(productList));
        Mockito.when(mealDtoConverter.toDto(coffeeWithId())).thenReturn(coffeeWithIdDto());


        final var firstPage = mealService.searchByName(PageRequest.of(page, pageSize), query).block();


        assertEquals(expected, firstPage);
        verify(mealRepository, times(1)).findAllByNameIgnoreCaseContaining(query);
        verify(mealDtoConverter, times(pageSize)).toDto(coffeeWithId());
        verifyNoMoreInteractions(mealRepository, mealDtoConverter);
    }

    @Test
    @DisplayName("When findAllByUserId and user has 5 meals, then returned should be 5 meals")
    void findAllByUserId_whenUserHas5Meals_thenReturn5Meals() {
        final var mealList = createMealList(5, DUMPLINGS);
        final var user = UserSample.johnWithId();


        when(mealRepository.findAllByUserId(user.getId())).thenReturn(Flux.fromIterable(mealList));


        final var meals = mealService.findAllByUserId(user.getId()).toStream().collect(Collectors.toList());


        assertAll(
                () -> assertEquals(mealList, meals),
                () -> assertEquals(mealList.size(), meals.size())
        );
        verify(mealRepository, times(1)).findAllByUserId(user.getId());
        verifyNoMoreInteractions(mealRepository);
    }

    @Test
    @DisplayName("When findAllByUserId and user has no meals, then returned should be empty list")
    void findAllByUserId_whenUserHasNoMeals_thenReturnEmptyList() {
        final var mealList = new ArrayList<Meal>();
        final var user = UserSample.johnWithId();


        when(mealRepository.findAllByUserId(user.getId())).thenReturn(Flux.empty());


        final var allByUserId = mealService.findAllByUserId(user.getId()).toStream().collect(Collectors.toList());


        assertAll(
                () -> assertEquals(mealList, allByUserId),
                () -> assertEquals(mealList.size(), allByUserId.size())
        );
        verify(mealRepository, times(1)).findAllByUserId(user.getId());
        verifyNoMoreInteractions(mealRepository);
    }

    @Test
    @DisplayName("When findAllPageable and there are twenty meals, then should be returned first page with 10 meals")
    void findAllPageable_whenTotalElements20PageSize10Page0_thenReturnFirstPageWith10Meals() {
        final var page = 0;
        final var pageSize = 10;
        final var totalElements = 20;
        final var mealList = createMealList(totalElements, DUMPLINGS);
        final var mealDtoList = createMealDtoList(totalElements, DUMPLINGS);
        final var expected = new Page<>(mealDtoList
                .stream()
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);


        Mockito.when(mealRepository.findAll()).thenReturn(Flux.fromIterable(mealList));
        Mockito.when(mealDtoConverter.toDto(dumplingsWithId())).thenReturn(dumplingsWithIdDto());


        final var firstPage = mealService.findAllPageable(PageRequest.of(page, pageSize)).block();


        assertEquals(expected, firstPage);
        verify(mealRepository, times(1)).findAll();
        verify(mealDtoConverter, times(10)).toDto(dumplingsWithId());
        verifyNoMoreInteractions(mealRepository, mealDtoConverter);
    }

    @Test
    @DisplayName("When findAllPageable and there are twenty meals and page = 1, should be returned second page(because first = 0)")
    void findAllPageable_whenTotalElements20PageSize10Page1_thenReturnSecondPageWith10Products() {
        final var page = 1;
        final var pageSize = 10;
        final var totalElements = 20;
        final var mealList = createMealList(totalElements, DUMPLINGS);
        final var mealDtoList = createMealDtoList(totalElements, DUMPLINGS);
        final var expected = new Page<>(mealDtoList
                .stream()
                .skip(pageSize)
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);


        Mockito.when(mealRepository.findAll()).thenReturn(Flux.fromIterable(mealList));
        Mockito.when(mealDtoConverter.toDto(dumplingsWithId())).thenReturn(dumplingsWithIdDto());


        final var firstPage = mealService.findAllPageable(PageRequest.of(page, pageSize)).block();


        assertEquals(expected, firstPage);
        verify(mealRepository, times(1)).findAll();
        verify(mealDtoConverter, times(10)).toDto(dumplingsWithId());
        verifyNoMoreInteractions(mealRepository, mealDtoConverter);
    }

    @Test
    @DisplayName("When findAllPagaable and there are no meals in db, returned should be empty page")
    void findAllPageable_whenTotalElements0PageSize10Page0_thenReturnFirstPageWithEmptyList() {
        final var page = 0;
        final var pageSize = 10;
        final var totalElements = 0;
        final var mealLis = createMealList(totalElements, DUMPLINGS);
        final var meaLDtoList = createMealDtoList(totalElements, DUMPLINGS);
        final var expected = new Page<>(meaLDtoList
                .stream()
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);


        Mockito.when(mealRepository.findAll()).thenReturn(Flux.fromIterable(mealLis));


        final var firstPage = mealService.findAllPageable(PageRequest.of(page, pageSize)).block();


        assertEquals(expected, firstPage);
        verify(mealRepository, times(1)).findAll();
        verifyNoMoreInteractions(mealRepository, mealDtoConverter);
    }

    @Test
    @DisplayName("When save, then mealRespository.save() should be used and Mono<Meal> should be returned")
    void save() {
        Mockito.when(mealRepository.save(meal1)).thenReturn(Mono.just(meal1));

        assertEquals(meal1, mealService.save(meal1).block());

        verify(mealRepository, times(1)).save(meal1);
        verifyNoMoreInteractions(mealRepository);
    }

    @Test
    @DisplayName("When save as argument MealDto - then should be used mealRepository.save(), dtoConverter.fromDto() and element should be returned")
    void saveDto() {
        Mockito.when(mealRepository.save(meal1)).thenReturn(Mono.just(meal1));
        Mockito.when(mealDtoConverter.fromDto(meal1Dto)).thenReturn(meal1);

        assertEquals(meal1, mealService.save(meal1Dto).block());

        verify(mealRepository, times(1)).save(meal1);
        verify(mealDtoConverter, times(1)).fromDto(meal1Dto);
        verifyNoMoreInteractions(mealRepository, mealDtoConverter);
    }

    @Test
    @DisplayName("DeletedAll, Mono.empty() should be returned and mealRepository.deleteAll() should be used")
    void deleteAll() {
        assertEquals(Mono.empty().block(), mealService.deleteAll());

        verify(mealRepository, times(1)).deleteAll();
        verifyNoMoreInteractions(mealRepository);
    }

    @Test
    @DisplayName("When deleteById, then returned should be Mono.empty() and mealRepository.deleteById(id) should be used")
    void deleteById() {
        assertEquals(Mono.empty().block(), mealService.deleteById(meal1.getId()));

        verify(mealRepository, times(1)).deleteById(meal1.getId());
        verifyNoMoreInteractions(mealRepository);
    }

    @Test
    @DisplayName("Update meal, when empty list of products, then return meal with empty list")
    void updateMeal_whenEmptyListWithProducts_thenReturnMealWithEmptyList() {
        final var products = new ArrayList<Product>();
        final var productDtos = new ArrayList<ProductDto>();

        final var afterUpdate = dumplingsWithIdDto();
        afterUpdate.setProducts(productDtos);

        Mockito.when(mealRepository.findById(meal1.getId())).thenReturn(Mono.just(meal1));
        Mockito.when(mealRepository.save(meal1)).thenReturn(Mono.just(meal1));
        Mockito.when(mealDtoConverter.toDto(meal1)).thenReturn(meal1Dto);
        Mockito.when(productDtoConverter.listFromDto(productDtos)).thenReturn(products);


        final var mealWithProducts = mealService.updateMeal(meal1.getId(), afterUpdate).block();


        assertNotNull(mealWithProducts);
        assertAll(
                () -> assertEquals(0, mealWithProducts.getProducts().size()),
                () -> assertEquals(0, mealWithProducts.getProtein()),
                () -> assertEquals(0, mealWithProducts.getKcal()),
                () -> assertEquals(0, mealWithProducts.getFibre()),
                () -> assertEquals(0, mealWithProducts.getFat()),
                () -> assertEquals(0, mealWithProducts.getCarbohydrate()),
                () -> assertEquals(0, mealWithProducts.getCarbohydrateExchange()),
                () -> assertEquals(0, mealWithProducts.getProteinAndFatEquivalent()),
                () -> assertEquals(meal1.getUserId(), mealWithProducts.getUserId()),
                () -> assertEquals(meal1.getDescription(), mealWithProducts.getDescription()),
                () -> assertEquals(meal1.getRecipe(), mealWithProducts.getRecipe()),
                () -> assertEquals(meal1.getName(), mealWithProducts.getName()),
                () -> assertEquals(meal1.getImageUrl(), mealWithProducts.getImageUrl()),
                () -> assertEquals(meal1.getId(), mealWithProducts.getId())
        );
        verify(mealRepository, times(1)).findById(meal1.getId());
        verify(mealRepository, times(1)).save(meal1);
        verify(mealDtoConverter, times(1)).toDto(meal1);
        verify(productDtoConverter, times(1)).listFromDto(productDtos);
        verify(userValidation, times(1)).validateUserWithPrincipal(meal1.getUserId());
        verifyNoMoreInteractions(mealRepository, mealDtoConverter, productDtoConverter, userValidation);
    }

    @Test
    @DisplayName("Update meal, when update object has different fields, then update the meal")
    void updateMeal_whenSomeInformationAreChanged_thenReturnMealUpdated() {
        final var products = new ArrayList<Product>();
        final var productDtos = new ArrayList<ProductDto>();

        final var afterUpdate = dumplingsWithIdDto();
        afterUpdate.setName("Update name");
        afterUpdate.setDescription("Update description");
        afterUpdate.setRecipe("Update recipe");
        afterUpdate.setImageUrl("some updated image");
        afterUpdate.setProducts(productDtos);

        final var afterUpdateDto = dumplingsWithId();
        afterUpdateDto.setName("Update name");
        afterUpdateDto.setDescription("Update description");
        afterUpdateDto.setRecipe("Update recipe");
        afterUpdateDto.setImageUrl("some updated image");
        afterUpdateDto.setProducts(products);

        Mockito.when(mealRepository.findById(meal1.getId())).thenReturn(Mono.just(meal1));
        Mockito.when(mealRepository.save(meal1)).thenReturn(Mono.just(afterUpdateDto));
        Mockito.when(mealDtoConverter.toDto(afterUpdateDto)).thenReturn(afterUpdate);
        Mockito.when(productDtoConverter.listFromDto(productDtos)).thenReturn(products);


        final var mealWithProducts = mealService.updateMeal(meal1.getId(), afterUpdate).block();


        assertNotNull(mealWithProducts);
        assertAll(
                () -> assertEquals(0, mealWithProducts.getProducts().size()),
                () -> assertEquals(0, mealWithProducts.getProtein()),
                () -> assertEquals(0, mealWithProducts.getKcal()),
                () -> assertEquals(0, mealWithProducts.getFibre()),
                () -> assertEquals(0, mealWithProducts.getFat()),
                () -> assertEquals(0, mealWithProducts.getCarbohydrate()),
                () -> assertEquals(0, mealWithProducts.getCarbohydrateExchange()),
                () -> assertEquals(0, mealWithProducts.getProteinAndFatEquivalent()),
                () -> assertEquals(meal1.getUserId(), mealWithProducts.getUserId()),
                () -> assertEquals(meal1.getDescription(), mealWithProducts.getDescription()),
                () -> assertEquals(meal1.getRecipe(), mealWithProducts.getRecipe()),
                () -> assertEquals(meal1.getName(), mealWithProducts.getName()),
                () -> assertEquals(meal1.getImageUrl(), mealWithProducts.getImageUrl()),
                () -> assertEquals(meal1.getId(), mealWithProducts.getId())
        );
        verify(mealRepository, times(1)).findById(meal1.getId());
        verify(mealRepository, times(1)).save(meal1);
        verify(mealDtoConverter, times(1)).toDto(meal1);
        verify(productDtoConverter, times(1)).listFromDto(productDtos);
        verify(userValidation, times(1)).validateUserWithPrincipal(meal1.getUserId());
        verifyNoMoreInteractions(mealRepository, mealDtoConverter, productDtoConverter, userValidation);
    }

    @Test
    @DisplayName("Update meal, when the only change is 2 products, then return the meal calculated with 2 products")
    void updateMeal_whenOnlyListIsChanged_thenReturnMealWith2Products() {
        var products = new ArrayList<Product>(Arrays.asList(breadWithId(), bananaWithId()));
        var productDtos = new ArrayList<ProductDto>(Arrays.asList(breadWithIdDto(), bananaWithIdDto()));

        final var afterUpdate = dumplingsWithId();
        final var afterUpdateDto = dumplingsWithIdDto();

        afterUpdate.setProducts(products);
        afterUpdate.setProtein(productDtos.get(0).getProtein() + productDtos.get(1).getProtein());
        afterUpdate.setFat(productDtos.get(0).getFat() + productDtos.get(1).getFat());
        afterUpdate.setCarbohydrate(productDtos.get(0).getCarbohydrate() + productDtos.get(1).getCarbohydrate());
        afterUpdate.setFibre(productDtos.get(0).getFibre() + productDtos.get(1).getFibre());
        afterUpdate.setKcal(productDtos.get(0).getKcal() + productDtos.get(1).getKcal());
        afterUpdate.setProteinAndFatEquivalent(productDtos.get(0).getProteinAndFatEquivalent() + productDtos.get(1).getProteinAndFatEquivalent());
        afterUpdate.setCarbohydrateExchange(productDtos.get(0).getCarbohydrateExchange() + productDtos.get(1).getCarbohydrateExchange());

        afterUpdateDto.setProducts(productDtos);
        afterUpdateDto.setProtein(productDtos.get(0).getProtein() + productDtos.get(1).getProtein());
        afterUpdateDto.setFat(productDtos.get(0).getFat() + productDtos.get(1).getFat());
        afterUpdateDto.setCarbohydrate(productDtos.get(0).getCarbohydrate() + productDtos.get(1).getCarbohydrate());
        afterUpdateDto.setFibre(productDtos.get(0).getFibre() + productDtos.get(1).getFibre());
        afterUpdateDto.setKcal(productDtos.get(0).getKcal() + productDtos.get(1).getKcal());
        afterUpdateDto.setProteinAndFatEquivalent(productDtos.get(0).getProteinAndFatEquivalent() + productDtos.get(1).getProteinAndFatEquivalent());
        afterUpdateDto.setCarbohydrateExchange(productDtos.get(0).getCarbohydrateExchange() + productDtos.get(1).getCarbohydrateExchange());


        Mockito.when(mealRepository.findById(meal1.getId())).thenReturn(Mono.just(meal1));
        Mockito.when(mealRepository.save(afterUpdate)).thenReturn(Mono.just(afterUpdate));
        Mockito.when(mealDtoConverter.toDto(afterUpdate)).thenReturn(afterUpdateDto);
        Mockito.when(productDtoConverter.listFromDto(productDtos)).thenReturn(products);


        MealDto mealWithProducts = mealService.updateMeal(meal1.getId(), afterUpdateDto).block();


        assertNotNull(mealWithProducts);
        assertAll(
                () -> assertEquals(products.size(), mealWithProducts.getProducts().size()),
                () -> assertEquals(products.get(0).getProtein() + products.get(1).getProtein(), mealWithProducts.getProtein()),
                () -> assertEquals(products.get(0).getKcal() + products.get(1).getKcal(), mealWithProducts.getKcal()),
                () -> assertEquals(products.get(0).getFibre() + products.get(1).getFibre(), mealWithProducts.getFibre()),
                () -> assertEquals(products.get(0).getFat() + products.get(1).getFat(), mealWithProducts.getFat()),
                () -> assertEquals(products.get(0).getCarbohydrate() + products.get(1).getCarbohydrate(), mealWithProducts.getCarbohydrate()),
                () -> assertEquals(products.get(0).getCarbohydrateExchange() + products.get(1).getCarbohydrateExchange(),
                        mealWithProducts.getCarbohydrateExchange()),
                () -> assertEquals(products.get(0).getProteinAndFatEquivalent() + products.get(1).getProteinAndFatEquivalent(),
                        mealWithProducts.getProteinAndFatEquivalent()),
                () -> assertEquals(meal1.getUserId(), mealWithProducts.getUserId()),
                () -> assertEquals(meal1.getDescription(), mealWithProducts.getDescription()),
                () -> assertEquals(meal1.getRecipe(), mealWithProducts.getRecipe()),
                () -> assertEquals(meal1.getName(), mealWithProducts.getName()),
                () -> assertEquals(meal1.getImageUrl(), mealWithProducts.getImageUrl()),
                () -> assertEquals(meal1.getId(), mealWithProducts.getId())
                );
        verify(mealRepository, times(1)).findById(meal1.getId());
        verify(mealRepository, times(1)).save(meal1);
        verify(mealDtoConverter, times(1)).toDto(meal1);
        verify(productDtoConverter, times(1)).listFromDto(productDtos);
        verify(userValidation, times(1)).validateUserWithPrincipal(meal1.getUserId());
        verifyNoMoreInteractions(mealRepository, mealDtoConverter, productDtoConverter, userValidation);
    }

    private ArrayList<Meal> createMealList(int size, String meal) {
        var arrayList = new ArrayList<Meal>();

        switch (meal) {
            case DUMPLINGS:
                for (int i = 0; i < size; i++)
                    arrayList.add(dumplingsWithId());
                break;
            case COFFEE:
                for (int i = 0; i < size; i++)
                    arrayList.add(coffeeWithId());
                break;
        }

        return arrayList;
    }

    private ArrayList<MealDto> createMealDtoList(int size, String meal) {
        var arrayList = new ArrayList<MealDto>();

        switch (meal) {
            case DUMPLINGS:
                for (int i = 0; i < size; i++)
                    arrayList.add(dumplingsWithIdDto());
                break;
            case COFFEE:
                for (int i = 0; i < size; i++)
                    arrayList.add(coffeeWithIdDto());
                break;
        }

        return arrayList;
    }
}