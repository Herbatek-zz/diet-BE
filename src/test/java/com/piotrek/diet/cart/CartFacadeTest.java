package com.piotrek.diet.cart;

import com.piotrek.diet.meal.MealService;
import com.piotrek.diet.product.ProductService;
import com.piotrek.diet.sample.CartSample;
import com.piotrek.diet.sample.MealSample;
import com.piotrek.diet.sample.ProductSample;
import com.piotrek.diet.user.UserValidation;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CartFacadeTest {

    @Mock
    private UserValidation userValidation;

    @Mock
    private CartService cartService;

    @Mock
    private CartDtoConverter cartDtoConverter;

    @Mock
    private MealService mealService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private CartFacade cartFacade;

    private Cart cart;
    private CartDto cartDto;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.initMocks(this);
        cart = CartSample.cart1();
        cartDto = CartSample.cartDto1();
    }

    @Test
    @DisplayName("Add meal to cart, when cart is empty, then cart should has 1 meal")
    void addMealToCart_whenCartIsEmpty_thenCartShouldHasOneMeal() {
        final var meal = MealSample.coffeeWithId();
        final var mealDto = MealSample.coffeeWithIdDto();

        when(cartService.findById(cart.getId())).thenReturn(Mono.just(cart));
        when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(cartService.save(cart)).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);

        cartDto.getMeals().add(mealDto);
        cartDto.getAllProducts().addAll(mealDto.getProducts());

        final var block = cartFacade.addMealToCart(cart.getId(), meal.getId()).block();

        assertAll(
                () -> assertEquals(cart.getId(), block.getId()),
                () -> assertEquals(cart.getMeals().size(), block.getMeals().size()),
                () -> assertEquals(cart.getUserId(), block.getUserId()),
                () -> assertEquals(cart.getDate(), block.getDate()),
                () -> assertEquals(cart.getProducts().size(), block.getProducts().size()),
                () -> assertEquals(2, block.getAllProducts().size())
        );
        verify(cartService, times(1)).findById(cart.getId());
        verify(mealService, times(1)).findById(meal.getId());
        verify(cartService, times(1)).save(cart);
        verify(cartDtoConverter, times(1)).toDto(cart);
        verifyNoMoreInteractions(cartService, mealService, cartDtoConverter);
    }

    @Test
    @DisplayName("Add meal to cart, when cart had one meal, then cart should has 2 meals")
    void addMealToCart_whenCartHadOneMeal_thenCartShouldHasTwoMeals() {
        final var meal = MealSample.coffeeWithId();
        final var mealDto = MealSample.coffeeWithIdDto();

        when(cartService.findById(cart.getId())).thenReturn(Mono.just(cart));
        when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(cartService.save(cart)).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);

        cart.getMeals().add(meal);
        cartDto.getMeals().add(mealDto);
        cartDto.getMeals().add(MealSample.dumplingsWithIdDto());
        cartDto.getAllProducts().addAll(mealDto.getProducts());
        cartDto.getAllProducts().addAll(MealSample.dumplingsWithIdDto().getProducts());

        var block = cartFacade.addMealToCart(cart.getId(), meal.getId()).block();

        assertAll(
                () -> assertEquals(cart.getId(), block.getId()),
                () -> assertEquals(cart.getMeals().size(), block.getMeals().size()),
                () -> assertEquals(cart.getUserId(), block.getUserId()),
                () -> assertEquals(cart.getDate(), block.getDate()),
                () -> assertEquals(cart.getProducts().size(), block.getProducts().size()),
                () -> assertEquals(4, block.getAllProducts().size())
        );
        verify(cartService, times(1)).findById(cart.getId());
        verify(mealService, times(1)).findById(meal.getId());
        verify(cartService, times(1)).save(cart);
        verify(cartDtoConverter, times(1)).toDto(cart);
        verifyNoMoreInteractions(cartService, mealService, cartDtoConverter);
    }

    @Test
    @DisplayName("Add meal to cart, when cart had one product, then cart should has one product and one meal")
    void addMealToCart_whenCartHadOneProduct_thenCartShouldHasOneMealAndOneProduct() {
        final var meal = MealSample.coffeeWithId();
        final var mealDto = MealSample.coffeeWithIdDto();
        final var product = ProductSample.bananaWithId();
        final var productDto = ProductSample.bananaWithIdDto();

        when(cartService.findById(cart.getId())).thenReturn(Mono.just(cart));
        when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(cartService.save(cart)).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);

        cart.getProducts().add(product);
        cartDto.getProducts().add(productDto);
        cartDto.getAllProducts().add(productDto);
        cartDto.getMeals().add(mealDto);
        cartDto.getAllProducts().addAll(mealDto.getProducts());

        var block = cartFacade.addMealToCart(cart.getId(), meal.getId()).block();

        assertAll(
                () -> assertEquals(cart.getId(), block.getId()),
                () -> assertEquals(cart.getMeals().size(), block.getMeals().size()),
                () -> assertEquals(cart.getUserId(), block.getUserId()),
                () -> assertEquals(cart.getDate(), block.getDate()),
                () -> assertEquals(cart.getProducts().size(), block.getProducts().size()),
                () -> assertEquals(3, block.getAllProducts().size())
        );
        verify(cartService, times(1)).findById(cart.getId());
        verify(mealService, times(1)).findById(meal.getId());
        verify(cartService, times(1)).save(cart);
        verify(cartDtoConverter, times(1)).toDto(cart);
        verifyNoMoreInteractions(cartService, mealService, cartDtoConverter);
    }

    @Test
    @DisplayName("Delete meal from cart, when cart had one meal, then cart should be empty")
    void deleteMealFromCart_whenCartHad1Meal_thenCartShouldBeEmpty() {
        final var meal = MealSample.coffeeWithId();

        when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(cartService.findById(cart.getId())).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);
        when(cartService.save(cart)).thenReturn(Mono.just(cart));

        cart.getMeals().add(meal);

        final var block = cartFacade.deleteMealFromCart(cart.getId(), meal.getId()).block();

        assertAll(
                () -> assertEquals(cartDto.getId(), block.getId()),
                () -> assertEquals(cartDto.getDate(), block.getDate()),
                () -> assertEquals(cartDto.getUserId(), block.getUserId()),
                () -> assertEquals(cartDto.getAllProducts(), block.getAllProducts()),
                () -> assertEquals(cartDto.getProducts(), block.getProducts()),
                () -> assertEquals(cartDto.getMeals(), block.getMeals())
        );
        assertEquals(0, cart.getMeals().size());
        verify(mealService, times(1)).findById(meal.getId());
        verify(cartService, times(1)).save(cart);
        verify(cartDtoConverter, times(1)).toDto(cart);
        verify(cartService, times(1)).findById(cart.getId());
        verifyNoMoreInteractions(cartService, mealService, cartDtoConverter);
    }

    @Test
    @DisplayName("Delete meal from cart, when cart had no meals, then return empty cart")
    void deleteMealFromCart_whenCartHadNoMeals_thenCartShouldBeEmpty() {
        final var meal = MealSample.coffeeWithId();

        when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(cartService.findById(cart.getId())).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);
        when(cartService.save(cart)).thenReturn(Mono.just(cart));

        CartDto block = cartFacade.deleteMealFromCart(cart.getId(), meal.getId()).block();

        assertAll(
                () -> assertEquals(cartDto.getId(), block.getId()),
                () -> assertEquals(cartDto.getDate(), block.getDate()),
                () -> assertEquals(cartDto.getUserId(), block.getUserId()),
                () -> assertEquals(0, block.getAllProducts().size()),
                () -> assertEquals(0, block.getProducts().size()),
                () -> assertEquals(0, block.getMeals().size())
        );

        assertEquals(0, cart.getMeals().size());
        verify(mealService, times(1)).findById(meal.getId());
        verify(cartService, times(1)).findById(cart.getId());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verifyNoMoreInteractions(cartService, mealService, cartDtoConverter);
    }

    @Test
    @DisplayName("Add product to cart, when cart is empty, then cart should has 1 product")
    void addProductToCart_whenCartIsEmpty_thenCartShouldHasOneProduct() {
        final var product = ProductSample.bananaWithId();
        final var productDto = ProductSample.bananaWithIdDto();

        when(cartService.findById(cart.getId())).thenReturn(Mono.just(cart));
        when(productService.findById(product.getId())).thenReturn(Mono.just(product));
        when(cartService.save(cart)).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);

        cartDto.getProducts().add(productDto);
        cartDto.getAllProducts().add(productDto);

        final var block = cartFacade.addProductToCart(cart.getId(), product.getId()).block();

        assertAll(
                () -> assertEquals(cart.getId(), block.getId()),
                () -> assertEquals(cart.getMeals().size(), block.getMeals().size()),
                () -> assertEquals(cart.getUserId(), block.getUserId()),
                () -> assertEquals(cart.getDate(), block.getDate()),
                () -> assertEquals(cart.getProducts().size(), block.getProducts().size()),
                () -> assertEquals(1, block.getAllProducts().size())
        );
        verify(cartService, times(1)).findById(cart.getId());
        verify(productService, times(1)).findById(product.getId());
        verify(cartService, times(1)).save(cart);
        verify(cartDtoConverter, times(1)).toDto(cart);
        verifyNoMoreInteractions(cartService, mealService, cartDtoConverter);
    }

    @Test
    @DisplayName("Add product to cart, when cart had one product, then cart should has 2 products")
    void addProductToCart_whenCartHadOneProduct_thenCartShouldHasTwoProducts() {
        final var product = ProductSample.bananaWithId();
        final var productDto = ProductSample.bananaWithIdDto();

        when(cartService.findById(cart.getId())).thenReturn(Mono.just(cart));
        when(productService.findById(product.getId())).thenReturn(Mono.just(product));
        when(cartService.save(cart)).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);

        cart.getProducts().add(product);
        cartDto.getProducts().add(productDto);
        cartDto.getProducts().add(ProductSample.breadWithIdDto());
        cartDto.getAllProducts().add(productDto);
        cartDto.getAllProducts().add(ProductSample.breadWithIdDto());

        var block = cartFacade.addProductToCart(cart.getId(), product.getId()).block();

        assertAll(
                () -> assertEquals(cart.getId(), block.getId()),
                () -> assertEquals(cart.getMeals().size(), block.getMeals().size()),
                () -> assertEquals(cart.getUserId(), block.getUserId()),
                () -> assertEquals(cart.getDate(), block.getDate()),
                () -> assertEquals(cart.getProducts().size(), block.getProducts().size()),
                () -> assertEquals(2, block.getAllProducts().size())
        );
        verify(cartService, times(1)).findById(cart.getId());
        verify(productService, times(1)).findById(product.getId());
        verify(cartService, times(1)).save(cart);
        verify(cartDtoConverter, times(1)).toDto(cart);
        verifyNoMoreInteractions(cartService, mealService, cartDtoConverter);
    }

    @Test
    @DisplayName("Add product to cart, when cart had one meal, then cart should has one product and one meal")
    void addProductToCart_whenCartHadOneMeal_thenCartShouldHasOneMealAndOneProduct() {
        final var meal = MealSample.coffeeWithId();
        final var mealDto = MealSample.coffeeWithIdDto();
        final var product = ProductSample.bananaWithId();
        final var productDto = ProductSample.bananaWithIdDto();

        when(cartService.findById(cart.getId())).thenReturn(Mono.just(cart));
        when(productService.findById(product.getId())).thenReturn(Mono.just(product));
        when(cartService.save(cart)).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);
        cart.getMeals().add(meal);
        cartDto.getProducts().add(productDto);
        cartDto.getAllProducts().add(productDto);
        cartDto.getMeals().add(mealDto);
        cartDto.getAllProducts().addAll(mealDto.getProducts());

        var block = cartFacade.addProductToCart(cart.getId(), product.getId()).block();

        assertAll(
                () -> assertEquals(cart.getId(), block.getId()),
                () -> assertEquals(cart.getMeals().size(), block.getMeals().size()),
                () -> assertEquals(cart.getUserId(), block.getUserId()),
                () -> assertEquals(cart.getDate(), block.getDate()),
                () -> assertEquals(cart.getProducts().size(), block.getProducts().size()),
                () -> assertEquals(3, block.getAllProducts().size())
        );
        verify(cartService, times(1)).findById(cart.getId());
        verify(productService, times(1)).findById(product.getId());
        verify(cartService, times(1)).save(cart);
        verify(cartDtoConverter, times(1)).toDto(cart);
        verifyNoMoreInteractions(cartService, mealService, cartDtoConverter);
    }

    @Test
    @DisplayName("Delete product from cart, when cart had one product, then cart should be empty")
    void deleteProductFromCart_whenCartHadOneProduct_thenCartShouldBeEmpty() {
        final var product = ProductSample.bananaWithId();

        when(productService.findById(product.getId())).thenReturn(Mono.just(product));
        when(cartService.findById(cart.getId())).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);
        when(cartService.save(cart)).thenReturn(Mono.just(cart));

        cart.getProducts().add(product);

        final var block = cartFacade.deleteProductFromCart(cart.getId(), product.getId()).block();

        assertAll(
                () -> assertEquals(cartDto.getId(), block.getId()),
                () -> assertEquals(cartDto.getDate(), block.getDate()),
                () -> assertEquals(cartDto.getUserId(), block.getUserId()),
                () -> assertEquals(0, block.getAllProducts().size()),
                () -> assertEquals(0, block.getProducts().size()),
                () -> assertEquals(0, block.getMeals().size())
        );
        assertEquals(0, cart.getMeals().size());
        verify(productService, times(1)).findById(product.getId());
        verify(cartService, times(1)).save(cart);
        verify(cartDtoConverter, times(1)).toDto(cart);
        verify(cartService, times(1)).findById(cart.getId());
        verifyNoMoreInteractions(cartService, mealService, cartDtoConverter);
    }

    @Test
    @DisplayName("Delete product from cart, when cart had no products, then cart should be empty")
    void deleteProductFromCart_whenCartHadNoProducts_thenCartShouldBeEmpty() {
        final var product = ProductSample.bananaWithId();

        when(productService.findById(product.getId())).thenReturn(Mono.just(product));
        when(cartService.findById(cart.getId())).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);
        when(cartService.save(cart)).thenReturn(Mono.just(cart));

        CartDto block = cartFacade.deleteProductFromCart(cart.getId(), product.getId()).block();

        assertAll(
                () -> assertEquals(cartDto.getId(), block.getId()),
                () -> assertEquals(cartDto.getDate(), block.getDate()),
                () -> assertEquals(cartDto.getUserId(), block.getUserId()),
                () -> assertEquals(0, block.getAllProducts().size()),
                () -> assertEquals(0, block.getProducts().size()),
                () -> assertEquals(0, block.getMeals().size())
        );
        assertEquals(0, cart.getMeals().size());
        verify(productService, times(1)).findById(product.getId());
        verify(cartService, times(1)).findById(cart.getId());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verifyNoMoreInteractions(cartService, mealService, cartDtoConverter);
    }
}
