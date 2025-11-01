package com.example.nutritiontracker.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Immutable identifier for {@link Meal} instances.
 * Provides stable equality semantics for use as map keys.
 */
public final class MealID implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String value;

    /**
     * Creates a new {@code MealID} with the given value.
     *
     * @param value raw identifier value
     * @requires {@code value} is non-null and not blank
     * @effects initializes an immutable identifier with the supplied value
     */
    public MealID(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("MealID value must not be null or blank.");
        }
        this.value = value;
    }

    /**
     * Generates a new, random {@code MealID}.
     *
     * @return newly created identifier
     * @requires nothing
     * @effects creates a {@code MealID} with a random UUID-backed value
     */
    public static MealID randomId() {
        return new MealID(UUID.randomUUID().toString());
    }

    /**
     * Returns the underlying string representation of this identifier.
     *
     * @return underlying string value of this identifier
     * @requires nothing
     * @effects does not modify this identifier
     */
    public String value() {
        return value;
    }

    /**
     * Compares this identifier to another object for equality.
     *
     * @param o object to compare
     * @return {@code true} when {@code o} is a {@code MealID} with the same value; otherwise {@code false}
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
        MealID mealID = (MealID) o;
        return value.equals(mealID.value);
    }

    /**
     * Computes the hash code for this identifier.
     *
     * @return hash code derived from the identifier value
     * @requires nothing
     * @effects does not modify this identifier
     */
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    /**
     * Returns a string representation of this identifier.
     *
     * @return the raw string value
     * @requires nothing
     * @effects does not modify this identifier
     */
    @Override
    public String toString() {
        return value;
    }
}
