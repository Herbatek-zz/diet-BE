package com.piotrek.diet.meal;

import com.piotrek.diet.helpers.PageSupport;
import com.piotrek.diet.helpers.exceptions.BadRequestException;
import com.piotrek.diet.sample.UserSample;
import com.piotrek.diet.user.User;
import com.piotrek.diet.user.UserService;
import com.piotrek.diet.user.UserValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static com.piotrek.diet.sample.MealSample.dumplingsWithId;
import static com.piotrek.diet.sample.MealSample.dumplingsWithIdDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class MealFacadeTest {

    @Mock
    private UserService userService;

    @Mock
    private MealService mealService;

    @Mock
    private MealDtoConverter mealDtoConverter;

    @Mock
    private UserValidation userValidation;

    @InjectMocks
    private MealFacade mealFacade;

    private MealDto mealDto;
    private Meal meal;
    private User user;

    @BeforeEach
    void setup() {
        createMeals();
        user = UserSample.johnWithId();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void createMeal_whenPrincipalEqualUserId_thenSuccess() {

        Mockito.when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        Mockito.when(mealService.save(meal)).thenReturn(Mono.just(meal));
        Mockito.when(mealDtoConverter.fromDto(mealDto)).thenReturn(meal);
        Mockito.when(mealDtoConverter.toDto(meal)).thenReturn(mealDto);

        var created = mealFacade.createMeal(user.getId(), mealDto).block();

        assertEquals(mealDto, created);
    }

    @Test
    void createMeal_whenPrincipalNotEqualUserId_thenFailure() {

        Mockito.when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        Mockito.when(mealService.save(meal)).thenReturn(Mono.just(meal));
        Mockito.doThrow(BadRequestException.class).when(userValidation).validateUserWithPrincipal(user.getId());

        assertThrows(BadRequestException.class, () -> mealFacade.createMeal(user.getId(), mealDto).block());
    }

    @Test
    void createMeal_whenUserDoesNotExist_thenThrowNotFoundException() {
        Mockito.doThrow(BadRequestException.class).when(userService).findById(user.getId());

        assertThrows(BadRequestException.class, () -> mealFacade.createMeal(user.getId(), mealDto).block());
    }

    @Test
    void findAllByUserId_whenUserHas20MealsPage0PageSize10_thenReturnFirstPage() {
        var page = 0;
        var pageSize = 10;
        var totalElements = 20;
        var mealsList = createMealsList(totalElements);
        var mealDtoList = createMealDtoList(totalElements);
        var expected = new PageSupport<>(mealDtoList
                .stream()
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);

        Mockito.when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        Mockito.when(mealDtoConverter.toDto(meal)).thenReturn(mealDto);
        Mockito.when(mealService.findAllByUserId(user.getId())).thenReturn(Flux.fromIterable(mealsList));

        var firstPage = mealFacade.findAllByUserId(user.getId(), PageRequest.of(page, pageSize)).block();

        assertEquals(expected, firstPage);

        verify(userService, times(1)).findById(user.getId());
        verify(mealService, times(1)).findAllByUserId(user.getId());
        verify(mealDtoConverter, times(10)).toDto(meal);
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(mealService);
        verifyNoMoreInteractions(mealDtoConverter);
    }

    @Test
    void findAllByUserId_whenUserHasNoMealsPage0PageSize10_thenReturnFirstPageWithEmptyList() {
        var page = 0;
        var pageSize = 10;
        var totalElements = 0;
        var mealList = new ArrayList<Meal>();
        var mealDtos = new ArrayList<MealDto>();
        var expected = new PageSupport<>(mealDtos
                .stream()
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);

        Mockito.when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        Mockito.when(mealDtoConverter.toDto(meal)).thenReturn(mealDto);
        Mockito.when(mealService.findAllByUserId(user.getId())).thenReturn(Flux.fromIterable(mealList));

        var firstPage = mealFacade.findAllByUserId(user.getId(), PageRequest.of(page, pageSize)).block();

        assertEquals(expected, firstPage);

        verify(userService, times(1)).findById(user.getId());
        verify(mealService, times(1)).findAllByUserId(user.getId());
        verify(mealDtoConverter, times(0)).toDto(meal);
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(mealService);
        verifyNoMoreInteractions(mealDtoConverter);
    }

    @Test
    void findAllByUserId_whenUserDoesNotExist_thenThrowNotFoundException() {
        Mockito.doThrow(BadRequestException.class).when(userService).findById(user.getId());

        assertThrows(BadRequestException.class,
                () -> mealFacade.findAllByUserId(user.getId(), PageRequest.of(1, 10)).block());
    }

    private void createMeals() {
        mealDto = dumplingsWithIdDto();
        meal = dumplingsWithId();
    }

    private ArrayList<Meal> createMealsList(int size) {
        var arrayList = new ArrayList<Meal>();

        for (int i = 0; i < size; i++)
            arrayList.add(dumplingsWithId());

        return arrayList;
    }

    private ArrayList<MealDto> createMealDtoList(int size) {
        var arrayList = new ArrayList<MealDto>();

        for (int i = 0; i < size; i++)
            arrayList.add(dumplingsWithIdDto());

        return arrayList;
    }
}