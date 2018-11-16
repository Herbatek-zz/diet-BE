package com.piotrek.diet.cart;

import com.piotrek.diet.helpers.CartSample;
import com.piotrek.diet.helpers.MealSample;
import com.piotrek.diet.helpers.ProductSample;
import com.piotrek.diet.meal.Meal;
import com.piotrek.diet.meal.MealDto;
import com.piotrek.diet.meal.MealDtoConverter;
import com.piotrek.diet.product.Product;
import com.piotrek.diet.product.ProductDto;
import com.piotrek.diet.product.ProductDtoConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static com.piotrek.diet.helpers.AssertEqualAllFields.assertCartFields;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class CartDtoConverterTest {

    @Mock
    private ProductDtoConverter productDtoConverter;

    @Mock
    private MealDtoConverter mealDtoConverter;

    @Mock
    private CartCalculator cartCalculator;

    @InjectMocks
    private CartDtoConverter cartDtoConverter;

    private Cart cart;
    private CartDto cartDto;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.initMocks(this);
        cart = CartSample.cart1();
        cartDto = CartSample.cartDto1();
    }

    @Test
    void toDto_emptyCart() {
        final var converted = cartDtoConverter.toDto(cart);

        assertCartFields(cartDto, converted);
        verify(productDtoConverter, times(1)).listToDto(cart.getProducts());
        verify(mealDtoConverter, times(1)).listToDto(cart.getMeals());
        verify(cartCalculator, times(1)).calculateCartInfo(cartDto);
        verify(cartCalculator, times(1)).sumProductsAndProductsFromMeals(new ArrayList<>(), new ArrayList<>());
        verify(cartCalculator, times(1)).retrieveProductsFromMeals(new ArrayList<>());
        verifyNoMoreInteractions(productDtoConverter, mealDtoConverter, cartCalculator);
    }

    @Test
    void toDto_withProducts() {
        cart.getProducts().add(ProductSample.bananaWithId());
        cartDto.getProducts().add(ProductSample.bananaWithIdDto());
        cartDto.getAllProducts().add(ProductSample.bananaWithIdDto());
        cartDto.setItemCounter(cartDto.getItemCounter() + 1);

        final var converted = cartDtoConverter.toDto(cart);

        assertCartFields(cartDto, converted);
        verify(productDtoConverter, times(1)).listToDto(cart.getProducts());
        verify(mealDtoConverter, times(1)).listToDto(cart.getMeals());
        verify(cartCalculator, times(1)).calculateCartInfo(cartDto);
        verify(cartCalculator, times(1)).sumProductsAndProductsFromMeals(cartDto.getProducts(), new ArrayList<>());
        verify(cartCalculator, times(1)).retrieveProductsFromMeals(new ArrayList<>());
        verifyNoMoreInteractions(productDtoConverter, mealDtoConverter, cartCalculator);
    }

    @Test
    void toDto_withMealsAndProducts() {
        addProducts();
        addMeals();

        final var converted = cartDtoConverter.toDto(cart);
        assertCartFields(cartDto, converted);
        verifyNoMoreInteractions(productDtoConverter, mealDtoConverter, cartCalculator);
    }

    @Test
    void toDto_withMealsWithMealsAndProductsDuplicated() {
        Product firstProduct = ProductSample.breadWithId();
        ProductDto firstProductDto = ProductSample.breadWithIdDto();
        cart.getProducts().add(firstProduct);
        cartDto.getProducts().add(firstProductDto);
        cartDto.getAllProducts().add(firstProductDto);
        cartDto.setItemCounter(cartDto.getItemCounter() + 1);

        Product secondProduct = ProductSample.bananaWithId();
        ProductDto secondProductDto = ProductSample.bananaWithIdDto();
        cart.getProducts().add(secondProduct);
        cartDto.getProducts().add(secondProductDto);
        cartDto.getAllProducts().add(secondProductDto);
        cartDto.setItemCounter(cartDto.getItemCounter() + 1);

        Meal firstMealWithSecondProduct = MealSample.coffeeWithId();
        firstMealWithSecondProduct.getProducts().add(ProductSample.bananaWithId());
        MealDto firstMealWithSecondProductDto = MealSample.coffeeWithIdDto();
        firstMealWithSecondProductDto.getProducts().add(ProductSample.bananaWithIdDto());

        cart.getMeals().add(firstMealWithSecondProduct);
        cartDto.getMeals().add(firstMealWithSecondProductDto);
        cartDto.setItemCounter(cartDto.getItemCounter() + 1);

        Meal dumplingsMeal = MealSample.dumplingsWithId();
        dumplingsMeal.getProducts().add(ProductSample.bananaWithId());

        MealDto dumplingsMealDto = MealSample.dumplingsWithIdDto();
        dumplingsMealDto.getProducts().add(ProductSample.bananaWithIdDto());

        cart.getMeals().add(dumplingsMeal);
        cartDto.getMeals().add(dumplingsMealDto);
        cartDto.getAllProducts().addAll(cartDto.getProducts());
        cartDto.setItemCounter(cartDto.getItemCounter() + 1);

        final var converted = cartDtoConverter.toDto(cart);
        assertCartFields(cartDto, converted);
        verifyNoMoreInteractions(productDtoConverter, mealDtoConverter, cartCalculator);
    }


    @Test
    void fromDto() {
        final Cart converted = cartDtoConverter.fromDto(cartDto);
        assertCartFields(cart, converted);
        verifyNoMoreInteractions(productDtoConverter, mealDtoConverter, cartCalculator);
    }

    @Test
    void fromDto_withMealsAndProducts() {
        addMeals();
        addProducts();

        final Cart converted = cartDtoConverter.fromDto(cartDto);
        assertCartFields(cart, converted);
        verifyNoMoreInteractions(productDtoConverter, mealDtoConverter, cartCalculator);
    }

    private void addProducts() {
        cart.getProducts().add(ProductSample.bananaWithId());
        cart.getProducts().add(ProductSample.breadWithId());

        cartDto.getProducts().add(ProductSample.bananaWithIdDto());
        cartDto.getProducts().add(ProductSample.breadWithIdDto());
        cartDto.setItemCounter(cartDto.getItemCounter() + 2);
        cartDto.getAllProducts().addAll(cartDto.getProducts());
    }

    private void addMeals() {
        cart.getMeals().add(MealSample.coffeeWithId());
        cart.getMeals().add(MealSample.dumplingsWithId());

        cartDto.getMeals().add(MealSample.coffeeWithIdDto());
        cartDto.getMeals().add(MealSample.dumplingsWithIdDto());
        cartDto.setItemCounter(cartDto.getItemCounter() + 2);
    }

}