package com.example.nutritiontracker.utils;

import com.example.nutritiontracker.model.Meal;
import com.example.nutritiontracker.model.MealLog;
import com.example.nutritiontracker.model.UserProfile;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

/**
 * Utility for exporting nutrition data to CSV.
 */
public final class CsvExporter {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final Locale LOCALE = Locale.US;
    private static final DecimalFormat ONE_DECIMAL_FORMAT = buildFormat("0.0###");
    private static final DecimalFormat ZERO_DECIMAL_FORMAT = buildFormat("0.###");

    private CsvExporter() {
        throw new AssertionError("Utility class should not be instantiated.");
    }

    public static void export(MealLog mealLog, UserProfile profile, Path target) throws IOException {
        Objects.requireNonNull(mealLog, "Meal log is required.");
        Objects.requireNonNull(profile, "User profile is required.");
        Objects.requireNonNull(target, "Target path is required.");

        if (target.getParent() != null) {
            Files.createDirectories(target.getParent());
        }

        try (BufferedWriter writer = Files.newBufferedWriter(
            target,
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING,
            StandardOpenOption.WRITE
        )) {
            writer.write("Log Date,Meal ID,Meal Name,Category,Protein (g),Carbs (g),Fat (g),Calories");
            writer.newLine();

            String logDate = DATE_FORMAT.format(mealLog.getLogDate());

            for (Meal meal : mealLog.getMeals()) {
                String line = String.join(",",
                    escape(logDate),
                    escape(meal.getId().toString()),
                    escape(meal.getName()),
                    escape(meal.getCategoryTag()),
                    formatOneDecimal(meal.getProtein()),
                    formatOneDecimal(meal.getCarbs()),
                    formatOneDecimal(meal.getFat()),
                    formatZeroDecimal(meal.getCalories())
                );
                writer.write(line);
                writer.newLine();
            }

            writer.write(String.join(",",
                "",
                "Totals",
                "",
                "",
                formatOneDecimal(mealLog.getTotalProtein()),
                formatOneDecimal(mealLog.getTotalCarbs()),
                formatOneDecimal(mealLog.getTotalFat()),
                formatZeroDecimal(mealLog.getTotalCalories())
            ));
            writer.newLine();

            writer.newLine();
            writer.write("Weight (kg),Height (cm),Age,Activity Level,Goal");
            writer.newLine();
            writer.write(String.join(",",
                formatOneDecimal(profile.getWeightKg()),
                formatOneDecimal(profile.getHeightCm()),
                Integer.toString(profile.getAge()),
                escape(profile.getActivityLevel().name()),
                escape(profile.getGoal().name())
            ));
        }
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains("\"")) {
            value = value.replace("\"", "\"\"");
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value + "\"";
        }
        return value;
    }

    private static String formatOneDecimal(double value) {
        return ONE_DECIMAL_FORMAT.format(value);
    }

    private static String formatZeroDecimal(double value) {
        return ZERO_DECIMAL_FORMAT.format(value);
    }

    private static DecimalFormat buildFormat(String pattern) {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(LOCALE);
        DecimalFormat format = new DecimalFormat(pattern, symbols);
        format.setGroupingUsed(false);
        return format;
    }
}
