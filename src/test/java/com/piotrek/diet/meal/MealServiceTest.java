package com.piotrek.diet.meal;

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

import static com.piotrek.diet.sample.MealSample.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MealServiceTest {

    @Mock
    private MealRepository mealRepository;

    @Mock
    private MealDtoConverter mealDtoConverter;

    private MealService mealService;

    private Meal meal;

    @BeforeEach
    void beforeEach() {
        meal = dumplingsWithId();
        MockitoAnnotations.initMocks(this);
        mealService = new MealService(mealRepository, mealDtoConverter);
    }

    @Test
    void findById_whenSuccess_thenReturnMeal() {
        Mockito.when(mealRepository.findById(meal.getId())).thenReturn(Mono.just(meal));

        Meal byId = mealService.findById(meal.getId()).block();

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
    void findById_whenNotFoundMeal_thenThrowNotFoundException() {
        var id = "@#@#@ID";
        Mockito.when(mealRepository.findById(id)).thenReturn(Mono.empty());

        assertThrows(NotFoundException.class, () -> mealService.findById(id).block());

        verify(mealRepository, times(1)).findById(id);
        verifyNoMoreInteractions(mealRepository);
    }

    @Test
    void searchByName_whenNoMealsAndNoResult_thenReturnEmptyPagable() {
        var page = 0;
        var pageSize = 10;
        var totalElements = 0;
        var query = "name";
        var mealList = createMealList(totalElements, COFFEE);
        var productDtoList = createMealDtoList(totalElements, COFFEE);
        var expected = new Page<>(productDtoList
                .stream()
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);

        Mockito.when(mealRepository.findAllByNameIgnoreCaseContaining(query)).thenReturn(Flux.fromIterable(mealList));
        Mockito.when(mealDtoConverter.toDto(coffeeWithId())).thenReturn(coffeeWithIdDto());

        var firstPage = mealService.searchByName(PageRequest.of(page, pageSize), query).block();

        assertEquals(expected, firstPage);

        verify(mealRepository, times(1)).findAllByNameIgnoreCaseContaining(query);
        verify(mealDtoConverter, times(0)).toDto(coffeeWithId());
        verifyNoMoreInteractions(mealRepository);
        verifyNoMoreInteractions(mealDtoConverter);
    }

    @Test
    void searchByName_when2ProductsAnd2Results_thenReturnPagableWith2Results() {
        var page = 0;
        var pageSize = 10;
        var totalElements = 2;
        var query = coffeeWithId().getName();
        var productList = createMealList(totalElements, COFFEE);
        var productDtoList = createMealDtoList(totalElements, COFFEE);
        var expected = new Page<>(productDtoList
                .stream()
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);

        Mockito.when(mealRepository.findAllByNameIgnoreCaseContaining(query)).thenReturn(Flux.fromIterable(productList));
        Mockito.when(mealDtoConverter.toDto(coffeeWithId())).thenReturn(coffeeWithIdDto());

        var firstPage = mealService.searchByName(PageRequest.of(page, pageSize), query).block();

        assertEquals(expected, firstPage);

        verify(mealRepository, times(1)).findAllByNameIgnoreCaseContaining(query);
        verify(mealDtoConverter, times(2)).toDto(coffeeWithId());
        verifyNoMoreInteractions(mealRepository);
        verifyNoMoreInteractions(mealDtoConverter);

    }

    @Test
    void searchByName_when22MealsAnd12Results_thenReturnPagableWith10ResultsInFirstPage() {
        var page = 0;
        var pageSize = 10;
        var totalElements = 22;
        var query = coffeeWithId().getName();
        var productList = createMealList(12, COFFEE);
        productList.addAll(createMealList(10, DUMPLINGS));
        var productDtoList = createMealDtoList(12, COFFEE);
        productDtoList.addAll(createMealDtoList(10, DUMPLINGS));
        var expected = new Page<>(productDtoList
                .stream()
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);

        Mockito.when(mealRepository.findAllByNameIgnoreCaseContaining(query)).thenReturn(Flux.fromIterable(productList));
        Mockito.when(mealDtoConverter.toDto(coffeeWithId())).thenReturn(coffeeWithIdDto());

        var firstPage = mealService.searchByName(PageRequest.of(page, pageSize), query).block();

        assertEquals(expected, firstPage);

        verify(mealRepository, times(1)).findAllByNameIgnoreCaseContaining(query);
        verify(mealDtoConverter, times(pageSize)).toDto(coffeeWithId());
        verifyNoMoreInteractions(mealRepository);
        verifyNoMoreInteractions(mealDtoConverter);
    }

    @Test
    void findAllByUserId_whenUserHas5Meals_thenReturn5Meals() {
        var mealList = createMealList(5, DUMPLINGS);
        var user = UserSample.johnWithId();

        when(mealRepository.findAllByUserId(user.getId())).thenReturn(Flux.fromIterable(mealList));

        mealService.findAllByUserId(user.getId()).toStream().collect(Collectors.toList());

        verify(mealRepository, times(1)).findAllByUserId(user.getId());
        verifyNoMoreInteractions(mealRepository);
    }

    @Test
    void findAllByUserId_whenTotalProducts1AndUserHas0_thenReturnEmptyList() {
        var mealList = new ArrayList<Meal>();
        var user = UserSample.johnWithId();

        when(mealRepository.findAllByUserId(user.getId())).thenReturn(Flux.empty());

        var allByUserId = mealService.findAllByUserId(user.getId()).toStream().collect(Collectors.toList());

        assertAll(
                () -> assertEquals(mealList, allByUserId),
                () -> assertEquals(mealList.size(), allByUserId.size())
        );

        verify(mealRepository, times(1)).findAllByUserId(user.getId());
        verifyNoMoreInteractions(mealRepository);
    }

    @Test
    void findAllPageable_whenTotalElements20PageSize10Page0_thenReturnFirstPageWith10Meals() {
        var page = 0;
        var pageSize = 10;
        var totalElements = 20;
        var mealList = createMealList(totalElements, DUMPLINGS);
        var mealDtoList = createMealDtoList(totalElements, DUMPLINGS);
        var expected = new Page<>(mealDtoList
                .stream()
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);

        Mockito.when(mealRepository.findAll()).thenReturn(Flux.fromIterable(mealList));
        Mockito.when(mealDtoConverter.toDto(dumplingsWithId())).thenReturn(dumplingsWithIdDto());

        var firstPage = mealService.findAllPageable(PageRequest.of(page, pageSize)).block();

        assertEquals(expected, firstPage);

        verify(mealRepository, times(1)).findAll();
        verify(mealDtoConverter, times(10)).toDto(dumplingsWithId());
        verifyNoMoreInteractions(mealRepository);
        verifyNoMoreInteractions(mealDtoConverter);
    }

    @Test
    void findAllPageable_whenTotalElements20PageSize10Page1_thenReturnSecondPageWith10Products() {
        var page = 1;
        var pageSize = 10;
        var totalElements = 20;
        var mealList = createMealList(totalElements, DUMPLINGS);
        var mealDtoList = createMealDtoList(totalElements, DUMPLINGS);
        var expected = new Page<>(mealDtoList
                .stream()
                .skip(pageSize)
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);

        Mockito.when(mealRepository.findAll()).thenReturn(Flux.fromIterable(mealList));
        Mockito.when(mealDtoConverter.toDto(dumplingsWithId())).thenReturn(dumplingsWithIdDto());

        var firstPage = mealService.findAllPageable(PageRequest.of(page, pageSize)).block();

        assertEquals(expected, firstPage);

        verify(mealRepository, times(1)).findAll();
        verify(mealDtoConverter, times(10)).toDto(dumplingsWithId());
        verifyNoMoreInteractions(mealRepository);
        verifyNoMoreInteractions(mealDtoConverter);
    }

    @Test
    void findAllPageable_whenTotalElements0PageSize10Page0_thenReturnFirstPageWithEmptyList() {
        var page = 0;
        var pageSize = 10;
        var totalElements = 0;
        var mealLis = createMealList(totalElements, DUMPLINGS);
        var meaLDtoList = createMealDtoList(totalElements, DUMPLINGS);
        var expected = new Page<>(meaLDtoList
                .stream()
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);

        Mockito.when(mealRepository.findAll()).thenReturn(Flux.fromIterable(mealLis));

        var secondPage = mealService.findAllPageable(PageRequest.of(page, pageSize)).block();

        assertEquals(expected, secondPage);

        verify(mealRepository, times(1)).findAll();
        verifyNoMoreInteractions(mealRepository);
        verifyNoMoreInteractions(mealDtoConverter);
    }

    @Test
    void save() {
        Mockito.when(mealRepository.save(meal)).thenReturn(Mono.just(meal));

        assertEquals(meal, mealService.save(meal).block());

        verify(mealRepository, times(1)).save(meal);
        verifyNoMoreInteractions(mealRepository);
    }

    @Test
    void deleteAll() {
        assertEquals(Mono.empty().block(), mealService.deleteAll());

        verify(mealRepository, times(1)).deleteAll();
        verifyNoMoreInteractions(mealRepository);
    }

    @Test
    void deleteById() {
        assertEquals(Mono.empty().block(), mealService.deleteById(meal.getId()));

        verify(mealRepository, times(1)).deleteById(meal.getId());
        verifyNoMoreInteractions(mealRepository);
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