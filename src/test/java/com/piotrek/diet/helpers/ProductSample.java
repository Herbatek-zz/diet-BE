package com.piotrek.diet.helpers;

import com.piotrek.diet.product.Product;
import com.piotrek.diet.product.ProductDto;

import java.util.UUID;

public class ProductSample {

    private static DiabetesCalculator diabetesCalculator = new DiabetesCalculator();

    private final static String BANANA_ID = UUID.randomUUID().toString();
    private final static String BREAD_ID = UUID.randomUUID().toString();
    public final static String BANANA = "banana";
    public final static String BREAD = "bread";

    public static Product bananaWithId() {
        var product = bananaWithoutId();
        product.setId(BANANA_ID);
        return product;
    }

    public static Product bananaWithoutId() {
        var product = new Product();
        product.setName("Banana");
        product.setDescription("Very yellow, so sweet, such tasty");
        product.setImageUrl("http://bananaWithId-so-good.com");
        product.setProtein(1.0);
        product.setCarbohydrate(21.8);
        product.setFat(0.3);
        product.setFibre(1.7);
        product.setKcal(97.0);
        product.setAmount(100);
        product.setUserId(UserSample.johnWithId().getId());
        product.setCarbohydrateExchange(diabetesCalculator.calculateCarbohydrateExchange(product.getCarbohydrate(), product.getFibre()));
        product.setProteinAndFatEquivalent(diabetesCalculator.calculateProteinAndFatEquivalent(product.getProtein(), product.getFat()));
        return product;
    }

    public static ProductDto bananaWithIdDto() {
        var product = bananaWithoutIdDto();
        product.setId(BANANA_ID);
        return product;
    }

    public static ProductDto bananaWithoutIdDto() {
        var productDto = new ProductDto();
        productDto.setName("Banana");
        productDto.setDescription("Very yellow, so sweet, such tasty");
        productDto.setImageUrl("http://bananaWithId-so-good.com");
        productDto.setProtein(1.0);
        productDto.setCarbohydrate(21.8);
        productDto.setFat(0.3);
        productDto.setFibre(1.7);
        productDto.setKcal(97.0);
        productDto.setAmount(100);
        productDto.setUserId(UserSample.johnWithId().getId());
        productDto.setCarbohydrateExchange(diabetesCalculator.calculateCarbohydrateExchange(productDto.getCarbohydrate(), productDto.getFibre()));
        productDto.setProteinAndFatEquivalent(diabetesCalculator.calculateProteinAndFatEquivalent(productDto.getProtein(), productDto.getFat()));
        return productDto;
    }

    public static Product breadWithId() {
        var product = breadWithoutId();
        product.setId(BREAD_ID);
        return product;
    }

    public static Product breadWithoutId() {
        var product = new Product();
        product.setName("Tesco Chleb Razowy");
        product.setDescription("gwarancja 100% satysfakcji, 100% żytni, wypiekany na naturalnym zakwasie, bez substancji konserwujących i polepszaczy, Jestem z Polski");
        product.setImageUrl("https://secure.ce-tescoassets.com/assets/PL/924/5051007036924/ShotType1_328x328.jpg");
        product.setProtein(4.9);
        product.setCarbohydrate(43.0);
        product.setFat(1.6);
        product.setAmount(100);
        product.setFibre(0.0);
        product.setKcal(216.0);
        product.setCarbohydrateExchange(diabetesCalculator.calculateCarbohydrateExchange(product.getCarbohydrate(), product.getFibre()));
        product.setProteinAndFatEquivalent(diabetesCalculator.calculateProteinAndFatEquivalent(product.getProtein(), product.getFat()));
        return product;
    }

    public static ProductDto breadWithIdDto() {
        var product = breadWithoutIdDto();
        product.setId(BREAD_ID);
        return product;
    }

    public static ProductDto breadWithoutIdDto() {
        var productDto = new ProductDto();
        productDto.setName("Tesco Chleb Razowy");
        productDto.setDescription("gwarancja 100% satysfakcji, 100% żytni, wypiekany na naturalnym zakwasie, bez substancji konserwujących i polepszaczy, Jestem z Polski");
        productDto.setImageUrl("https://secure.ce-tescoassets.com/assets/PL/924/5051007036924/ShotType1_328x328.jpg");
        productDto.setProtein(4.9);
        productDto.setCarbohydrate(43.0);
        productDto.setFat(1.6);
        productDto.setFibre(0.0);
        productDto.setAmount(100);
        productDto.setKcal(216.0);
        productDto.setCarbohydrateExchange(diabetesCalculator.calculateCarbohydrateExchange(productDto.getCarbohydrate(), productDto.getFibre()));
        productDto.setProteinAndFatEquivalent(diabetesCalculator.calculateProteinAndFatEquivalent(productDto.getProtein(), productDto.getFat()));
        return productDto;
    }
}
