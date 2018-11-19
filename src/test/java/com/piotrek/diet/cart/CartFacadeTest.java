package com.piotrek.diet.cart;

import com.piotrek.diet.helpers.CartSample;
import com.piotrek.diet.helpers.MealSample;
import com.piotrek.diet.helpers.ProductSample;
import com.piotrek.diet.helpers.UserSample;
import com.piotrek.diet.exceptions.NotFoundException;
import com.piotrek.diet.meal.Meal;
import com.piotrek.diet.meal.MealDto;
import com.piotrek.diet.meal.MealService;
import com.piotrek.diet.product.Product;
import com.piotrek.diet.product.ProductDto;
import com.piotrek.diet.product.ProductService;
import com.piotrek.diet.user.User;
import com.piotrek.diet.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

import static com.piotrek.diet.helpers.AssertEqualAllFields.assertCartFields;
import static com.piotrek.diet.helpers.AssertEqualAllFields.assertMealFields;
import static com.piotrek.diet.helpers.AssertEqualAllFields.assertProductFields;
import static com.piotrek.diet.helpers.MealSample.dumplings;
import static com.piotrek.diet.helpers.MealSample.dumplingsDto;
import static com.piotrek.diet.helpers.ProductSample.banana;
import static com.piotrek.diet.helpers.ProductSample.bananaDto;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CartFacadeTest {

    @Mock
    private CartService cartService;

    @Mock
    private UserService userService;

    @Mock
    private MealService mealService;

    @Mock
    private ProductService productService;

    @Mock
    private CartDtoConverter cartDtoConverter;

    @InjectMocks
    private CartFacade cartFacade;

    private Product product;
    private ProductDto productDto;

    private Meal meal;
    private MealDto mealDto;

    private User user;

    private Cart cart;
    private CartDto cartDto;

    @BeforeEach
    void beforeEach() {
        mealDto = dumplingsDto();
        meal = dumplings();

        productDto = bananaDto();
        product = banana();

        user = UserSample.john();

        cartDto = CartSample.cartDto1();
        cart = CartSample.cart1();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Find cart, when found, then return")
    void findCart_whenFound_thenReturn() {
        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);

        final CartDto block = cartFacade.findDtoCartByUserAndDate(cart.getUserId(), cart.getDate()).block();

        assertCartFields(cartDto, block);
        verify(cartService, times(1)).findByUserIdAndDate(user.getId(), cart.getDate());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verifyNoMoreInteractions(cartService, userService, mealService, productService, cartDtoConverter);
    }

    @Test
    @DisplayName("Find cart, when not found, then return")
    void findCart_whenNotFound_thenThrowNotFoundException() {
        when(cartService.findByUserIdAndDate(cartDto.getUserId(), cartDto.getDate())).thenThrow(new NotFoundException(""));

        assertThrows(NotFoundException.class, () -> cartFacade.findDtoCartByUserAndDate(cartDto.getUserId(), cartDto.getDate()).block());

        verify(cartService, times(1)).findByUserIdAndDate(user.getId(), cart.getDate());
        verifyNoMoreInteractions(cartService, userService, mealService, productService, cartDtoConverter);
    }

    @Test
    @DisplayName("Add meal to cart, when cart is empty, then cart should has 1 meal")
    void addMealToCart_whenCartIsEmpty_thenCartShouldHasOneMeal() {
        final var AMOUNT = 100;

        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);
        when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(cartService.save(cart)).thenReturn(Mono.just(cart));

        productDto.setAmount(50);
        product.setAmount(50);
        mealDto.getProducts().add(productDto);
        meal.getProducts().add(product);

        cartDto.getMeals().add(mealDto);
        cartDto.getAllProducts().addAll(mealDto.getProducts());

        final var block = cartFacade.addMealToCart(user.getId(), meal.getId(), cart.getDate(), AMOUNT).block();

        assertCartFields(cartDto, block);
        assertMealFields(cartDto.getMeals().get(0), block.getMeals().get(0));
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verify(mealService, times(1)).findById(meal.getId());
        verify(mealService, times(1)).calculateMealInformation(meal);
        verify(cartService, times(1)).save(cart);
        verifyNoMoreInteractions(cartService, userService, mealService, productService, cartDtoConverter);
    }

    @Test
    @DisplayName("Add meal to cart, when cart is empty, then cart should has 1 meal")
    void addMealToCart_whenThereIsNoCart_thenCreateCartAndAddMeal() {
        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.error(new NotFoundException("")));
        when(userService.findById(cart.getUserId())).thenReturn(Mono.just(user));
        when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);
        when(cartService.save(any(Cart.class))).thenReturn(Mono.just(cart));

        productDto.setAmount(50);
        product.setAmount(50);
        mealDto.getProducts().add(productDto);
        meal.getProducts().add(product);

        cartDto.getMeals().add(mealDto);
        cartDto.getAllProducts().addAll(mealDto.getProducts());

        final var block = cartFacade.addMealToCart(user.getId(), meal.getId(), cart.getDate(), 100).block();

        assertCartFields(cartDto, block);
        verify(userService, times(1)).findById(cart.getUserId());
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verify(mealService, times(1)).findById(meal.getId());
        verify(mealService, times(1)).calculateMealInformation(meal);
        verify(cartService, times(1)).save(any(Cart.class));
        verifyNoMoreInteractions(cartService, userService, mealService, productService, cartDtoConverter);
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
        cartDto.getMeals().add(MealSample.dumplingsDto());
        cartDto.getAllProducts().addAll(mealDto.getProducts());
        cartDto.getAllProducts().addAll(MealSample.dumplingsDto().getProducts());

        var block = cartFacade.addMealToCart(user.getId(), meal.getId(), cart.getDate(), 100).block();

        assertCartFields(cartDto, block);
        verify(cartDtoConverter, times(1)).toDto(cart);
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verify(mealService, times(1)).findById(meal.getId());
        verify(mealService, times(1)).calculateMealInformation(meal);
        verify(cartService, times(1)).save(cart);
        verifyNoMoreInteractions(cartService, userService, mealService, productService, cartDtoConverter);
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

        assertCartFields(cartDto, block);
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verify(mealService, times(1)).findById(meal.getId());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verify(mealService, times(1)).calculateMealInformation(meal);
        verify(cartService, times(1)).save(cart);
        verifyNoMoreInteractions(cartService, userService, mealService, productService, cartDtoConverter);
    }

    @Test
    @DisplayName("Delete meal from cart, when cart had one meal, then cart should be empty")
    void deleteMealFromCart_whenCartHad1Meal_thenCartShouldBeEmpty() {
        final var expected = CartSample.cartDto1();

        when(cartService.findByUserIdAndDate(cartDto.getUserId(), cartDto.getDate())).thenReturn(Mono.just(cart));
        when(cartService.save(cart)).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);

        cart.getMeals().add(meal);

        CartDto block = cartFacade.deleteMealFromCart(cart.getUserId(), meal.getId(), cart.getDate()).block();

        assertCartFields(expected, block);
        verify(cartService, times(1)).findByUserIdAndDate(cartDto.getUserId(), cartDto.getDate());
        verify(cartService, times(1)).save(cart);
        verify(cartDtoConverter, times(1)).toDto(cart);
        verifyNoMoreInteractions(cartService, userService, mealService, productService, cartDtoConverter);
    }

    @Test
    @DisplayName("Delete meal from cart, when cart had no meals, then return empty cart")
    void deleteMealFromCart_whenCartHadNoMeals_thenCartShouldBeEmpty() {
        var expected = CartSample.cartDto1();
        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);

        CartDto block = cartFacade.deleteMealFromCart(cartDto.getUserId(), meal.getId(), cartDto.getDate()).block();

        assertCartFields(expected, block);
        verify(cartService, times(1)).findByUserIdAndDate(cartDto.getUserId(), cartDto.getDate());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verifyNoMoreInteractions(cartService, userService, mealService, productService, cartDtoConverter);
    }

    @Test
    @DisplayName("Add product to cart, when there is no cart, then cart should be created and product should be added")
    void addProductToCart_whenThereIsNoCart_thenCreateCartAndAddProduct() {
        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.error(new NotFoundException("")));
        when(userService.findById(cart.getUserId())).thenReturn(Mono.just(user));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);
        when(productService.findById(product.getId())).thenReturn(Mono.just(product));
        when(cartService.save(any(Cart.class))).thenReturn(Mono.just(cart));

        cartDto.getProducts().add(productDto);
        cartDto.getAllProducts().add(productDto);

        final var block = cartFacade.addProductToCart(user.getId(), product.getId(), cart.getDate(), 100).block();

        assertCartFields(cartDto, block);
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verify(userService, times(1)).findById(cart.getUserId());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verify(productService, times(1)).findById(product.getId());
        verify(cartService, times(1)).save(any(Cart.class));
        verify(productService, times(1)).calculateProductInfoByAmount(product);
        verifyNoMoreInteractions(cartService, userService, mealService, productService, cartDtoConverter);
    }

    @Test
    @DisplayName("Add product to cart, when cart is empty, then create cart and add product")
    void addProductToCart_whenCartIsEmpty_thenCartShouldHasOneProduct() {
        when(cartService.findByUserIdAndDate(user.getId(), cart.getDate())).thenReturn(Mono.just(cart));
        when(productService.findById(product.getId())).thenReturn(Mono.just(product));
        when(productService.calculateProductInfoByAmount(product)).thenReturn(product);
        when(cartService.save(any(Cart.class))).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);

        cartDto.getProducts().add(productDto);
        cartDto.getAllProducts().add(productDto);

        final var block = cartFacade.addProductToCart(user.getId(), product.getId(), cart.getDate(), 100).block();

        assertCartFields(cartDto, block);
        verify(cartService, times(1)).findByUserIdAndDate(user.getId(), cart.getDate());
        verify(productService, times(1)).findById(product.getId());
        verify(productService, times(1)).calculateProductInfoByAmount(product);
        verify(cartService, times(1)).save(any(Cart.class));
        verify(cartDtoConverter, times(1)).toDto(cart);
        verifyNoMoreInteractions(cartService, userService, mealService, productService, cartDtoConverter);
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
        cartDto.getProducts().add(ProductSample.breadDto());
        cartDto.getAllProducts().add(productDto);
        cartDto.getAllProducts().add(ProductSample.breadDto());

        var block = cartFacade.addProductToCart(user.getId(), product.getId(), cart.getDate(), 100).block();

        assertCartFields(cartDto, block);
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verify(productService, times(1)).findById(product.getId());
        verify(cartService, times(1)).save(cart);
        verify(productService, times(1)).calculateProductInfoByAmount(product);
        verifyNoMoreInteractions(cartService, userService, mealService, productService, cartDtoConverter);
    }

    @Test
    @DisplayName("Add product to cart, when cart had one product, then cart should has 2 products")
    void addProductToCart_whenCartHadTheSameOneProduct_thenCartShouldHasOneProductWithSum() {
        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.just(cart));
        when(productService.findById(product.getId())).thenReturn(Mono.just(product));
        when(cartService.save(cart)).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);

        product.setAmount(100);
        productDto.setAmount(200);
        cart.getProducts().add(product);
        cartDto.getProducts().add(productDto);
        cartDto.getAllProducts().add(productDto);
        cartDto.getAllProducts().add(ProductSample.breadDto());

        var block = cartFacade.addProductToCart(user.getId(), product.getId(), cart.getDate(), 100).block();

        assertCartFields(cartDto, block);
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verify(productService, times(1)).findById(product.getId());
        verify(cartService, times(1)).save(cart);
        verify(productService, times(1)).calculateProductInfoByAmount(product);
        verifyNoMoreInteractions(cartService, userService, mealService, productService, cartDtoConverter);
    }

    @Test
    @DisplayName("Add product to cart, when cart had one meal, then cart should has one product and one meal")
    void addProductToCart_whenCartHadOneMeal_thenCartShouldHasOneMealAndOneProduct() {
        final int AMOUNT = 100;
        meal.setAmount(AMOUNT);
        mealDto.setAmount(AMOUNT);
        product.setAmount(AMOUNT);
        productDto.setAmount(AMOUNT);

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

        assertCartFields(cartDto, block);
        verify(cartDtoConverter, times(1)).toDto(cart);
        verify(productService, times(1)).calculateProductInfoByAmount(product);
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verify(productService, times(1)).findById(product.getId());
        verify(cartService, times(1)).save(cart);
        verifyNoMoreInteractions(cartService, userService, mealService, productService, cartDtoConverter);
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

        assertCartFields(expected, block);
        verify(cartDtoConverter, times(1)).toDto(cart);
        verify(productService, times(1)).findById(product.getId());
        verify(cartService, times(1)).save(cart);
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verifyNoMoreInteractions(cartService, userService, mealService, productService, cartDtoConverter);
    }

    @Test
    @DisplayName("Delete product from cart, when cart had no products, then cart should be empty")
    void deleteProductFromCart_whenCartHadNoProducts_thenCartShouldBeEmpty() {
        when(productService.findById(product.getId())).thenReturn(Mono.just(product));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);
        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.just(cart));
        when(cartService.save(cart)).thenReturn(Mono.just(cart));

        CartDto actual = cartFacade.deleteProductFromCart(user.getId(), product.getId(), cart.getDate()).block();

        assertCartFields(cartDto, actual);
        verify(productService, times(1)).findById(product.getId());
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verifyNoMoreInteractions(cartService, userService, mealService, productService, cartDtoConverter);
    }
}
