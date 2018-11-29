package com.piotrek.diet.meal;

import com.piotrek.diet.helpers.BaseEntity;
import com.piotrek.diet.product.Product;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Data
@Document
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, of = {})
public class Meal extends BaseEntity {

    @NotNull
    private String name;

    @NotNull
    private String description;

    @NotNull
    private String recipe;

    private double protein;

    private double carbohydrate;

    private double fat;

    private double fibre;

    private double kcal;

    private int amount;

    private MultipartFile imageUrl;

    private double carbohydrateExchange;

    private double proteinAndFatEquivalent;

    private ArrayList<Product> products = new ArrayList<>();

    @NotNull
    private String userId;

    private AtomicLong favouriteCounter = new AtomicLong(0);

    public Meal(String id) {
        super(id);
    }
}
