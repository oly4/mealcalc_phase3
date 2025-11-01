package com.example.nutritiontracker.model;

import java.io.Serializable;

/**
 * A mutable data class to hold user's physical information and goals.
 */
public class UserProfile implements Serializable {
    private static final long serialVersionUID = 1L;

    private double weightKg; // in kilograms
    private double heightCm; // in centimeters
    private int age;
    private ActivityLevel activityLevel;
    private Goal goal;

    public enum ActivityLevel {
        SEDENTARY(1.2),
        LIGHTLY_ACTIVE(1.375),
        MODERATELY_ACTIVE(1.55),
        VERY_ACTIVE(1.725),
        EXTRA_ACTIVE(1.9);

        public final double multiplier;

        ActivityLevel(double multiplier) {
            this.multiplier = multiplier;
        }
    }

    public enum Goal {
        LOSE_WEIGHT(-500),
        MAINTAIN_WEIGHT(0),
        GAIN_WEIGHT(500);

        public final int calorieModifier;

        Goal(int calorieModifier) {
            this.calorieModifier = calorieModifier;
        }
    }

    /**
     * Creates a profile with sensible default values.
     *
     * @requires nothing
     * @effects initializes all fields with defaults
     */
    public UserProfile() {
        // Default values
        this.weightKg = 70;
        this.heightCm = 175;
        this.age = 30;
        this.activityLevel = ActivityLevel.MODERATELY_ACTIVE;
        this.goal = Goal.MAINTAIN_WEIGHT;
    }

    // Getters and Setters
    /**
     * Returns the user's weight.
     *
     * @return weight in kilograms
     * @requires nothing
     * @effects does not modify profile state
     */
    public double getWeightKg() {
        return weightKg;
    }

    /**
     * Updates the user's weight.
     *
     * @param weightKg new weight in kilograms
     * @return nothing
     * @requires {@code weightKg} &gt; 0
     * @effects sets {@link #weightKg} to {@code weightKg}
     */
    public void setWeightKg(double weightKg) {
        this.weightKg = weightKg;
    }

    /**
     * Returns the user's height.
     *
     * @return height in centimetres
     * @requires nothing
     * @effects does not modify profile state
     */
    public double getHeightCm() {
        return heightCm;
    }

    /**
     * Updates the user's height.
     *
     * @param heightCm new height in centimetres
     * @return nothing
     * @requires {@code heightCm} &gt; 0
     * @effects sets {@link #heightCm} to {@code heightCm}
     */
    public void setHeightCm(double heightCm) {
        this.heightCm = heightCm;
    }

    /**
     * Returns the user's age.
     *
     * @return age in years
     * @requires nothing
     * @effects does not modify profile state
     */
    public int getAge() {
        return age;
    }

    /**
     * Updates the user's age.
     *
     * @param age new age in years
     * @return nothing
     * @requires {@code age} &gt;= 0
     * @effects sets {@link #age} to {@code age}
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Returns the current activity level.
     *
     * @return activity level
     * @requires nothing
     * @effects does not modify profile state
     */
    public ActivityLevel getActivityLevel() {
        return activityLevel;
    }

    /**
     * Updates the activity level.
     *
     * @param activityLevel new activity level
     * @return nothing
     * @requires {@code activityLevel} is non-null
     * @effects sets {@link #activityLevel} to {@code activityLevel}
     */
    public void setActivityLevel(ActivityLevel activityLevel) {
        this.activityLevel = activityLevel;
    }

    /**
     * Returns the user's goal.
     *
     * @return current goal
     * @requires nothing
     * @effects does not modify profile state
     */
    public Goal getGoal() {
        return goal;
    }

    /**
     * Updates the user's goal.
     *
     * @param goal new goal
     * @return nothing
     * @requires {@code goal} is non-null
     * @effects sets {@link #goal} to {@code goal}
     */
    public void setGoal(Goal goal) {
        this.goal = goal;
    }
}
