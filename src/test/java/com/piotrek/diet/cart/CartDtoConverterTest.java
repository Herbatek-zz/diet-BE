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

import static com.piotrek.diet.helpers.AssertEqualAllFields.assertCartFields;
import static org.mockito.Mockito.*;

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
        when(cartCalculator.calculateCartInfo(cartDto)).thenReturn(cartDto);

        final var converted = cartDtoConverter.toDto(cart);

        assertCartFields(cartDto, converted);
        verify(productDtoConverter, times(1)).listToDto(cart.getProducts());
        verify(mealDtoConverter, times(1)).listToDto(cart.getMeals());
        verify(cartCalculator, times(1)).calculateCartInfo(cartDto);
        verifyNoMoreInteractions(productDtoConverter, mealDtoConverter, cartCalculator);
    }

    @Test
    void toDto_withProducts() {
        cart.getProducts().add(ProductSample.banana());
        cartDto.getProducts().add(ProductSample.bananaDto());
        cartDto.getAllProducts().add(ProductSample.bananaDto());
        cartDto.setItemCounter(cartDto.getItemCounter() + 1);

        final var converted = cartDtoConverter.toDto(cart);

        assertCartFields(cartDto, converted);
        verify(productDtoConverter, times(1)).listToDto(cart.getProducts());
        verify(mealDtoConverter, times(1)).listToDto(cart.getMeals());
        verify(cartCalculator, times(1)).calculateCartInfo(cartDto);
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
        Product firstProduct = ProductSample.bread();
        ProductDto firstProductDto = ProductSample.breadDto();
        cart.getProducts().add(firstProduct);
        cartDto.getProducts().add(firstProductDto);
        cartDto.getAllProducts().add(firstProductDto);
        cartDto.setItemCounter(cartDto.getItemCounter() + 1);

        Product secondProduct = ProductSample.banana();
        ProductDto secondProductDto = ProductSample.bananaDto();
        cart.getProducts().add(secondProduct);
        cartDto.getProducts().add(secondProductDto);
        cartDto.getAllProducts().add(secondProductDto);
        cartDto.setItemCounter(cartDto.getItemCounter() + 1);

        Meal firstMealWithSecondProduct = MealSample.coffee();
        firstMealWithSecondProduct.getProducts().add(ProductSample.banana());
        MealDto firstMealWithSecondProductDto = MealSample.coffeeDto();
        firstMealWithSecondProductDto.getProducts().add(ProductSample.bananaDto());

        cart.getMeals().add(firstMealWithSecondProduct);
        cartDto.getMeals().add(firstMealWithSecondProductDto);
        cartDto.setItemCounter(cartDto.getItemCounter() + 1);

        Meal dumplingsMeal = MealSample.dumplings();
        dumplingsMeal.getProducts().add(ProductSample.banana());

        MealDto dumplingsMealDto = MealSample.dumplingsDto();
        dumplingsMealDto.getProducts().add(ProductSample.bananaDto());

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
        cart.getProducts().add(ProductSample.banana());
        cart.getProducts().add(ProductSample.bread());

        cartDto.getProducts().add(ProductSample.bananaDto());
        cartDto.getProducts().add(ProductSample.breadDto());
        cartDto.setItemCounter(cartDto.getItemCounter() + 2);
        cartDto.getAllProducts().addAll(cartDto.getProducts());
    }

    private void addMeals() {
        cart.getMeals().add(MealSample.coffee());
        cart.getMeals().add(MealSample.dumplings());

        cartDto.getMeals().add(MealSample.coffeeDto());
        cartDto.getMeals().add(MealSample.dumplingsDto());
        cartDto.setItemCounter(cartDto.getItemCounter() + 2);
    }

}