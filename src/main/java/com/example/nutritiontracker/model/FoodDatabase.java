package com.example.nutritiontracker.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom Food Database.
 * Manages a collection of reusable food items for quick meal logging.
 *
 * Abstraction Function (AF):
 * AF(c) = A user's personal library of saved foods, represented by the Food objects in {@code c.foods}.
 *
 * Representation Invariant (RI):
 * - {@code foods} must not be null.
 * - All keys in {@code foods} must be non-null and non-blank.
 * - All values in {@code foods} must be non-null Food objects.
 *
 * This ADT is MUTABLE - foods can be added and removed.
 */
public class FoodDatabase implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<String, Food> foods;

    /**
     * Creates an empty food database.
     *
     * @requires nothing
     * @effects initializes an empty {@code foods} map
     */
    public FoodDatabase() {
        this.foods = new HashMap<>();
        checkRep();
    }

    /**
     * Checks that the Representation Invariant holds.
     */
    private void checkRep() {
        if (foods == null) {
            throw new IllegalStateException("RI Violated: foods map cannot be null");
        }
        for (Map.Entry<String, Food> entry : foods.entrySet()) {
            if (entry.getKey() == null || entry.getKey().trim().isEmpty()) {
                throw new IllegalStateException("RI Violated: food key cannot be null or blank");
            }
            if (entry.getValue() == null) {
                throw new IllegalStateException("RI Violated: food value cannot be null");
            }
        }
    }

    /**
     * Adds a custom food to the database.
     *
     * @param food food to add
     * @return nothing
     * @requires {@code food} is non-null
     * @effects adds {@code food} to the database; if a food with the same name exists, it is replaced
     */
    public void addFood(Food food) {
        if (food == null) {
            throw new IllegalArgumentException("Food cannot be null.");
        }
        foods.put(food.getName().toLowerCase(), food);
        checkRep();
    }

    /**
     * Removes a food from the database by name.
     *
     * @param name name of the food to remove
     * @return {@code true} if the food was removed; {@code false} if not found
     * @requires {@code name} is non-null
     * @effects removes the food with the given name from the database
     */
    public boolean removeFood(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Food name cannot be null.");
        }
        boolean removed = foods.remove(name.toLowerCase()) != null;
        checkRep();
        return removed;
    }

    /**
     * Retrieves a food by name.
     *
     * @param name name of the food to retrieve
     * @return the {@link Food} with the given name, or {@code null} if not found
     * @requires {@code name} is non-null
     * @effects does not modify internal state
     */
    public Food getFood(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Food name cannot be null.");
        }
        return foods.get(name.toLowerCase());
    }

    /**
     * Checks if a food exists in the database.
     *
     * @param name name of the food to check
     * @return {@code true} if the food exists; {@code false} otherwise
     * @requires {@code name} is non-null
     * @effects does not modify internal state
     */
    public boolean hasFood(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Food name cannot be null.");
        }
        return foods.containsKey(name.toLowerCase());
    }

    /**
     * Returns all custom foods in the database.
     *
     * @return list of all {@link Food} objects
     * @requires nothing
     * @effects does not modify internal state
     */
    public List<Food> getAllFoods() {
        return new ArrayList<>(foods.values());
    }

    /**
     * Returns the number of foods in the database.
     *
     * @return count of foods
     * @requires nothing
     * @effects does not modify internal state
     */
    public int size() {
        return foods.size();
    }

    /**
     * Checks if the database is empty.
     *
     * @return {@code true} if no foods are stored; {@code false} otherwise
     * @requires nothing
     * @effects does not modify internal state
     */
    public boolean isEmpty() {
        return foods.isEmpty();
    }

    private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        inputStream.defaultReadObject();
        if (foods == null) {
            throw new IllegalStateException("RI Violated: foods map cannot be null after deserialization");
        }
        checkRep();
    }
}
