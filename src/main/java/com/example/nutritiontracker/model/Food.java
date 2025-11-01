package com.example.nutritiontracker.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;

/**
 * Immutable representation of a reusable food item with nutritional values.
 * Used for building a personal food database.
 *
 * RI:
 * - {@code name} is non-null/non-blank.
 * - Nutrient values and calories are all non-negative.
 *
 * AF(this) = A reusable food entry that can be selected to create meals quickly.
 */
public final class Food implements Serializable {
    private static final long serialVersionUID = 2L;

    private final String name;
    private final double protein;
    private final double carbs;
    private final double fat;
    private final double calories;

    /**
     * Creates a reusable food item.
     *
     * @param name the food name
     * @param protein grams of protein
     * @param carbs grams of carbohydrates
     * @param fat grams of fat
     * @param calories total calories
     * @requires {@code name} is non-null/non-blank and all nutrient values are {@code >= 0}
     * @effects initializes an immutable food item
     */
    public Food(String name, double protein, double carbs, double fat, double calories) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Food name cannot be empty.");
        }
        if (protein < 0 || carbs < 0 || fat < 0 || calories < 0) {
            throw new IllegalArgumentException("Nutritional values must be non-negative.");
        }

        this.name = name;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.calories = calories;
    }

    /**
     * Returns the food name.
     *
     * @return food name
     * @requires nothing
     * @effects does not modify this food
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the protein grams.
     *
     * @return protein grams
     * @requires nothing
     * @effects does not modify this food
     */
    public double getProtein() {
        return protein;
    }

    /**
     * Returns the carbohydrate grams.
     *
     * @return carbohydrate grams
     * @requires nothing
     * @effects does not modify this food
     */
    public double getCarbs() {
        return carbs;
    }

    /**
     * Returns the fat grams.
     *
     * @return fat grams
     * @requires nothing
     * @effects does not modify this food
     */
    public double getFat() {
        return fat;
    }

    /**
     * Returns the calories.
     *
     * @return calorie total
     * @requires nothing
     * @effects does not modify this food
     */
    public double getCalories() {
        return calories;
    }

    /**
     * Creates a meal from this food item.
     *
     * @return new {@link Meal} with this food's nutritional values
     * @requires nothing
     * @effects creates a new meal for today
     */
    public Meal toMeal() {
        return new Meal(name, protein, carbs, fat, calories);
    }

    /**
     * Returns a formatted summary of this food.
     *
     * @return formatted string containing name and nutrient values
     * @requires nothing
     * @effects does not modify this food
     */
    @Override
    public String toString() {
        return String.format("%s (P:%.1fg, C:%.1fg, F:%.1fg, Cal:%.0f)",
            name, protein, carbs, fat, calories);
    }

    /**
     * Compares this food to another object for equality based on name.
     *
     * @param o object to compare
     * @return {@code true} when {@code o} is a {@code Food} with the same name; otherwise {@code false}
     * @requires nothing
     * @effects does not modify either object
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Food food = (Food) o;
        return name.equalsIgnoreCase(food.name);
    }

    /**
     * Computes the hash code using this food's name.
     *
     * @return hash code for this food
     * @requires nothing
     * @effects does not modify this food
     */
    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase());
    }
    
    /**
     * Custom serialization method to ensure proper saving of food objects.
     * 
     * @param out the output stream
     * @throws IOException if an I/O error occurs
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }
    
    /**
     * Custom deserialization method to ensure proper loading of food objects.
     * 
     * @param in the input stream
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if the class of a serialized object cannot be found
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }
}
