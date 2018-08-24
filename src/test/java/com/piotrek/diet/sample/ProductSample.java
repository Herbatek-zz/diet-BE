package com.piotrek.diet.sample;

import com.piotrek.diet.product.Product;

public class ProductSample {

    public static Product bananaWithId() {
        var product = new Product();
        product.setId("ProductWow123");
        product.setName("Banana");
        product.setDescription("Very yellow, so sweet, such tasty");
        product.setImageUrl("http://bananaWithId-so-good.com");
        product.setProtein(1.0);
        product.setCarbohydrate(21.8);
        product.setFat(0.3);
        product.setFibre(1.7);
        product.setKcal(97.0);
        product.setCarbohydrateExchange(2.1);
        product.setProteinAndFatEquivalent(0.067);
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
        product.setFibre(0.0);
        product.setKcal(216.0);
        return product;
    }
}
