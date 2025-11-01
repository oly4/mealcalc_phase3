package com.example.nutritiontracker.controller;

import com.example.nutritiontracker.model.AppModel;
import com.example.nutritiontracker.model.Food;
import com.example.nutritiontracker.model.Meal;
import com.example.nutritiontracker.view.MainView;

import java.io.File;
import java.io.IOException;

/**
 * REQUIREMENT: The Controller in MVC.
 *
 * Connects the View and Model. It handles user actions from the view, validates input,
 * and calls the appropriate methods on the model to update the application state.
 */
public class AppController {

    private final AppModel model;
    private final MainView view;

    public AppController(AppModel model, MainView view) {
        this.model = model;
        this.view = view;
    }

    public void updateProfile() {
        try {
            model.updateUserProfile(view.getProfileInput());
        } catch (NumberFormatException e) {
            view.showError("Invalid number format in profile fields. Please enter valid numbers.");
        }
    }

    public void addMeal() {
        String[] inputs = view.getMealInput();
        String name = inputs[0];

        if (name == null || name.trim().isEmpty()) {
            view.showError("Meal name cannot be empty.");
            return;
        }

        if (name.contains("#")) {
            try {
                model.addMealFromEntry(name.trim());
                view.clearMealInput();
                return;
            } catch (IllegalArgumentException e) {
                view.showError(e.getMessage());
                return;
            }
        }

        try {
            double protein = Double.parseDouble(inputs[1]);
            double carbs = Double.parseDouble(inputs[2]);
            double fat = Double.parseDouble(inputs[3]);
            double calories = Double.parseDouble(inputs[4]);

            // Basic validation for non-negative numbers
            if (protein < 0 || carbs < 0 || fat < 0 || calories < 0) {
                view.showError("Nutritional values cannot be negative.");
                return;
            }

            Meal meal = new Meal(
                name,
                protein,
                carbs,
                fat,
                calories,
                model.getMealLog().getLogDate()
            );
            model.addMeal(meal);
            view.clearMealInput();
        } catch (NumberFormatException e) {
            view.showError("Invalid number format in meal fields. Please enter valid numbers.");
        }
    }

    public void removeMeal() {
        Meal selected = view.getSelectedMeal();
        if (selected != null) {
            model.removeMeal(selected.getId());
        } else {
            view.showError("Please select a meal to remove.");
        }
    }

    /**
     * Applies the current input values to the selected meal entry.
     */
    public void updateMeal() {
        Meal selected = view.getSelectedMeal();
        if (selected == null) {
            view.showError("Please select a meal to update.");
            return;
        }

        String[] inputs = view.getMealInput();
        String name = inputs[0];

        if (name == null || name.trim().isEmpty()) {
            view.showError("Meal name cannot be empty.");
            return;
        }

        if (name.contains("#")) {
            view.showError("Editing with quick entry format is not supported. Use the numeric fields.");
            return;
        }

        try {
            double protein = Double.parseDouble(inputs[1]);
            double carbs = Double.parseDouble(inputs[2]);
            double fat = Double.parseDouble(inputs[3]);
            double calories = Double.parseDouble(inputs[4]);

            if (protein < 0 || carbs < 0 || fat < 0 || calories < 0) {
                view.showError("Nutritional values cannot be negative.");
                return;
            }

            model.updateMeal(selected.getId(), name, protein, carbs, fat, calories);
            view.clearMealInput();
        } catch (NumberFormatException e) {
            view.showError("Invalid number format in meal fields. Please enter valid numbers.");
        } catch (IllegalArgumentException e) {
            view.showError(e.getMessage());
        }
    }

    /**
     * Saves the current meal input as a custom food.
     */
    public void saveAsCustomFood() {
        String[] inputs = view.getMealInput();
        Meal selectedMeal = view.getSelectedMeal();

        String name = inputs[0];
        if (name == null || name.trim().isEmpty()) {
            if (selectedMeal == null) {
                view.showError("Enter a food name or select a meal first.");
                return;
            }
            name = selectedMeal.getName();
        }

        try {
            double protein = resolveMacro(inputs[1], selectedMeal != null ? selectedMeal.getProtein() : null, "protein");
            double carbs = resolveMacro(inputs[2], selectedMeal != null ? selectedMeal.getCarbs() : null, "carbohydrates");
            double fat = resolveMacro(inputs[3], selectedMeal != null ? selectedMeal.getFat() : null, "fat");
            double calories = resolveMacro(inputs[4], selectedMeal != null ? selectedMeal.getCalories() : null, "calories");

            Food food = new Food(name.trim(), protein, carbs, fat, calories);
            model.addCustomFood(food);
            view.showMessage("Custom food '" + food.getName() + "' saved successfully!");
            view.clearMealInput();
        } catch (NumberFormatException e) {
            view.showError("Invalid number format. Please enter valid numbers or select a meal.");
        } catch (IllegalArgumentException e) {
            view.showError(e.getMessage());
        }
    }

    /**
     * Loads a selected custom food into the meal input fields.
     */
    public void loadCustomFood() {
        Food selected = view.getSelectedCustomFood();
        if (selected != null) {
            view.loadFoodIntoMealInput(selected);
        } else {
            // If no custom food is selected, check if a meal is selected
            Meal selectedMeal = view.getSelectedMeal();
            if (selectedMeal != null) {
                // Convert the selected meal to a custom food and add it
                Food food = new Food(
                    selectedMeal.getName(),
                    selectedMeal.getProtein(),
                    selectedMeal.getCarbs(),
                    selectedMeal.getFat(),
                    selectedMeal.getCalories()
                );
                model.addCustomFood(food);
                view.showMessage("Meal '" + selectedMeal.getName() + "' saved as custom food!");
            } else {
                view.showError("Please select a food from the list or a meal to save.");
            }
        }
    }

    /**
     * Removes the selected custom food from the database.
     */
    public void removeCustomFood() {
        Food selected = view.getSelectedCustomFood();
        if (selected != null) {
            boolean removed = model.removeCustomFood(selected.getName());
            if (removed) {
                view.showMessage("Custom food '" + selected.getName() + "' removed.");
            }
        } else {
            view.showError("Please select a food to remove.");
        }
    }

    /**
     * Exports nutrition data to a CSV file selected by the user.
     */
    public void exportNutritionData() {
        File target = view.promptForCsvExport();
        if (target == null) {
            return;
        }

        try {
            model.exportNutritionData(target.toPath());
            view.showMessage("Nutrition data exported to " + target.getAbsolutePath());
        } catch (IOException e) {
            view.showError("Failed to export data: " + e.getMessage());
        }
    }

    private double resolveMacro(String input, Double fallback, String label) {
        if (input != null && !input.trim().isEmpty()) {
            double value = Double.parseDouble(input.trim());
            if (value < 0) {
                throw new IllegalArgumentException(label + " cannot be negative.");
            }
            return value;
        }
        if (fallback != null) {
            return fallback;
        }
        return 0.0;
    }
}
