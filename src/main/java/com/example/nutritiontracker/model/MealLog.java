package com.example.nutritiontracker.model;

import com.example.nutritiontracker.model.RequirementCalculator.DailyRequirements;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * REQUIREMENT: Abstract Data Type (ADT) 2.
 * This ADT is MUTABLE and includes AF, RI, and checkRep().
 *
 * Manages a collection of meals for a single day.
 *
 * Abstraction Function (AF):
 * AF(c) = A user's daily record of meals, represented by the Meal objects in the {@code c.mealList}.
 *
 * Representation Invariant (RI):
 * - {@code mealList} must not contain {@code null}.
 * - All nutritional values (protein, carbs, fats, calories) are non-negative.
 * - All meals recorded belong to the same {@link #logDate}.
 *
 * Safety from Rep Exposure:
 * The {@link #getMeals()} method returns an unmodifiable list to prevent clients from
 * changing the internal state in a way that could violate the RI.
 */
public class MealLog implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Pattern MEAL_ENTRY_PATTERN = Pattern.compile(
        "^\\s*(?<name>[A-Za-z][A-Za-z\\s]*)\\s+#(?<category>\\w+)\\s*"
            + "(?:(?<protein>\\d+(?:\\.\\d+)?)p\\s*)?"
            + "(?:(?<carbs>\\d+(?:\\.\\d+)?)c\\s*)?"
            + "(?:(?<fat>\\d+(?:\\.\\d+)?)f\\s*)?"
            + "(?<calories>\\d+(?:\\.\\d+)?)cal\\s*$",
        Pattern.CASE_INSENSITIVE
    );

    private final List<Meal> mealList;
    private LocalDate logDate;

    /**
     * Creates an empty meal log for the current day.
     *
     * @requires nothing
     * @effects initializes an empty {@code mealList} and sets {@link #logDate} to today
     */
    public MealLog() {
        this(LocalDate.now());
    }

    /**
     * Creates an empty meal log bound to the supplied date.
     *
     * @param logDate date represented by this log
     * @requires {@code logDate} is non-null
     * @effects initializes an empty {@code mealList} for {@code logDate}
     */
    public MealLog(LocalDate logDate) {
        if (logDate == null) {
            throw new IllegalArgumentException("Log date cannot be null.");
        }
        this.mealList = new ArrayList<>();
        this.logDate = logDate;
        checkRep();
    }

    /**
     * Checks that the Representation Invariant holds.
     */
    private void checkRep() {
        if (mealList == null) {
            throw new IllegalStateException("RI Violated: meal list cannot be null");
        }
        if (logDate == null) {
            throw new IllegalStateException("RI Violated: log date cannot be null");
        }
        Set<MealID> ids = new HashSet<>();
        for (Meal meal : mealList) {
            if (meal == null) {
                throw new IllegalStateException("RI Violated: meal cannot be null");
            }
            if (meal.getId() == null) {
                throw new IllegalStateException("RI Violated: meal ID cannot be null.");
            }
            if (meal.getCategoryTag() == null) {
                throw new IllegalStateException("RI Violated: meal category cannot be null.");
            }
            if (!ids.add(meal.getId())) {
                throw new IllegalStateException("RI Violated: duplicate meal ID found: " + meal.getId());
            }
            if (!logDate.equals(meal.getConsumedOn())) {
                throw new IllegalStateException("RI Violated: meal date does not match log date: " + meal.getConsumedOn());
            }
            if (meal.getProtein() < 0 || meal.getCarbs() < 0 || meal.getFat() < 0 || meal.getCalories() < 0) {
                throw new IllegalStateException("RI Violated: nutritional values must be non-negative.");
            }
        }
    }

    /**
     * Adds a meal to the log.
     *
     * @param meal meal to add
     * @return nothing
     * @requires {@code meal} is non-null and its {@code consumedOn} matches {@link #logDate}
     * @effects appends {@code meal} to the internal list
     */
    public void addMeal(Meal meal) {
        if (meal == null) {
            throw new IllegalArgumentException("Meal cannot be null.");
        }
        if (!logDate.equals(meal.getConsumedOn())) {
            throw new IllegalArgumentException("Meal date does not match log date.");
        }
        this.mealList.add(meal);
        checkRep();
    }

    /**
     * Parses a free-form meal entry and adds it to the log.
     *
     * @param mealEntry user-entered string describing a meal
     * @return the newly created {@link Meal}
     * @requires {@code mealEntry} is non-null and matches the expected pattern
     * @effects creates a new {@link Meal} and appends it to the internal list
     */
    public Meal addMealFromEntry(String mealEntry) {
        if (mealEntry == null) {
            throw new IllegalArgumentException("Meal entry cannot be null.");
        }

        Matcher matcher = MEAL_ENTRY_PATTERN.matcher(mealEntry.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Meal entry format is invalid: " + mealEntry);
        }

        String name = matcher.group("name").trim();
        String category = "#" + matcher.group("category").toLowerCase();

        double protein = parseMacroValue(matcher.group("protein"));
        double carbs = parseMacroValue(matcher.group("carbs"));
        double fat = parseMacroValue(matcher.group("fat"));
        double calories = parseMacroValue(matcher.group("calories"));

        Meal meal = new Meal(name, category, protein, carbs, fat, calories, logDate);
        addMeal(meal);
        return meal;
    }

    private double parseMacroValue(String rawValue) {
        return rawValue == null ? 0.0 : Double.parseDouble(rawValue);
    }

    /**
     * Removes a meal from the log.
     *
     * @param mealId identifier of the meal to remove
     * @return nothing
     * @requires {@code mealId} is non-null
     * @effects removes any meal whose identifier equals {@code mealId}
     */
    public void removeMeal(MealID mealId) {
        if (mealId == null) {
            throw new IllegalArgumentException("Meal ID cannot be null.");
        }
        this.mealList.removeIf(meal -> meal.getId().equals(mealId));
        checkRep();
    }

    /**
     * Updates the nutritional values of a meal already stored in the log.
     *
     * @param mealId identifier of the meal to update
     * @param name new meal name
     * @param protein grams of protein
     * @param carbs grams of carbohydrates
     * @param fat grams of fat
     * @param calories total calories
     * @requires {@code mealId} and {@code name} are non-null, nutrient values {@code >= 0}
     * @effects mutates the matching meal with the supplied values
     */
    public void updateMeal(MealID mealId, String name, double protein, double carbs, double fat, double calories) {
        if (mealId == null) {
            throw new IllegalArgumentException("Meal ID cannot be null.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Meal name cannot be blank.");
        }
        if (protein < 0 || carbs < 0 || fat < 0 || calories < 0) {
            throw new IllegalArgumentException("Nutritional values must be non-negative.");
        }

        boolean updated = false;
        for (Meal meal : mealList) {
            if (meal.getId().equals(mealId)) {
                meal.setName(name.trim());
                meal.setProtein(protein);
                meal.setCarbs(carbs);
                meal.setFat(fat);
                meal.setCalories(calories);
                updated = true;
                break;
            }
        }

        if (!updated) {
            throw new IllegalArgumentException("Meal with id " + mealId + " not found.");
        }

        checkRep();
    }

    /**
     * Provides a safe, unmodifiable view of the meals in the log.
     *
     * @return unmodifiable list of meals for {@link #logDate}
     * @requires nothing
     * @effects does not modify internal state
     */
    public List<Meal> getMeals() {
        return Collections.unmodifiableList(mealList);
    }

    /**
     * Returns the date associated with this log.
     *
     * @return log date
     * @requires nothing
     * @effects does not modify internal state
     */
    public LocalDate getLogDate() {
        return logDate;
    }

    // --- Calculation Methods ---
    /**
     * Computes total calories for all meals.
     *
     * @return total calories logged
     * @requires nothing
     * @effects does not modify internal state
     */
    public double getTotalCalories() {
        return mealList.stream().mapToDouble(Meal::getCalories).sum();
    }

    /**
     * Computes total protein for all meals.
     *
     * @return total protein grams logged
     * @requires nothing
     * @effects does not modify internal state
     */
    public double getTotalProtein() {
        return mealList.stream().mapToDouble(Meal::getProtein).sum();
    }

    /**
     * Computes total carbohydrates for all meals.
     *
     * @return total carbohydrate grams logged
     * @requires nothing
     * @effects does not modify internal state
     */
    public double getTotalCarbs() {
        return mealList.stream().mapToDouble(Meal::getCarbs).sum();
    }

    /**
     * Computes total fat for all meals.
     *
     * @return total fat grams logged
     * @requires nothing
     * @effects does not modify internal state
     */
    public double getTotalFat() {
        return mealList.stream().mapToDouble(Meal::getFat).sum();
    }

    /**
     * Calculates remaining calories against supplied requirements.
     *
     * @param requirements daily nutrition requirements
     * @return remaining calories (positive if under goal, negative if exceeded)
     * @requires {@code requirements} is non-null
     * @effects does not modify internal state
     */
    public double remainingCalories(DailyRequirements requirements) {
        if (requirements == null) {
            throw new IllegalArgumentException("Daily requirements are required.");
        }
        return requirements.calories() - getTotalCalories();
    }

    private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        inputStream.defaultReadObject();
        if (logDate == null) {
            logDate = LocalDate.now();
        }
        checkRep();
    }
}
