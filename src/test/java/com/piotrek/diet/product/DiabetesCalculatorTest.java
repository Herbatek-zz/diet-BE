package com.piotrek.diet.product;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DiabetesCalculatorTest {

    private DiabetesCalculator calculator = new DiabetesCalculator();

    @Test
    void calculateCarbohydrateExchange_whenArgumentsAre0And0_thenReturn0() {
        double result = calculator.calculateCarbohydrateExchange(0, 0);
        assertEquals(0, result);
    }

    @Test
    void calculateCarbohydrateExchange_whenTheSameArguments_thenReturn0() {
        double result = calculator.calculateCarbohydrateExchange(10, 10);
        assertEquals(0, result);
    }

    @Test
    void calculateCarbohydrateExchange_whenCarbohydrateIsLowerThanFibre_thenReturnNegativeNumber() {
        double result = calculator.calculateCarbohydrateExchange(5, 10);
        assertTrue(result < 0);
    }

    @Test
    void calculateCarbohydrateExchange_whenArguments10And2_thenCalculateResult() {
        double result = calculator.calculateCarbohydrateExchange(10, 2);
        assertEquals(0.8, result);
    }

    @Test
    void calculateProteinAndFatEquivalent_whenArguments0And0_thenReturn0() {
        double result = calculator.calculateProteinAndFatEquivalent(0,  0);
        assertEquals(0, result);
    }

    @Test
    void calculateProteinAndFatEquivalent_whenArguments10And0_thenReturnResult() {
        double result = calculator.calculateProteinAndFatEquivalent(10,  0);
        assertEquals(0.4, result);
    }

    @Test
    void calculateProteinAndFatEquivalent_whenArguments0And10_thenReturnResult() {
        double result = calculator.calculateProteinAndFatEquivalent(0,  10);
        assertEquals(0.9, result);
    }

    @Test
    void calculateProteinAndFatEquivalent_whenArguments10And10_thenReturnResult() {
        double result = calculator.calculateProteinAndFatEquivalent(10,  10);
        assertEquals(1.3, result);
    }
}