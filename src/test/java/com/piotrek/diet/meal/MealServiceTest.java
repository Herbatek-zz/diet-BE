package com.piotrek.diet.meal;

import com.piotrek.diet.helpers.Page;
import com.piotrek.diet.helpers.exceptions.NotFoundException;
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
import java.util.stream.Collectors;

import static com.piotrek.diet.sample.MealSample.*;
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

    private Meal meal;
    private MealDto mealDto;

    @BeforeEach
    void beforeEach() {
        meal = dumplingsWithId();
        mealDto = dumplingsWithIdDto();
        MockitoAnnotations.initMocks(this);
        mealService = new MealService(mealRepository, mealDtoConverter, productDtoConverter, userValidation);
    }

    @Test
    @DisplayName("When findById and found meal, then the meal should be returned")
    void findById_whenSuccess_thenReturnMeal() {
        Mockito.when(mealRepository.findById(meal.getId())).thenReturn(Mono.just(meal));

        final var byId = mealService.findById(meal.getId()).block();

        assertNotNull(byId);
        assertAll(
                () -> assertEquals(meal.getId(), byId.getId()),
                () -> assertEquals(meal.getName(), byId.getName()),
                () -> assertEquals(meal.getDescription(), byId.getDescription()),
                () -> assertEquals(meal.getRecipe(), byId.getRecipe()),
                () -> assertEquals(meal.getImageUrl(), byId.getImageUrl()),
                () -> assertEquals(meal.getCarbohydrate(), byId.getCarbohydrate()),
                () -> assertEquals(meal.getFibre(), byId.getFibre()),
                () -> assertEquals(meal.getFat(), byId.getFat()),
                () -> assertEquals(meal.getProtein(), byId.getProtein()),
                () -> assertEquals(meal.getProteinAndFatEquivalent(), byId.getProteinAndFatEquivalent()),
                () -> assertEquals(meal.getCarbohydrateExchange(), byId.getCarbohydrateExchange()),
                () -> assertEquals(meal.getProducts(), byId.getProducts()),
                () -> assertEquals(meal.getUserId(), byId.getUserId()),
                () -> assertEquals(meal.getKcal(), byId.getKcal())
        );

        verify(mealRepository, times(1)).findById(meal.getId());
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
        Mockito.when(mealRepository.findById(meal.getId())).thenReturn(Mono.just(meal));
        Mockito.when(mealDtoConverter.toDto(meal)).thenReturn(mealDto);

        final var byId = mealService.findDtoById(meal.getId()).block();

        assertNotNull(byId);
        assertAll(
                () -> assertEquals(meal.getId(), byId.getId()),
                () -> assertEquals(meal.getName(), byId.getName()),
                () -> assertEquals(meal.getDescription(), byId.getDescription()),
                () -> assertEquals(meal.getRecipe(), byId.getRecipe()),
                () -> assertEquals(meal.getImageUrl(), byId.getImageUrl()),
                () -> assertEquals(meal.getCarbohydrate(), byId.getCarbohydrate()),
                () -> assertEquals(meal.getFibre(), byId.getFibre()),
                () -> assertEquals(meal.getFat(), byId.getFat()),
                () -> assertEquals(meal.getProtein(), byId.getProtein()),
                () -> assertEquals(meal.getProteinAndFatEquivalent(), byId.getProteinAndFatEquivalent()),
                () -> assertEquals(meal.getCarbohydrateExchange(), byId.getCarbohydrateExchange()),
                () -> assertEquals(productDtoConverter.listToDto(meal.getProducts()), byId.getProducts()),
                () -> assertEquals(meal.getUserId(), byId.getUserId()),
                () -> assertEquals(meal.getKcal(), byId.getKcal())
        );

        verify(mealRepository, times(1)).findById(meal.getId());
        verify(mealDtoConverter, times(1)).toDto(meal);
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
        Mockito.when(mealRepository.save(meal)).thenReturn(Mono.just(meal));

        assertEquals(meal, mealService.save(meal).block());

        verify(mealRepository, times(1)).save(meal);
        verifyNoMoreInteractions(mealRepository);
    }

    @Test
    @DisplayName("When save as argument MealDto - then should be used mealRepository.save(), dtoConverter.fromDto() and element should be returned")
    void saveDto() {
        Mockito.when(mealRepository.save(meal)).thenReturn(Mono.just(meal));
        Mockito.when(mealDtoConverter.fromDto(mealDto)).thenReturn(meal);

        assertEquals(meal, mealService.save(mealDto).block());

        verify(mealRepository, times(1)).save(meal);
        verify(mealDtoConverter, times(1)).fromDto(mealDto);
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
        assertEquals(Mono.empty().block(), mealService.deleteById(meal.getId()));

        verify(mealRepository, times(1)).deleteById(meal.getId());
        verifyNoMoreInteractions(mealRepository);
    }

    @Test
    @DisplayName("")
    void addProductsToMeal() {

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