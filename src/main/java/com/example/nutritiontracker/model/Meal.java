package com.example.nutritiontracker.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Mutable representation of a meal with nutritional values and a category tag.
 *
 * RI:
 * - {@code name} is non-null/non-blank.
 * - {@code categoryTag} is non-null.
 * - Nutrient values and calories are all non-negative.
 * - {@code consumedOn} is non-null.
 *
 * AF(this) = A logged meal entry identified by {@code id} that contributes the stored
 * nutritional values to the owning {@link MealLog}.
 */
public class Meal implements Serializable {
    private static final long serialVersionUID = 1L;

    private final MealID id;
    private String name;
    private String categoryTag;
    private double protein;
    private double carbs;
    private double fat;
    private double calories;
    private LocalDate consumedOn;

    /**
     * Creates a meal entry for today using the default {@code #uncategorized} tag.
     *
     * @param name the meal name
     * @param protein grams of protein
     * @param carbs grams of carbohydrates
     * @param fat grams of fat
     * @param calories total calories
     * @requires {@code name} is non-null/non-blank and all nutrient values are {@code >= 0}
     * @effects initializes this meal with a generated {@link MealID} and today's date
     */
    public Meal(String name, double protein, double carbs, double fat, double calories) {
        this(name, "#uncategorized", protein, carbs, fat, calories, LocalDate.now());
    }

    /**
     * Creates a meal entry for a specific consumption date with the default category tag.
     *
     * @param name the meal name
     * @param protein grams of protein
     * @param carbs grams of carbohydrates
     * @param fat grams of fat
     * @param calories total calories
     * @param consumedOn the date the meal was consumed
     * @requires {@code name} is non-null/non-blank, nutrients {@code >= 0}, and {@code consumedOn} non-null
     * @effects initializes this meal with a generated {@link MealID} and the supplied date
     */
    public Meal(String name, double protein, double carbs, double fat, double calories, LocalDate consumedOn) {
        this(name, "#uncategorized", protein, carbs, fat, calories, consumedOn);
    }

    /**
     * Creates a meal entry with an explicit category tag for today.
     *
     * @param name the meal name
     * @param categoryTag tag categorising the meal (e.g. {@code #protein})
     * @param protein grams of protein
     * @param carbs grams of carbohydrates
     * @param fat grams of fat
     * @param calories total calories
     * @requires {@code name} is non-null/non-blank, {@code categoryTag} non-null, nutrients {@code >= 0}
     * @effects initializes this meal with a generated {@link MealID} and today's date
     */
    public Meal(String name, String categoryTag, double protein, double carbs, double fat, double calories) {
        this(name, categoryTag, protein, carbs, fat, calories, LocalDate.now());
    }

    /**
     * Creates a meal entry with an explicit category tag and consumption date.
     *
     * @param name the meal name
     * @param categoryTag tag categorising the meal (e.g. {@code #protein})
     * @param protein grams of protein
     * @param carbs grams of carbohydrates
     * @param fat grams of fat
     * @param calories total calories
     * @param consumedOn the date the meal was consumed
     * @requires {@code name} is non-null/non-blank, {@code categoryTag} non-null, nutrients {@code >= 0}, {@code consumedOn} non-null
     * @effects initializes this meal with a generated {@link MealID} and supplied data
     */
    public Meal(String name, String categoryTag, double protein, double carbs, double fat, double calories, LocalDate consumedOn) {
        this.id = MealID.randomId();
        this.name = name;
        this.categoryTag = categoryTag;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.calories = calories;
        this.consumedOn = consumedOn;
        checkRep();
    }

    // Getter methods
    /**
     * Returns this meal's identifier.
     *
     * @return identifier for this meal
     * @requires nothing
     * @effects does not modify this meal
     */
    public MealID getId() {
        return id;
    }

    /**
     * Returns the meal name.
     *
     * @return current meal name
     * @requires nothing
     * @effects does not modify this meal
     */
    public String getName() {
        return name;
    }

    /**
     * Updates the meal name.
     *
     * @param name new meal name
     * @return nothing
     * @requires {@code name} is non-null/non-blank
     * @effects sets this meal's name to {@code name}
     */
    public void setName(String name) {
        this.name = name;
        checkRep();
    }

    /**
     * Returns the category tag assigned to this meal.
     *
     * @return category tag (e.g. {@code #protein})
     * @requires nothing
     * @effects does not modify this meal
     */
    public String getCategoryTag() {
        return categoryTag;
    }

    /**
     * Updates the category tag assigned to this meal.
     *
     * @param categoryTag new category tag
     * @return nothing
     * @requires {@code categoryTag} is non-null
     * @effects sets this meal's category tag to {@code categoryTag}
     */
    public void setCategoryTag(String categoryTag) {
        this.categoryTag = categoryTag;
        checkRep();
    }

    /**
     * Returns the recorded protein grams.
     *
     * @return protein grams
     * @requires nothing
     * @effects does not modify this meal
     */
    public double getProtein() {
        return protein;
    }

    /**
     * Updates the recorded protein grams.
     *
     * @param protein new protein grams
     * @return nothing
     * @requires {@code protein} &gt;= 0
     * @effects sets this meal's protein to {@code protein}
     */
    public void setProtein(double protein) {
        this.protein = protein;
        checkRep();
    }

    /**
     * Returns the recorded carbohydrate grams.
     *
     * @return carbohydrate grams
     * @requires nothing
     * @effects does not modify this meal
     */
    public double getCarbs() {
        return carbs;
    }

    /**
     * Updates the recorded carbohydrate grams.
     *
     * @param carbs new carbohydrate grams
     * @return nothing
     * @requires {@code carbs} &gt;= 0
     * @effects sets this meal's carbohydrates to {@code carbs}
     */
    public void setCarbs(double carbs) {
        this.carbs = carbs;
        checkRep();
    }

    /**
     * Returns the recorded fat grams.
     *
     * @return fat grams
     * @requires nothing
     * @effects does not modify this meal
     */
    public double getFat() {
        return fat;
    }

    /**
     * Updates the recorded fat grams.
     *
     * @param fat new fat grams
     * @return nothing
     * @requires {@code fat} &gt;= 0
     * @effects sets this meal's fat grams to {@code fat}
     */
    public void setFat(double fat) {
        this.fat = fat;
        checkRep();
    }

    /**
     * Returns the recorded calories.
     *
     * @return calorie total
     * @requires nothing
     * @effects does not modify this meal
     */
    public double getCalories() {
        return calories;
    }

    /**
     * Updates the recorded calories.
     *
     * @param calories new calorie total
     * @return nothing
     * @requires {@code calories} &gt;= 0
     * @effects sets this meal's calories to {@code calories}
     */
    public void setCalories(double calories) {
        this.calories = calories;
        checkRep();
    }

    /**
     * Returns the date the meal was consumed.
     *
     * @return consumption date
     * @requires nothing
     * @effects does not modify this meal
     */
    public LocalDate getConsumedOn() {
        return consumedOn;
    }

    /**
     * Updates the consumption date.
     *
     * @param consumedOn new consumption date
     * @return nothing
     * @requires {@code consumedOn} is non-null
     * @effects sets this meal's consumption date to {@code consumedOn}
     */
    public void setConsumedOn(LocalDate consumedOn) {
        this.consumedOn = consumedOn;
        checkRep();
    }

    /**
     * Adjusts this meal for a different portion size.
     *
     * @param portionMultiplier portion multiplier (&gt; 0)
     * @return {@code this} when {@code portionMultiplier == 1}, otherwise a new scaled {@link Meal}
     * @requires {@code portionMultiplier} &gt; 0
     * @effects does not modify this instance when {@code portionMultiplier == 1}; otherwise returns a new meal
     */
    public Meal scaleBy(double portionMultiplier) {
        if (portionMultiplier <= 0) {
            throw new IllegalArgumentException("Portion multiplier must be positive.");
        }
        if (Double.compare(portionMultiplier, 1.0) == 0) {
            return this;
        }
        return new Meal(
            name,
            categoryTag,
            protein * portionMultiplier,
            carbs * portionMultiplier,
            fat * portionMultiplier,
            calories * portionMultiplier,
            consumedOn
        );
    }

    /**
     * Returns a formatted summary of this meal.
     *
     * @return formatted string containing name, category, and nutrient values
     * @requires nothing
     * @effects does not modify this meal
     */
    @Override
    public String toString() {
        return String.format("%s %s (P:%.1f, C:%.1f, F:%.1f, Cal:%.1f)",
            name,
            categoryTag == null || categoryTag.isBlank() ? "" : categoryTag,
            protein,
            carbs,
            fat,
            calories
        ).trim();
    }

    /**
     * Compares this meal to another object for equality based on {@link MealID}.
     *
     * @param o object to compare
     * @return {@code true} when {@code o} is a {@code Meal} with the same identifier; otherwise {@code false}
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
        Meal meal = (Meal) o;
        return Objects.equals(id, meal.id);
    }

    /**
     * Computes the hash code using this meal's identifier.
     *
     * @return hash code for this meal
     * @requires nothing
     * @effects does not modify this meal
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    private void checkRep() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Meal name cannot be empty.");
        }
        if (categoryTag == null) {
            throw new IllegalArgumentException("Category tag must not be null.");
        }
        if (protein < 0 || carbs < 0 || fat < 0 || calories < 0) {
            throw new IllegalArgumentException("Nutritional values must be non-negative.");
        }
        if (consumedOn == null) {
            throw new IllegalArgumentException("Consumption date must be provided.");
        }
    }

    private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        inputStream.defaultReadObject();
        if (categoryTag == null) {
            categoryTag = "#uncategorized";
        }
        if (consumedOn == null) {
            consumedOn = LocalDate.now();
        }
        checkRep();
    }
}
