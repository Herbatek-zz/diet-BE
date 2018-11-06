package com.piotrek.diet.meal;

import com.piotrek.diet.helpers.MealSample;
import com.piotrek.diet.helpers.Page;
import com.piotrek.diet.helpers.UserSample;
import com.piotrek.diet.helpers.exceptions.NotFoundException;
import com.piotrek.diet.product.ProductDtoConverter;
import com.piotrek.diet.user.UserValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.piotrek.diet.helpers.AssertEqualAllFields.assertMealFields;
import static com.piotrek.diet.helpers.MealSample.*;
import static com.piotrek.diet.helpers.ProductSample.*;
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

    @InjectMocks
    private MealService mealService;

    private Meal meal;
    private MealDto mealDto;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.initMocks(this);
        meal = dumplingsWithId();
        mealDto = dumplingsWithIdDto();
    }

    @Test
    @DisplayName("Find meal by id, when found, then return the meal")
    void findById_whenFound_thenReturn() {
        when(mealRepository.findById(meal.getId())).thenReturn(Mono.just(meal));

        final var mealById = mealService.findById(meal.getId()).block();

        assertMealFields(meal, mealById);
        verify(mealRepository, times(1)).findById(meal.getId());
        verifyNoMoreInteractions(mealRepository, mealDtoConverter, productDtoConverter, userValidation);
    }

    @Test
    @DisplayName("Find meal by id, when not found, then throw NotFoundException")
    void findById_whenNotFound_thenThrowNotFoundException() {
        final var WRONG_ID = UUID.randomUUID().toString();
        when(mealRepository.findById(WRONG_ID)).thenReturn(Mono.empty());

        assertThrows(NotFoundException.class, mealService.findById(WRONG_ID)::block);
        verify(mealRepository, times(1)).findById(WRONG_ID);
        verifyNoMoreInteractions(mealRepository, mealDtoConverter, productDtoConverter, userValidation);
    }

    @Test
    @DisplayName("Find meal dto by id, when found, then return")
    void findDtoById_whenFound_thenReturn() {
        when(mealRepository.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(mealDtoConverter.toDto(meal)).thenReturn(mealDto);

        final var mealDtoById = mealService.findDtoById(meal.getId()).block();

        assertMealFields(mealDto, mealDtoById);
        verify(mealRepository, times(1)).findById(meal.getId());
        verify(mealDtoConverter, times(1)).toDto(meal);
        verifyNoMoreInteractions(mealRepository, mealDtoConverter, productDtoConverter, userValidation);
    }

    @Test
    @DisplayName("Find meal dto by id, when not found, then throw NotFoundException")
    void findDtoById_whenNotFound_thenThrowNotFoundException() {
        final var INCORRECT_ID = UUID.randomUUID().toString();
        when(mealRepository.findById(INCORRECT_ID)).thenReturn(Mono.empty());

        assertThrows(NotFoundException.class, mealService.findDtoById(INCORRECT_ID)::block);
        verify(mealRepository, times(1)).findById(INCORRECT_ID);
        verifyNoMoreInteractions(mealRepository, mealDtoConverter, productDtoConverter, userValidation);
    }

    @Test
    @DisplayName("Search meals by name, when nothing found, then return empty page")
    void searchByName_whenNothingFound_thenReturnEmptyPage() {
        final var page = 0;
        final var pageSize = 10;
        final var totalElements = 0;
        final var query = "query";
        final var mealList = createMealList(totalElements, COFFEE);
        final var mealDtoList = createMealDtoList(totalElements, COFFEE);
        final var expected = new Page<>(mealDtoList
                .stream()
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);

        when(mealRepository.findAllByNameIgnoreCaseContaining(query)).thenReturn(Flux.fromIterable(mealList));

        final var actualPage = mealService.searchByName(PageRequest.of(page, pageSize), query).block();

        assertEquals(expected, actualPage);
        verify(mealRepository, times(1)).findAllByNameIgnoreCaseContaining(query);
        verifyNoMoreInteractions(mealRepository, mealDtoConverter, productDtoConverter, userValidation);
    }

    @Test
    @DisplayName("Search meals, when found 2 meals, return page with 2 meals")
    void searchByName_whenFoundTwoMeals_thenReturnPageWithTwoMeals() {
        final var page = 0;
        final var pageSize = 10;
        final var totalElements = 2;
        final var query = coffeeWithId().getName();
        final var mealList = createMealList(totalElements, COFFEE);
        final var mealDtoList = createMealDtoList(totalElements, COFFEE);
        final var expected = new Page<>(mealDtoList
                .stream()
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);

        when(mealRepository.findAllByNameIgnoreCaseContaining(query)).thenReturn(Flux.fromIterable(mealList));
        when(mealDtoConverter.toDto(coffeeWithId())).thenReturn(coffeeWithIdDto());

        final var actualPage = mealService.searchByName(PageRequest.of(page, pageSize), query).block();

        assertEquals(expected, actualPage);
        verify(mealRepository, times(1)).findAllByNameIgnoreCaseContaining(query);
        verify(mealDtoConverter, times(2)).toDto(coffeeWithId());
        verifyNoMoreInteractions(mealRepository, mealDtoConverter, productDtoConverter, userValidation);
    }

    @Test
    @DisplayName("Search meals, when there are twenty-two meals, but query matches for 12, then return first page with 10 meals")
    void searchByName_when22MealsAnd12ResultsAndFirstPage_thenReturnPageWith10ResultsInFirstPage() {
        final var page = 0;
        final var pageSize = 10;
        final var totalElementsMatchesToQuery = 12;
        final var query = coffeeWithId().getName();

        final var mealList = createMealList(totalElementsMatchesToQuery, COFFEE);
        mealList.addAll(createMealList(10, DUMPLINGS));

        final var mealDtoList = createMealDtoList(totalElementsMatchesToQuery, COFFEE);
        mealDtoList.addAll(createMealDtoList(10, DUMPLINGS));

        final var expected = new Page<>(mealDtoList
                .stream()
                .skip(page * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElementsMatchesToQuery);

        when(mealRepository.findAllByNameIgnoreCaseContaining(query)).thenReturn(Flux.fromIterable(mealList.subList(0, totalElementsMatchesToQuery)));
        when(mealDtoConverter.toDto(coffeeWithId())).thenReturn(coffeeWithIdDto());

        final var firstPage = mealService.searchByName(PageRequest.of(page, pageSize), query).block();

        assertEquals(expected, firstPage);
        verify(mealRepository, times(1)).findAllByNameIgnoreCaseContaining(query);
        verify(mealDtoConverter, times(pageSize)).toDto(coffeeWithId());
        verifyNoMoreInteractions(mealRepository, mealDtoConverter, productDtoConverter, userValidation);
    }

    @Test
    @DisplayName("Search meals, when there are twenty-two meals, but query matches for 12, then return second page with two meals")
    void searchByName_when22MealsAnd12ResultsAndSecondPage_thenReturnPageWithTwoResultsInSecondPage() {
        final var page = 1;
        final var pageSize = 10;
        final var totalElementsMatchesToQuery = 12;
        final var query = coffeeWithId().getName();

        final var mealList = createMealList(totalElementsMatchesToQuery, COFFEE);
        mealList.addAll(createMealList(10, DUMPLINGS));

        final var mealDtoList = createMealDtoList(totalElementsMatchesToQuery, COFFEE);
        mealDtoList.addAll(createMealDtoList(10, DUMPLINGS));

        final var expected = new Page<>(mealDtoList
                .stream()
                .skip(page * pageSize)
                .limit(totalElementsMatchesToQuery - page * pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElementsMatchesToQuery);

        when(mealRepository.findAllByNameIgnoreCaseContaining(query)).thenReturn(Flux.fromIterable(mealList.subList(0, totalElementsMatchesToQuery)));
        when(mealDtoConverter.toDto(coffeeWithId())).thenReturn(coffeeWithIdDto());

        final var firstPage = mealService.searchByName(PageRequest.of(page, pageSize), query).block();

        assertEquals(expected, firstPage);
        verify(mealRepository, times(1)).findAllByNameIgnoreCaseContaining(query);
        verify(mealDtoConverter, times(2)).toDto(coffeeWithId());
        verifyNoMoreInteractions(mealRepository, mealDtoConverter, productDtoConverter, userValidation);
    }

    @Test
    @DisplayName("Find all user meals, when user has 5 meals, then return 5 meals")
    void findAllByUserId_whenUserHas5Meals_thenReturn5Meals() {
        var page = 0;
        var pageSize = 10;
        var totalElements = 5;
        var mealList = createMealList(totalElements, DUMPLINGS);
        var mealDtoList = createMealDtoList(totalElements, DUMPLINGS);
        var expected = new Page<>(mealDtoList
                .stream()
                .skip(page * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);
        final var user = UserSample.johnWithId();

        when(mealRepository.findAllByUserId(user.getId())).thenReturn(Flux.fromIterable(mealList));
        when(mealDtoConverter.toDto(MealSample.dumplingsWithId())).thenReturn(MealSample.dumplingsWithIdDto());

        final var actualPage = mealService.findAllByUserId(user.getId(), PageRequest.of(page, pageSize)).block();

        assertNotNull(actualPage);
        assertEquals(expected, actualPage);
        verify(mealRepository, times(1)).findAllByUserId(user.getId());
        verify(mealDtoConverter, times(totalElements)).toDto(MealSample.dumplingsWithId());
        verifyNoMoreInteractions(mealRepository, mealDtoConverter, productDtoConverter, userValidation);
    }

    @Test
    @DisplayName("Find all user meals, when user has no meals, then return empty list")
    void findAllByUserId_whenUserHasNoMeals_thenReturnEmptyList() {
        var page = 0;
        var pageSize = 10;
        var totalElements = 0;
        var expected = new Page<>(new ArrayList<>()
                .stream()
                .skip(page * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);
        final var user = UserSample.johnWithId();

        when(mealRepository.findAllByUserId(user.getId())).thenReturn(Flux.empty());

        final var actualPage = mealService.findAllByUserId(user.getId(), PageRequest.of(page, pageSize)).block();

        assertNotNull(actualPage);
        assertEquals(expected, actualPage);
        verify(mealRepository, times(1)).findAllByUserId(user.getId());
        verifyNoMoreInteractions(mealRepository, mealDtoConverter, productDtoConverter, userValidation);
    }

    @Test
    @DisplayName("Find all meals pageable, when there are 20 meals and first page, then return first page with 10 meals")
    void findAllPageable_whenTotalElements20PageSize10Page0_thenReturnFirstPageWith10Meals() {
        final var page = 0;
        final var pageSize = 10;
        final var totalElements = 20;
        final var mealList = createMealList(totalElements, DUMPLINGS);
        final var mealDtoList = createMealDtoList(totalElements, DUMPLINGS);
        final var expected = new Page<>(mealDtoList
                .stream()
                .skip(page * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);

        when(mealRepository.findAll()).thenReturn(Flux.fromIterable(mealList));
        when(mealDtoConverter.toDto(dumplingsWithId())).thenReturn(dumplingsWithIdDto());

        final var actualFirstPage = mealService.findAllPageable(PageRequest.of(page, pageSize)).block();

        assertEquals(expected, actualFirstPage);
        verify(mealRepository, times(1)).findAll();
        verify(mealDtoConverter, times(10)).toDto(dumplingsWithId());
        verifyNoMoreInteractions(mealRepository, mealDtoConverter, productDtoConverter, userValidation);
    }

    @Test
    @DisplayName("Find all meals pageable, when there is 20 meals and second page, then return second page with 10 meals")
    void findAllPageable_whenTotalElements20PageSize10Page1_thenReturnSecondPageWith10Products() {
        final var page = 1;
        final var pageSize = 10;
        final var totalElements = 20;
        final var mealList = createMealList(totalElements, DUMPLINGS);
        final var mealDtoList = createMealDtoList(totalElements, DUMPLINGS);
        final var expected = new Page<>(mealDtoList
                .stream()
                .skip(pageSize * page)
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);

        when(mealRepository.findAll()).thenReturn(Flux.fromIterable(mealList));
        when(mealDtoConverter.toDto(dumplingsWithId())).thenReturn(dumplingsWithIdDto());

        final var actualSecondPage = mealService.findAllPageable(PageRequest.of(page, pageSize)).block();

        assertEquals(expected, actualSecondPage);
        verify(mealRepository, times(1)).findAll();
        verify(mealDtoConverter, times(10)).toDto(dumplingsWithId());
        verifyNoMoreInteractions(mealRepository, mealDtoConverter, productDtoConverter, userValidation);
    }

    @Test
    @DisplayName("Find all meals pageable, when no meals, then return empty page")
    void findAllPageable_whenTotalElements0PageSize10Page0_thenReturnFirstPageWithEmptyList() {
        final var page = 0;
        final var pageSize = 10;
        final var totalElements = 0;
        final var mealLis = createMealList(totalElements, DUMPLINGS);
        final var meaLDtoList = createMealDtoList(totalElements, DUMPLINGS);
        final var expected = new Page<>(meaLDtoList
                .stream()
                .skip(page * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);

        when(mealRepository.findAll()).thenReturn(Flux.fromIterable(mealLis));

        final var firstPage = mealService.findAllPageable(PageRequest.of(page, pageSize)).block();

        assertEquals(expected, firstPage);
        verify(mealRepository, times(1)).findAll();
        verifyNoMoreInteractions(mealRepository, mealDtoConverter, productDtoConverter, userValidation);
    }

    @Test
    @DisplayName("Save meal, then save and return the meal")
    void save() {
        when(mealRepository.save(meal)).thenReturn(Mono.just(meal));

        final Meal savedMeal = mealService.save(meal).block();

        assertMealFields(meal, savedMeal);
        verify(mealRepository, times(1)).save(meal);
        verifyNoMoreInteractions(mealRepository, mealDtoConverter, productDtoConverter, userValidation);
    }

    @Test
    @DisplayName("Save a meal dto, then save and return the meal")
    void saveDto() {
        when(mealRepository.save(meal)).thenReturn(Mono.just(meal));
        when(mealDtoConverter.fromDto(mealDto)).thenReturn(meal);

        final Meal savedMeal = mealService.save(mealDto).block();

        assertMealFields(meal, savedMeal);
        verify(mealRepository, times(1)).save(meal);
        verify(mealDtoConverter, times(1)).fromDto(mealDto);
        verifyNoMoreInteractions(mealRepository, mealDtoConverter, productDtoConverter, userValidation);
    }

    @Test
    @DisplayName("Deleted all meals, then return Mono.empty()")
    void deleteAll() {
        assertEquals(Mono.empty().block(), mealService.deleteAll());

        verify(mealRepository, times(1)).deleteAll();
        verifyNoMoreInteractions(mealRepository, mealDtoConverter, productDtoConverter, userValidation);
    }

    @Test
    @DisplayName("Delete meal by id, then Mono.empty()")
    void deleteById() {
        when(mealRepository.findById(meal.getId())).thenReturn(Mono.just(meal));

        assertEquals(Mono.empty().block(), mealService.deleteById(meal.getId()));
        verify(userValidation, times(1)).validateUserWithPrincipal(meal.getUserId());
        verify(mealRepository, times(1)).deleteById(meal.getId());
        verify(mealRepository, times(1)).findById(meal.getId());
        verifyNoMoreInteractions(mealRepository, mealDtoConverter, productDtoConverter, userValidation);
    }

    @Test
    @DisplayName("Update meal, when empty list of products, then return meal with empty list")
    void updateMeal_whenEmptyListWithProducts_thenReturnMealWithEmptyList() {
        final var expectedMeal = dumplingsWithIdDto();
        expectedMeal.setProducts(new ArrayList<>());

        when(mealRepository.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(mealRepository.save(meal)).thenReturn(Mono.just(meal));
        when(mealDtoConverter.toDto(meal)).thenReturn(expectedMeal);
        when(productDtoConverter.listFromDto(new ArrayList<>())).thenReturn(new ArrayList<>());

        final var afterUpdate = mealService.updateMeal(meal.getId(), expectedMeal).block();

        assertMealFields(expectedMeal, afterUpdate);
        verify(mealRepository, times(1)).findById(meal.getId());
        verify(mealRepository, times(1)).save(meal);
        verify(mealDtoConverter, times(1)).toDto(meal);
        verify(productDtoConverter, times(1)).listFromDto(new ArrayList<>());
        verify(userValidation, times(1)).validateUserWithPrincipal(meal.getUserId());
        verifyNoMoreInteractions(mealRepository, mealDtoConverter, productDtoConverter, userValidation);
    }

    @Test
    @DisplayName("Update meal, when update object has different fields, then update the meal")
    void updateMeal_whenSomeInformationAreChanged_thenReturnMealUpdated() {
        final var expectedDto = dumplingsWithIdDto();
        expectedDto.setName("Update name");
        expectedDto.setDescription("Update description");
        expectedDto.setRecipe("Update recipe");
        expectedDto.setImageUrl("some updated image");
        expectedDto.setProducts(new ArrayList<>());

        final var expected = dumplingsWithId();
        expected.setName("Update name");
        expected.setDescription("Update description");
        expected.setRecipe("Update recipe");
        expected.setImageUrl("some updated image");
        expected.setProducts(new ArrayList<>());

        when(mealRepository.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(mealRepository.save(meal)).thenReturn(Mono.just(expected));
        when(mealDtoConverter.toDto(expected)).thenReturn(expectedDto);
        when(productDtoConverter.listFromDto(new ArrayList<>())).thenReturn(new ArrayList<>());

        final var actualMeal = mealService.updateMeal(meal.getId(), expectedDto).block();

        assertMealFields(expectedDto, actualMeal);
        verify(mealRepository, times(1)).findById(meal.getId());
        verify(mealRepository, times(1)).save(meal);
        verify(mealDtoConverter, times(1)).toDto(meal);
        verify(productDtoConverter, times(1)).listFromDto(new ArrayList<>());
        verify(userValidation, times(1)).validateUserWithPrincipal(meal.getUserId());
        verifyNoMoreInteractions(mealRepository, mealDtoConverter, productDtoConverter, userValidation);
    }

    @Test
    @DisplayName("Update meal, when the only change is 2 products added, then return the meal calculated with 2 products")
    void updateMeal_whenOnlyListIsChanged_thenReturnMealWith2Products() {
        var products = new ArrayList<>(Arrays.asList(breadWithId(), bananaWithId()));
        var productDtos = new ArrayList<>(Arrays.asList(breadWithIdDto(), bananaWithIdDto()));

        final var expected = dumplingsWithId();
        final var expectedDto = dumplingsWithIdDto();

        expected.setProducts(products);
        expected.setProtein(productDtos.get(0).getProtein() + productDtos.get(1).getProtein());
        expected.setFat(productDtos.get(0).getFat() + productDtos.get(1).getFat());
        expected.setCarbohydrate(productDtos.get(0).getCarbohydrate() + productDtos.get(1).getCarbohydrate());
        expected.setFibre(productDtos.get(0).getFibre() + productDtos.get(1).getFibre());
        expected.setKcal(productDtos.get(0).getKcal() + productDtos.get(1).getKcal());
        expected.setProteinAndFatEquivalent(productDtos.get(0).getProteinAndFatEquivalent() + productDtos.get(1).getProteinAndFatEquivalent());
        expected.setCarbohydrateExchange(productDtos.get(0).getCarbohydrateExchange() + productDtos.get(1).getCarbohydrateExchange());

        expectedDto.setProducts(productDtos);
        expectedDto.setProtein(productDtos.get(0).getProtein() + productDtos.get(1).getProtein());
        expectedDto.setFat(productDtos.get(0).getFat() + productDtos.get(1).getFat());
        expectedDto.setCarbohydrate(productDtos.get(0).getCarbohydrate() + productDtos.get(1).getCarbohydrate());
        expectedDto.setFibre(productDtos.get(0).getFibre() + productDtos.get(1).getFibre());
        expectedDto.setKcal(productDtos.get(0).getKcal() + productDtos.get(1).getKcal());
        expectedDto.setProteinAndFatEquivalent(productDtos.get(0).getProteinAndFatEquivalent() + productDtos.get(1).getProteinAndFatEquivalent());
        expectedDto.setCarbohydrateExchange(productDtos.get(0).getCarbohydrateExchange() + productDtos.get(1).getCarbohydrateExchange());

        when(mealRepository.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(mealRepository.save(expected)).thenReturn(Mono.just(expected));
        when(mealDtoConverter.toDto(expected)).thenReturn(expectedDto);
        when(productDtoConverter.listFromDto(productDtos)).thenReturn(products);

        MealDto actual = mealService.updateMeal(meal.getId(), expectedDto).block();

        assertMealFields(expectedDto, actual);
        verify(mealRepository, times(1)).findById(meal.getId());
        verify(mealRepository, times(1)).save(meal);
        verify(mealDtoConverter, times(1)).toDto(meal);
        verify(productDtoConverter, times(1)).listFromDto(productDtos);
        verify(userValidation, times(1)).validateUserWithPrincipal(meal.getUserId());
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