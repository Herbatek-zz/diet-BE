package com.piotrek.diet.cart;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.piotrek.diet.DietApplication;
import com.piotrek.diet.helpers.config.DataBaseForIntegrationTestsConfiguration;
import com.piotrek.diet.helpers.exceptions.GlobalExceptionHandler;
import com.piotrek.diet.meal.MealDtoConverter;
import com.piotrek.diet.meal.MealService;
import com.piotrek.diet.product.ProductService;
import com.piotrek.diet.sample.CartSample;
import com.piotrek.diet.sample.MealSample;
import com.piotrek.diet.sample.ProductSample;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {DietApplication.class, DataBaseForIntegrationTestsConfiguration.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CartControllerTest {


    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    @Autowired
    private CartFacade cartFacade;

    @Autowired
    private CartService cartService;

    @Autowired
    private CartDtoConverter cartDtoConverter;

    @Autowired
    private MealDtoConverter mealDtoConverter;

    @Autowired
    private MealService mealService;

    @Autowired
    private ProductService productService;

    private WebTestClient webTestClient;

    @BeforeEach
    void beforeEach() {
        providePrincipal();

        cartService.deleteAll().block();
        webTestClient = WebTestClient
                .bindToController(new CartController(cartFacade))
                .controllerAdvice(globalExceptionHandler)
                .build();
    }

    @AfterAll
    void afterAll() {
        cartService.deleteAll().block();
    }

    @Test
    @DisplayName("Add meal to cart, when cart is empty, then cart has one meal")
    void addMealToCart_whenCartIsEmpty_thenCartHasOneMeal() throws JsonProcessingException {
        var mealToAdd = MealSample.coffeeWithoutId();
        mealToAdd.getProducts().add(ProductSample.breadWithId());
        mealToAdd = mealService.save(mealToAdd).block();

        var cart = cartService.save(CartSample.cart1()).block();
        var expected = cartDtoConverter.toDto(cart);
        expected.getMeals().add(mealDtoConverter.toDto(mealToAdd));
        expected.getAllProducts().addAll(mealDtoConverter.toDto(mealToAdd).getProducts());

        final var URI = "/carts/" + cart.getId() + "/meals/" + mealToAdd.getId();

        CartDto responseBody = webTestClient.put().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertAll(
                () -> assertEquals(expected.getId(), responseBody.getId()),
                () -> assertEquals(expected.getUserId(), responseBody.getUserId()),
                () -> assertEquals(expected.getMeals(), responseBody.getMeals()),
                () -> assertEquals(1, responseBody.getMeals().size()),
                () -> assertEquals(expected.getProducts(), responseBody.getProducts()),
                () -> assertEquals(expected.getAllProducts(), responseBody.getAllProducts()),
                () -> assertEquals(1, responseBody.getAllProducts().size()),
                () -> assertEquals(expected.getDate(), responseBody.getDate())
        );
    }

    @Test
    @DisplayName("Add meal to cart, when cart has one meal, then cart has two meal")
    void addMealToCart_whenCartHasOneMeal_thenCartHasTwoMeals() {
        var mealToAdd = MealSample.dumplingsWithId();
        mealToAdd.getProducts().add(ProductSample.breadWithId());
        mealToAdd.getProducts().add(ProductSample.bananaWithId());
        mealToAdd = mealService.save(mealToAdd).block();

        var mealInCart = MealSample.coffeeWithId();
        mealInCart.getProducts().add(ProductSample.bananaWithId());

        var cart = CartSample.cart1();
        cart.getMeals().add(mealInCart);
        cart = cartService.save(cart).block();

        var expected = CartSample.cartDto1();
        expected.getMeals().add(mealDtoConverter.toDto(mealInCart));
        expected.getMeals().add(mealDtoConverter.toDto(mealToAdd));
        expected.getAllProducts().add(ProductSample.breadWithIdDto());
        var duplicatedProduct = ProductSample.bananaWithIdDto();
        duplicatedProduct.setAmount(duplicatedProduct.getAmount() * 2);
        expected.getAllProducts().add(duplicatedProduct);

        final var URI = "/carts/" + cart.getId() + "/meals/" + mealToAdd.getId();


        CartDto responseBody = webTestClient.put().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertAll(
                () -> assertEquals(expected.getId(), responseBody.getId()),
                () -> assertEquals(expected.getUserId(), responseBody.getUserId()),
                () -> assertEquals(expected.getMeals(), responseBody.getMeals()),
                () -> assertEquals(2, responseBody.getMeals().size()),
                () -> assertEquals(expected.getProducts(), responseBody.getProducts()),
                () -> assertEquals(expected.getAllProducts(), responseBody.getAllProducts()),
                () -> assertEquals(2, expected.getAllProducts().size()),
                () -> assertEquals(expected.getDate(), responseBody.getDate())
        );
    }

    @Test
    @DisplayName("Add meal to cart, when cart has one product, then cart has one meal and one product")
    void addMealToCart_whenCartHasOneProduct_thenCartHasOneMealAndOneProduct() {
        var mealToAdd = MealSample.dumplingsWithId();
        mealToAdd.getProducts().add(ProductSample.breadWithId());
        mealToAdd.getProducts().add(ProductSample.bananaWithId());
        mealToAdd = mealService.save(mealToAdd).block();

        var productInCart = ProductSample.bananaWithId();

        var cart = CartSample.cart1();
        cart.getProducts().add(productInCart);
        cart = cartService.save(cart).block();

        var expected = CartSample.cartDto1();
        expected.getMeals().add(mealDtoConverter.toDto(mealToAdd));
        expected.getProducts().add(ProductSample.bananaWithIdDto());
        expected.getAllProducts().add(ProductSample.breadWithIdDto());
        var duplicatedProduct = ProductSample.bananaWithIdDto();
        duplicatedProduct.setAmount(duplicatedProduct.getAmount() * 2);
        expected.getAllProducts().add(duplicatedProduct);

        final var URI = "/carts/" + cart.getId() + "/meals/" + mealToAdd.getId();


        CartDto responseBody = webTestClient.put().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertAll(
                () -> assertEquals(expected.getId(), responseBody.getId()),
                () -> assertEquals(expected.getUserId(), responseBody.getUserId()),
                () -> assertEquals(expected.getMeals(), responseBody.getMeals()),
                () -> assertEquals(1, responseBody.getMeals().size()),
                () -> assertEquals(expected.getProducts(), responseBody.getProducts()),
                () -> assertEquals(1, responseBody.getProducts().size()),
                () -> assertEquals(expected.getAllProducts(), responseBody.getAllProducts()),
                () -> assertEquals(2, expected.getAllProducts().size()),
                () -> assertEquals(expected.getDate(), responseBody.getDate())
        );
    }

    @Test
    @DisplayName("Add product to cart, when cart is empty, then cart has one product")
    void addProductToCart_whenCartIsEmpty_thenCartHasOneProduct() {
        var productToAdd = productService.save(ProductSample.bananaWithId()).block();

        var cart = cartService.save(CartSample.cart1()).block();

        var expected = CartSample.cartDto1();
        expected.getProducts().add(ProductSample.bananaWithIdDto());
        expected.getAllProducts().add(ProductSample.bananaWithIdDto());

        final var URI = "/carts/" + cart.getId() + "/products/" + productToAdd.getId();


        CartDto responseBody = webTestClient.put().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertAll(
                () -> assertEquals(expected.getId(), responseBody.getId()),
                () -> assertEquals(expected.getUserId(), responseBody.getUserId()),
                () -> assertEquals(expected.getMeals(), responseBody.getMeals()),
                () -> assertEquals(0, responseBody.getMeals().size()),
                () -> assertEquals(expected.getProducts(), responseBody.getProducts()),
                () -> assertEquals(1, responseBody.getProducts().size()),
                () -> assertEquals(expected.getAllProducts(), responseBody.getAllProducts()),
                () -> assertEquals(1, expected.getAllProducts().size()),
                () -> assertEquals(expected.getDate(), responseBody.getDate())
        );
    }

    @Test
    @DisplayName("Add product to cart, when cart has one product, then cart has two products")
    void addProductToCart_whenCartHasOneProduct_thenCartHasTwoProducts() {
        var productToAdd = productService.save(ProductSample.bananaWithId()).block();
        var productInCart = productService.save(ProductSample.breadWithId()).block();
        var cart = CartSample.cart1();
        cart.getProducts().add(productInCart);
        cart = cartService.save(cart).block();

        var expected = CartSample.cartDto1();
        expected.getProducts().add(ProductSample.breadWithIdDto());
        expected.getProducts().add(ProductSample.bananaWithIdDto());
        expected.getAllProducts().add(ProductSample.breadWithIdDto());
        expected.getAllProducts().add(ProductSample.bananaWithIdDto());


        final var URI = "/carts/" + cart.getId() + "/products/" + productToAdd.getId();


        CartDto responseBody = webTestClient.put().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertAll(
                () -> assertEquals(expected.getId(), responseBody.getId()),
                () -> assertEquals(expected.getUserId(), responseBody.getUserId()),
                () -> assertEquals(expected.getMeals(), responseBody.getMeals()),
                () -> assertEquals(0, responseBody.getMeals().size()),
                () -> assertEquals(expected.getProducts(), responseBody.getProducts()),
                () -> assertEquals(2, responseBody.getProducts().size()),
                () -> assertEquals(expected.getAllProducts(), responseBody.getAllProducts()),
                () -> assertEquals(2, expected.getAllProducts().size()),
                () -> assertEquals(expected.getDate(), responseBody.getDate())
        );
    }

    @Test
    @DisplayName("Add product to cart, when cart has one meal, then cart has one meal and one product")
    void addProductToCart_whenCartHasOneMeal_thenCartHasOneMealAndOneProduct() {
        var productToAdd = productService.save(ProductSample.bananaWithId()).block();

        var mealInCart = MealSample.coffeeWithId();
        mealInCart.getProducts().add(ProductSample.bananaWithId());

        var cart = CartSample.cart1();
        cart.getMeals().add(mealInCart);
        cart = cartService.save(cart).block();

        var expected = CartSample.cartDto1();
        expected.getMeals().add(mealDtoConverter.toDto(mealInCart));
        expected.getProducts().add(ProductSample.bananaWithIdDto());
        var duplicatedProduct = ProductSample.bananaWithIdDto();
        duplicatedProduct.setAmount(duplicatedProduct.getAmount() * 2);
        expected.getAllProducts().add(duplicatedProduct);

        final var URI = "/carts/" + cart.getId() + "/products/" + productToAdd.getId();


        CartDto responseBody = webTestClient.put().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertAll(
                () -> assertEquals(expected.getId(), responseBody.getId()),
                () -> assertEquals(expected.getUserId(), responseBody.getUserId()),
                () -> assertEquals(expected.getMeals(), responseBody.getMeals()),
                () -> assertEquals(1, responseBody.getMeals().size()),
                () -> assertEquals(expected.getProducts(), responseBody.getProducts()),
                () -> assertEquals(1, responseBody.getProducts().size()),
                () -> assertEquals(expected.getAllProducts(), responseBody.getAllProducts()),
                () -> assertEquals(1, expected.getAllProducts().size()),
                () -> assertEquals(expected.getDate(), responseBody.getDate())
        );
    }

    @Test
    @DisplayName("Delete meal from cart, when cart has one meal, then cart is empty")
    void deleteMealFromCart_whenCartHasOneMeal_thenCartIsEmpty() {
        var mealToDelete = MealSample.coffeeWithId();
        mealToDelete.getProducts().add(ProductSample.bananaWithId());
        mealToDelete = mealService.save(mealToDelete).block();

        var cart = CartSample.cart1();
        cart.getMeals().add(mealToDelete);
        cartService.save(cart).block();

        final var URI = "/carts/" + cart.getId() + "/meals/" + mealToDelete.getId();

        CartDto responseBody = webTestClient.delete().uri(URI)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertAll(
                () -> assertEquals(cart.getId(), responseBody.getId()),
                () -> assertEquals(cart.getUserId(), responseBody.getUserId()),
                () -> assertEquals(0, responseBody.getMeals().size()),
                () -> assertEquals(0, responseBody.getProducts().size()),
                () -> assertEquals(0, responseBody.getAllProducts().size()),
                () -> assertEquals(cart.getDate(), responseBody.getDate())
        );
    }

    @Test
    @DisplayName("Delete meal from cart, when cart has two meals, then cart has one meal")
    void deleteMealFromCart_whenCartHasTwoMeals_thenCartHasOneMeal() {
        var mealToDelete = MealSample.coffeeWithId();
        mealToDelete.getProducts().add(ProductSample.bananaWithId());
        mealToDelete = mealService.save(mealToDelete).block();

        var mealInCart = MealSample.dumplingsWithId();
        mealInCart.getProducts().add(ProductSample.breadWithId());
        mealInCart = mealService.save(mealInCart).block();

        var cart = CartSample.cart1();
        cart.getMeals().add(mealToDelete);
        cart.getMeals().add(mealInCart);
        cartService.save(cart).block();

        final var URI = "/carts/" + cart.getId() + "/meals/" + mealToDelete.getId();

        CartDto responseBody = webTestClient.delete().uri(URI)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertAll(
                () -> assertEquals(cart.getId(), responseBody.getId()),
                () -> assertEquals(cart.getUserId(), responseBody.getUserId()),
                () -> assertEquals(1, responseBody.getMeals().size()),
                () -> assertEquals(0, responseBody.getProducts().size()),
                () -> assertEquals(1, responseBody.getAllProducts().size()),
                () -> assertEquals(cart.getDate(), responseBody.getDate())
        );
    }

    @Test
    @DisplayName("Delete meal form cart, when cart has one product and one meal, then cart has one product")
    void deleteMealFromCart_whenCartHasOneProductAndOneMeal_thenCartHasOneProduct() {
        var mealToDelete = MealSample.coffeeWithId();
        mealToDelete.getProducts().add(ProductSample.bananaWithId());
        mealToDelete = mealService.save(mealToDelete).block();

        var productInCart = productService.save(ProductSample.breadWithId()).block();

        var cart = CartSample.cart1();
        cart.getMeals().add(mealToDelete);
        cart.getProducts().add(productInCart);
        cartService.save(cart).block();

        final var URI = "/carts/" + cart.getId() + "/meals/" + mealToDelete.getId();

        CartDto responseBody = webTestClient.delete().uri(URI)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertAll(
                () -> assertEquals(cart.getId(), responseBody.getId()),
                () -> assertEquals(cart.getUserId(), responseBody.getUserId()),
                () -> assertEquals(0, responseBody.getMeals().size()),
                () -> assertEquals(1, responseBody.getProducts().size()),
                () -> assertEquals(1, responseBody.getAllProducts().size()),
                () -> assertEquals(cart.getDate(), responseBody.getDate())
        );
    }

    @Test
    @DisplayName("Delete meal from cart, when cart is empty, then cart is still empty")
    void deleteMealFromCart_whenCartIsEmpty_thenCartIsStillEmpty() {
        var mealToDelete = MealSample.coffeeWithId();
        mealToDelete.getProducts().add(ProductSample.bananaWithId());
        mealToDelete = mealService.save(mealToDelete).block();

        var cart = cartService.save(CartSample.cart1()).block();

        final var URI = "/carts/" + cart.getId() + "/meals/" + mealToDelete.getId();

        CartDto responseBody = webTestClient.delete().uri(URI)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertAll(
                () -> assertEquals(cart.getId(), responseBody.getId()),
                () -> assertEquals(cart.getUserId(), responseBody.getUserId()),
                () -> assertEquals(0, responseBody.getMeals().size()),
                () -> assertEquals(0, responseBody.getProducts().size()),
                () -> assertEquals(0, responseBody.getAllProducts().size()),
                () -> assertEquals(cart.getDate(), responseBody.getDate())
        );
    }

    @Test
    @DisplayName("Delete product from cart, when cart has one product, then cart is empty")
    void deleteProductFromCart_whenCartHasOneProduct_thenCartIsEmpty() {
        var productToDelete = productService.save(ProductSample.breadWithId()).block();

        var cart = CartSample.cart1();
        cart.getProducts().add(productToDelete);
        cartService.save(cart).block();

        final var URI = "/carts/" + cart.getId() + "/products/" + productToDelete.getId();

        CartDto responseBody = webTestClient.delete().uri(URI)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertAll(
                () -> assertEquals(cart.getId(), responseBody.getId()),
                () -> assertEquals(cart.getUserId(), responseBody.getUserId()),
                () -> assertEquals(0, responseBody.getMeals().size()),
                () -> assertEquals(0, responseBody.getProducts().size()),
                () -> assertEquals(0, responseBody.getAllProducts().size()),
                () -> assertEquals(cart.getDate(), responseBody.getDate())
        );
    }

    @Test
    @DisplayName("Delete product from cart, when cart has two products, then cart has one product")
    void deleteProductFromCart_whenCartHasTwoProducts_thenCartHasOneProduct() {
        var productToDelete = productService.save(ProductSample.breadWithId()).block();

        var productInCart = productService.save(ProductSample.bananaWithId()).block();

        var cart = CartSample.cart1();
        cart.getProducts().add(productToDelete);
        cart.getProducts().add(productInCart);
        cartService.save(cart).block();

        final var URI = "/carts/" + cart.getId() + "/products/" + productToDelete.getId();

        CartDto responseBody = webTestClient.delete().uri(URI)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertAll(
                () -> assertEquals(cart.getId(), responseBody.getId()),
                () -> assertEquals(cart.getUserId(), responseBody.getUserId()),
                () -> assertEquals(0, responseBody.getMeals().size()),
                () -> assertEquals(1, responseBody.getProducts().size()),
                () -> assertEquals(1, responseBody.getAllProducts().size()),
                () -> assertEquals(cart.getDate(), responseBody.getDate())
        );
    }

    @Test
    @DisplayName("Delete product form cart, when cart has one product and one meal, then cart has one meal")
    void deleteProductFromCart_whenCartHasOneProductAndOneMeal_thenCartHasOneMeal() {
        var mealInCart = MealSample.coffeeWithId();
        mealInCart.getProducts().add(ProductSample.bananaWithId());
        mealInCart = mealService.save(mealInCart).block();

        var productToDelete = productService.save(ProductSample.breadWithId()).block();

        var cart = CartSample.cart1();
        cart.getMeals().add(mealInCart);
        cart.getProducts().add(productToDelete);
        cartService.save(cart).block();

        final var URI = "/carts/" + cart.getId() + "/products/" + productToDelete.getId();

        CartDto responseBody = webTestClient.delete().uri(URI)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertAll(
                () -> assertEquals(cart.getId(), responseBody.getId()),
                () -> assertEquals(cart.getUserId(), responseBody.getUserId()),
                () -> assertEquals(1, responseBody.getMeals().size()),
                () -> assertEquals(0, responseBody.getProducts().size()),
                () -> assertEquals(1, responseBody.getAllProducts().size()),
                () -> assertEquals(cart.getDate(), responseBody.getDate())
        );
    }

    @Test
    @DisplayName("Delete product from cart, when cart is empty, then cart is still empty")
    void deleteProductFromCart_whenCartIsEmpty_thenCartIsStillEmpty() {
        var productToDelete = productService.save(ProductSample.breadWithId()).block();

        var cart = cartService.save(CartSample.cart1()).block();

        final var URI = "/carts/" + cart.getId() + "/products/" + productToDelete.getId();

        CartDto responseBody = webTestClient.delete().uri(URI)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertAll(
                () -> assertEquals(cart.getId(), responseBody.getId()),
                () -> assertEquals(cart.getUserId(), responseBody.getUserId()),
                () -> assertEquals(0, responseBody.getMeals().size()),
                () -> assertEquals(0, responseBody.getProducts().size()),
                () -> assertEquals(0, responseBody.getAllProducts().size()),
                () -> assertEquals(cart.getDate(), responseBody.getDate())
        );
    }

    private void providePrincipal() {
        var testingAuthentication = new TestingAuthenticationToken(CartSample.cart1().getUserId(), null);
        SecurityContextHolder.getContext().setAuthentication(testingAuthentication);
    }

}
