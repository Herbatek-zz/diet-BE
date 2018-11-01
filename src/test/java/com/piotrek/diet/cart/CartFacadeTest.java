package com.piotrek.diet.cart;

import com.piotrek.diet.helpers.CartSample;
import com.piotrek.diet.helpers.MealSample;
import com.piotrek.diet.helpers.ProductSample;
import com.piotrek.diet.helpers.UserSample;
import com.piotrek.diet.helpers.exceptions.NotFoundException;
import com.piotrek.diet.meal.Meal;
import com.piotrek.diet.meal.MealDto;
import com.piotrek.diet.meal.MealService;
import com.piotrek.diet.product.Product;
import com.piotrek.diet.product.ProductDto;
import com.piotrek.diet.product.ProductService;
import com.piotrek.diet.user.User;
import com.piotrek.diet.user.UserValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

import static com.piotrek.diet.helpers.MealSample.*;
import static com.piotrek.diet.helpers.MealSample.dumplingsWithIdDto;
import static com.piotrek.diet.helpers.ProductSample.bananaWithId;
import static com.piotrek.diet.helpers.ProductSample.bananaWithIdDto;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class CartFacadeTest {

    @Mock
    private CartDtoConverter cartDtoConverter;

    @Mock
    private CartService cartService;

    @Mock
    private ProductService productService;

    @Mock
    private UserValidation userValidation;

    @Mock
    private MealService mealService;

    @InjectMocks
    private CartFacade cartFacade;

    private Product product;
    private ProductDto productDto;

    private Meal meal;
    private MealDto mealDto;

    private Meal meal2;
    private MealDto mealDto2;

    private User user;

    private Cart cart;
    private CartDto cartDto;

    @BeforeEach
    void beforeEach() {
        initProducts();
        initMeals();
        user = UserSample.johnWithId();
        cart = CartSample.cart1();
        cartDto = CartSample.cartDto1();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Find cart, when found, then return")
    void findCart_whenFound_thenReturn() {
        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);

        final CartDto block = cartFacade.findDtoCartByUserAndDate(cart.getUserId(), cart.getDate()).block();

        this.assertEqualsAllCartFields(cartDto, block);
        verify(cartService, times(1)).findByUserIdAndDate(user.getId(), cart.getDate());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verifyNoMoreInteractions(cartService, userValidation, productService, mealService);
    }

    @Test
    @DisplayName("Find cart, when not found, then return")
    void findCart_whenNotFound_thenThrowNotFoundException() {
        when(cartService.findByUserIdAndDate(cartDto.getUserId(), cartDto.getDate())).thenThrow(new NotFoundException(""));

        assertThrows(NotFoundException.class, () -> cartFacade.findDtoCartByUserAndDate(cartDto.getUserId(), cartDto.getDate()).block());

        verify(cartService, times(1)).findByUserIdAndDate(user.getId(), cart.getDate());
        verifyNoMoreInteractions(cartService, userValidation, productService, mealService);
    }

    @Test
    @DisplayName("Add meal to cart, when cart is empty, then cart should has 1 meal")
    void addMealToCart_whenCartIsEmpty_thenCartShouldHasOneMeal() {
        final var AMOUNT = 100;

        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);
        when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(cartService.save(cart)).thenReturn(Mono.just(cart));

        cartDto.getMeals().add(mealDto);
        cartDto.getAllProducts().addAll(mealDto.getProducts());

        final var block = cartFacade.addMealToCart(user.getId(), meal.getId(), cart.getDate(), AMOUNT).block();

        this.assertEqualsAllCartFields(cartDto, block);
        this.assertEqualMealAllFields(cartDto.getMeals().get(0), block.getMeals().get(0));
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verify(userValidation, times(1)).validateUserWithPrincipal(cart.getUserId());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verify(mealService, times(1)).findById(meal.getId());
        verify(mealService, times(1)).calculateMealInformation(meal);
        verify(cartService, times(1)).save(cart);
        verifyNoMoreInteractions(cartService, userValidation, productService, mealService);
    }

    @Test
    @DisplayName("Add meal to cart, when cart is empty, then cart should has 1 meal")
    void addMealToCart_whenThereIsNoCart_thenCreateCartAndAddMeal() {
        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.error(new NotFoundException("")));
        when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);
        when(cartService.save(any(Cart.class))).thenReturn(Mono.just(cart));

        cartDto.getMeals().add(mealDto);
        cartDto.getAllProducts().addAll(mealDto.getProducts());

        final var block = cartFacade.addMealToCart(user.getId(), meal.getId(), cart.getDate(), 100).block();

        this.assertEqualsAllCartFields(cartDto, block);
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verify(mealService, times(1)).findById(meal.getId());
        verify(mealService, times(1)).calculateMealInformation(meal);
        verify(cartService, times(1)).save(any(Cart.class));
        verifyNoMoreInteractions(cartService, userValidation, productService, mealService);
    }

    @Test
    @DisplayName("Add meal to cart, when cart had one meal, then cart should has 2 meals")
    void addMealToCart_whenCartHadOneMeal_thenCartShouldHasTwoMeals() {
        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);
        when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(cartService.save(cart)).thenReturn(Mono.just(cart));

        cart.getMeals().add(meal);
        cartDto.getMeals().add(mealDto);
        cartDto.getMeals().add(MealSample.dumplingsWithIdDto());
        cartDto.getAllProducts().addAll(mealDto.getProducts());
        cartDto.getAllProducts().addAll(MealSample.dumplingsWithIdDto().getProducts());

        var block = cartFacade.addMealToCart(user.getId(), meal.getId(), cart.getDate(), 100).block();

        this.assertEqualsAllCartFields(cartDto, block);
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verify(mealService, times(1)).findById(meal.getId());
        verify(mealService, times(1)).calculateMealInformation(meal);
        verify(cartService, times(1)).save(cart);
        verifyNoMoreInteractions(cartService, userValidation, productService, mealService);
    }

    @Test
    @DisplayName("Add meal to cart, when cart had one product, then cart should has one product and one meal")
    void addMealToCart_whenCartHadOneProduct_thenCartShouldHasOneMealAndOneProduct() {
        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);
        when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(cartService.save(cart)).thenReturn(Mono.just(cart));

        cart.getProducts().add(product);
        cartDto.getProducts().add(productDto);
        cartDto.getAllProducts().add(productDto);
        cartDto.getMeals().add(mealDto);
        cartDto.getAllProducts().addAll(mealDto.getProducts());

        var block = cartFacade.addMealToCart(user.getId(), meal.getId(), cart.getDate(), 100).block();

        this.assertEqualsAllCartFields(cartDto, block);
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verify(mealService, times(1)).findById(meal.getId());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verify(mealService, times(1)).calculateMealInformation(meal);
        verify(cartService, times(1)).save(cart);
        verifyNoMoreInteractions(cartService, userValidation, productService, mealService);
    }

    @Test
    @DisplayName("Delete meal from cart, when cart had one meal, then cart should be empty")
    void deleteMealFromCart_whenCartHad1Meal_thenCartShouldBeEmpty() {
        final var expected = CartSample.cartDto1();

        when(cartService.findByUserIdAndDate(cartDto.getUserId(), cartDto.getDate())).thenReturn(Mono.just(cart));
        when(cartService.save(cart)).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);

        CartDto block = cartFacade.deleteMealFromCart(cart.getUserId(), mealDto.getId(), cart.getDate()).block();

        this.assertEqualsAllCartFields(expected, block);
        verify(userValidation, times(1)).validateUserWithPrincipal(cartDto.getUserId());
        verify(cartService, times(1)).findByUserIdAndDate(cartDto.getUserId(), cartDto.getDate());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verifyNoMoreInteractions(cartService, userValidation, productService, mealService);
    }

    @Test
    @DisplayName("Delete meal from cart, when cart had no meals, then return empty cart")
    void deleteMealFromCart_whenCartHadNoMeals_thenCartShouldBeEmpty() {
        var expected = CartSample.cartDto1();
        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);

        CartDto block = cartFacade.deleteMealFromCart(cartDto.getUserId(), meal.getId(), cartDto.getDate()).block();

        this.assertEqualsAllCartFields(expected, block);
        verify(userValidation, times(1)).validateUserWithPrincipal(cartDto.getUserId());
        verify(cartService, times(1)).findByUserIdAndDate(cartDto.getUserId(), cartDto.getDate());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verifyNoMoreInteractions(cartService, userValidation, productService, mealService);
    }

    @Test
    @DisplayName("Add product to cart, when there is no cart, then cart should be created and product should be added")
    void addProductToCart_whenThereIsNoCart_thenCreateCartAndAddProduct() {
        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.error(new NotFoundException("")));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);
        when(productService.findById(product.getId())).thenReturn(Mono.just(product));
        when(cartService.save(any(Cart.class))).thenReturn(Mono.just(cart));

        cartDto.getProducts().add(productDto);
        cartDto.getAllProducts().add(productDto);

        final var block = cartFacade.addProductToCart(user.getId(), product.getId(), cart.getDate(), 100).block();

        this.assertEqualsAllCartFields(cartDto, block);
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verify(productService, times(1)).findById(product.getId());
        verify(cartService, times(1)).save(any(Cart.class));
        verify(productService, times(1)).calculateProductInfoByAmount(product);
        verifyNoMoreInteractions(cartService, userValidation, productService, mealService);
    }

    @Test
    @DisplayName("Add product to cart, when cart is empty, then cart should has 1 product")
    void addProductToCart_whenCartIsEmpty_thenCartShouldHasOneProduct() {
        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);
        when(productService.findById(product.getId())).thenReturn(Mono.just(product));
        when(cartService.save(cart)).thenReturn(Mono.just(cart));

        cartDto.getProducts().add(productDto);
        cartDto.getAllProducts().add(productDto);

        final var block = cartFacade.addProductToCart(user.getId(), product.getId(), cart.getDate(), 100).block();

        this.assertEqualsAllCartFields(cartDto, block);
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verify(productService, times(1)).findById(product.getId());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verify(cartService, times(1)).save(cart);
        verify(productService, times(1)).calculateProductInfoByAmount(product);
        verifyNoMoreInteractions(cartService, userValidation, productService, mealService);
    }

    @Test
    @DisplayName("Add product to cart, when cart had one product, then cart should has 2 products")
    void addProductToCart_whenCartHadOneProduct_thenCartShouldHasTwoProducts() {
        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);
        when(productService.findById(product.getId())).thenReturn(Mono.just(product));
        when(cartService.save(cart)).thenReturn(Mono.just(cart));

        cart.getProducts().add(product);
        cartDto.getProducts().add(productDto);
        cartDto.getProducts().add(ProductSample.breadWithIdDto());
        cartDto.getAllProducts().add(productDto);
        cartDto.getAllProducts().add(ProductSample.breadWithIdDto());

        var block = cartFacade.addProductToCart(user.getId(), product.getId(), cart.getDate(), 100).block();

        this.assertEqualsAllCartFields(cartDto, block);
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verify(productService, times(1)).findById(product.getId());
        verify(cartService, times(1)).save(cart);
        verify(productService, times(1)).calculateProductInfoByAmount(product);
        verifyNoMoreInteractions(cartService, userValidation, productService, mealService);
    }

    @Test
    @DisplayName("Add product to cart, when cart had one meal, then cart should has one product and one meal")
    void addProductToCart_whenCartHadOneMeal_thenCartShouldHasOneMealAndOneProduct() {
        final int amount = 100;
        meal.setAmount(amount);
        mealDto.setAmount(amount);
        product.setAmount(amount);
        productDto.setAmount(amount);

        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.just(cart));
        when(productService.findById(product.getId())).thenReturn(Mono.just(product));
        when(cartService.save(cart)).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);

        cart.getMeals().add(meal);
        cartDto.getProducts().add(productDto);
        cartDto.getAllProducts().add(productDto);
        cartDto.getMeals().add(mealDto);
        cartDto.getAllProducts().addAll(mealDto.getProducts());

        var block = cartFacade.addProductToCart(user.getId(), product.getId(), cart.getDate(), 100).block();

        this.assertEqualsAllCartFields(cartDto, block);
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verify(productService, times(1)).calculateProductInfoByAmount(product);
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verify(productService, times(1)).findById(product.getId());
        verify(cartService, times(1)).save(cart);
        verifyNoMoreInteractions(cartService, userValidation, productService, mealService);
    }

    @Test
    @DisplayName("Delete product from cart, when cart had one product, then cart should be empty")
    void deleteProductFromCart_whenCartHadOneProduct_thenCartShouldBeEmpty() {
        cart.getProducts().add(product);

        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.just(cart));
        when(productService.findById(product.getId())).thenReturn(Mono.just(product));
        when(cartService.save(cart)).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);

        CartDto block = cartFacade.deleteProductFromCart(user.getId(), product.getId(), cart.getDate()).block();

        var expected = CartSample.cartDto1();
        expected.setProducts(new ArrayList<>());

        this.assertEqualsAllCartFields(expected, block);
        verify(userValidation, times(1)).validateUserWithPrincipal(cartDto.getUserId());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verify(productService, times(1)).findById(product.getId());
        verify(cartService, times(1)).save(cart);
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verifyNoMoreInteractions(cartService, userValidation, productService, mealService);
    }

    @Test
    @DisplayName("Delete product from cart, when cart had no products, then cart should be empty")
    void deleteProductFromCart_whenCartHadNoProducts_thenCartShouldBeEmpty() {
        when(productService.findById(product.getId())).thenReturn(Mono.just(product));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);
        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.just(cart));
        when(cartService.save(cart)).thenReturn(Mono.just(cart));

        CartDto actual = cartFacade.deleteProductFromCart(user.getId(), product.getId(), cart.getDate()).block();

        this.assertEqualsAllCartFields(cartDto, actual);
        verify(productService, times(1)).findById(product.getId());
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verifyNoMoreInteractions(cartService, userValidation, productService, mealService);
    }
    private void initMeals() {
        mealDto = dumplingsWithIdDto();
        meal = dumplingsWithId();
        mealDto2 = coffeeWithIdDto();
        meal2 = coffeeWithId();
    }

    private void initProducts() {
        productDto = bananaWithIdDto();
        product = bananaWithId();
    }

    private void assertEqualsAllCartFields(CartDto expected, CartDto actual) {
        assertAll(
                () -> assertEquals(expected.getId(), actual.getId()),
                () -> assertEquals(expected.getMeals().size(), actual.getMeals().size(), "Cart meal list size"),
                () -> assertEquals(expected.getProducts().size(), actual.getProducts().size(), "Cart products list size"),
                () -> assertEquals(expected.getAllProducts(), actual.getAllProducts(), "Cart all products list"),
                () -> assertEquals(expected.getUserId(), actual.getUserId()),
                () -> assertEquals(expected.getDate(), actual.getDate())
        );
    }

    private void assertEqualMealAllFields(MealDto expected, MealDto actual) {
        assertNotNull(actual);
        assertAll(
                () -> assertEquals(expected.getId(), actual.getId()),
                () -> assertEquals(expected.getName(), actual.getName()),
                () -> assertEquals(expected.getDescription(), actual.getDescription()),
                () -> assertEquals(expected.getRecipe(), actual.getRecipe()),
                () -> assertEquals(expected.getProtein(), actual.getProtein()),
                () -> assertEquals(expected.getCarbohydrate(), actual.getCarbohydrate()),
                () -> assertEquals(expected.getFat(), actual.getFat()),
                () -> assertEquals(expected.getFibre(), actual.getFibre()),
                () -> assertEquals(expected.getKcal(), actual.getKcal()),
                () -> assertEquals(expected.getAmount(), actual.getAmount()),
                () -> assertEquals(expected.getImageUrl(), actual.getImageUrl()),
                () -> assertEquals(expected.getCarbohydrateExchange(), actual.getCarbohydrateExchange()),
                () -> assertEquals(expected.getProteinAndFatEquivalent(), actual.getProteinAndFatEquivalent()),
                () -> assertEquals(expected.getProducts().size(), actual.getProducts().size()),
                () -> assertEquals(expected.getUserId(), actual.getUserId())
        );
    }
}
