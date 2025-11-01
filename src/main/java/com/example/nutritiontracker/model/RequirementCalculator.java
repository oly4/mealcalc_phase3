package com.example.nutritiontracker.model;

/**
 * Encapsulates the nutrition requirement calculations so they can be reused or
 * replaced without touching the rest of the model layer.
 */
public final class RequirementCalculator {

    /**
     * Calculates daily nutrition requirements for the supplied profile.
     *
     * @param profile user profile input
     * @return calculated macronutrient and calorie goals
     * @requires {@code profile} is non-null
     * @effects does not modify {@code profile}
     */
    public DailyRequirements calculate(UserProfile profile) {
        if (profile == null) {
            throw new IllegalArgumentException("User profile is required for calculations.");
        }

        // Using Mifflin-St Jeor formula for BMR (assuming male baseline).
        double bmr = 10 * profile.getWeightKg()
            + 6.25 * profile.getHeightCm()
            - 5 * profile.getAge()
            + 5;

        double maintenanceCalories = bmr * profile.getActivityLevel().multiplier;
        double targetCalories = maintenanceCalories + profile.getGoal().calorieModifier;

        // Macronutrient split (40% carbs, 30% protein, 30% fat)
        double protein = (targetCalories * 0.30) / 4;
        double carbs = (targetCalories * 0.40) / 4;
        double fat = (targetCalories * 0.30) / 9;

        return new DailyRequirements(targetCalories, protein, carbs, fat);
    }

    /**
     * Immutable view of the calculated daily requirements.
     *
     * @param calories total daily calorie goal
     * @param protein protein grams goal
     * @param carbs carbohydrate grams goal
     * @param fat fat grams goal
     * @requires nothing
     * @effects encapsulates computed requirement values
     */
    public record DailyRequirements(double calories, double protein, double carbs, double fat) { }
}
