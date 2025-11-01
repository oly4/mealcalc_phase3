package com.example.nutritiontracker.model;

import com.example.nutritiontracker.model.RequirementCalculator.DailyRequirements;
import com.example.nutritiontracker.utils.CsvExporter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;

/**
 * REQUIREMENT: Observer Pattern (The Observable/Subject).
 *
 * This class acts as a central hub for the application's model state. It holds
 * the UserProfile and the MealLog. It implements the Observable interface to notify
 * the View whenever any part of the model's state changes.
 */
public class AppModel implements Observable, Serializable {
    private static final long serialVersionUID = 1L;

    private UserProfile userProfile;
    private MealLog mealLog;
    private FoodDatabase foodDatabase;
    private transient List<Observer> observers; // transient: don't save observers
    private transient RequirementCalculator requirementCalculator;

    /**
     * Creates an application model with default profile and empty meal log.
     *
     * @requires nothing
     * @effects initializes model state and supporting services
     */
    public AppModel() {
        this.userProfile = new UserProfile();
        this.mealLog = new MealLog();
        this.foodDatabase = new FoodDatabase();
        ensureTransientState();
    }

    // --- Business Logic Methods that modify state ---

    /**
     * Replaces the stored user profile.
     *
     * @param profile new profile to persist
     * @return nothing
     * @requires {@code profile} is non-null
     * @effects sets {@link #userProfile} and notifies observers
     */
    public void updateUserProfile(UserProfile profile) {
        ensureTransientState();
        this.userProfile = profile;
        notifyObservers();
    }

    /**
     * Adds a new meal to the daily log.
     *
     * @param meal meal to add
     * @return nothing
     * @requires {@code meal} is non-null and its {@code consumedOn} matches the log date
     * @effects delegates to {@link MealLog#addMeal(Meal)} and notifies observers
     */
    public void addMeal(Meal meal) {
        ensureTransientState();
        this.mealLog.addMeal(meal);
        notifyObservers();
    }

    /**
     * Parses a free-form meal entry and adds it to the log.
     *
     * @param mealEntry user-entered string describing a meal
     * @return the created {@link Meal}
     * @requires {@code mealEntry} is non-null and matches the expected pattern
     * @effects delegates to {@link MealLog#addMealFromEntry(String)} and notifies observers
     */
    public Meal addMealFromEntry(String mealEntry) {
        ensureTransientState();
        Meal meal = this.mealLog.addMealFromEntry(mealEntry);
        notifyObservers();
        return meal;
    }

    /**
     * Removes a meal from the daily log.
     *
     * @param mealId identifier of the meal to remove
     * @return nothing
     * @requires {@code mealId} is non-null
     * @effects delegates to {@link MealLog#removeMeal(MealID)} and notifies observers
     */
    public void removeMeal(MealID mealId) {
        ensureTransientState();
        this.mealLog.removeMeal(mealId);
        notifyObservers();
    }

    // --- Getters for state information ---
    /**
     * Returns the current user profile.
     *
     * @return active {@link UserProfile}
     * @requires nothing
     * @effects does not modify internal state
     */
    public UserProfile getUserProfile() {
        return userProfile;
    }

    /**
     * Returns the current meal log.
     *
     * @return active {@link MealLog}
     * @requires nothing
     * @effects does not modify internal state
     */
    public MealLog getMealLog() {
        return mealLog;
    }

    /**
     * Calculates daily nutrition requirements for the stored profile.
     *
     * @return computed {@link DailyRequirements}
     * @requires {@link #userProfile} is non-null
     * @effects does not modify internal state
     */
    public DailyRequirements getDailyRequirements() {
        ensureTransientState();
        return requirementCalculator.calculate(userProfile);
    }

    /**
     * Returns the custom food database.
     *
     * @return active {@link FoodDatabase}
     * @requires nothing
     * @effects does not modify internal state
     */
    public FoodDatabase getFoodDatabase() {
        return foodDatabase;
    }

    // --- Custom Food Management Methods ---
    /**
     * Adds a custom food to the database.
     *
     * @param food food to add
     * @return nothing
     * @requires {@code food} is non-null
     * @effects adds {@code food} to the database and notifies observers
     */
    public void addCustomFood(Food food) {
        ensureTransientState();
        this.foodDatabase.addFood(food);
        notifyObservers();
    }

    /**
     * Removes a custom food from the database.
     *
     * @param foodName name of the food to remove
     * @return {@code true} if removed successfully
     * @requires {@code foodName} is non-null
     * @effects removes the food and notifies observers
     */
    public boolean removeCustomFood(String foodName) {
        ensureTransientState();
        boolean removed = this.foodDatabase.removeFood(foodName);
        if (removed) {
            notifyObservers();
        }
        return removed;
    }

    /**
     * Exports current nutrition data to a CSV file.
     *
     * @param target path to the CSV file
     * @requires {@code target} is non-null
     * @effects writes the meal log and profile information into {@code target}
     */
    public void exportNutritionData(Path target) throws IOException {
        ensureTransientState();
        CsvExporter.export(mealLog, userProfile, target);
    }

    /**
     * Updates an existing meal with new nutritional values.
     *
     * @param mealId identifier of the meal to update
     * @param name new meal name
     * @param protein grams of protein
     * @param carbs grams of carbohydrates
     * @param fat grams of fat
     * @param calories total calories
     * @requires {@code mealId} and {@code name} are non-null, nutrient values {@code >= 0}
     * @effects mutates the stored meal and notifies observers
     */
    public void updateMeal(MealID mealId, String name, double protein, double carbs, double fat, double calories) {
        ensureTransientState();
        this.mealLog.updateMeal(mealId, name, protein, carbs, fat, calories);
        notifyObservers();
    }

    // --- Observer Pattern Implementation ---
    /**
     * Registers a new observer.
     *
     * @param observer observer to register
     * @return nothing
     * @requires {@code observer} is non-null
     * @effects adds {@code observer} to the internal observer list
     */
    @Override
    public void addObserver(Observer observer) {
        ensureTransientState();
        observers.add(observer);
    }

    /**
     * Unregisters an observer.
     *
     * @param observer observer to remove
     * @return nothing
     * @requires {@code observer} is non-null
     * @effects removes {@code observer} from the internal observer list
     */
    @Override
    public void removeObserver(Observer observer) {
        ensureTransientState();
        observers.remove(observer);
    }

    /**
     * Notifies all registered observers of state changes.
     *
     * @return nothing
     * @requires nothing
     * @effects invokes {@link Observer#update(AppModel)} on each observer
     */
    @Override
    public void notifyObservers() {
        ensureTransientState();
        for (Observer observer : observers) {
            observer.update(this);
        }
    }

    private void ensureTransientState() {
        if (observers == null) {
            observers = new ArrayList<>();
        }
        if (requirementCalculator == null) {
            requirementCalculator = new RequirementCalculator();
        }
    }

    private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        inputStream.defaultReadObject();
        if (foodDatabase == null) {
            foodDatabase = new FoodDatabase();
            System.out.println("Created new FoodDatabase during deserialization");
        } else {
            System.out.println("Loaded FoodDatabase with " + foodDatabase.size() + " foods");
        }
        ensureTransientState();
    }
}
