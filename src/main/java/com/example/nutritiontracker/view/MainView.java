package com.example.nutritiontracker.view;

import com.example.nutritiontracker.controller.AppController;
import com.example.nutritiontracker.model.AppModel;
import com.example.nutritiontracker.model.Food;
import com.example.nutritiontracker.model.Meal;
import com.example.nutritiontracker.model.MealLog;
import com.example.nutritiontracker.model.Observer;
import com.example.nutritiontracker.model.RequirementCalculator.DailyRequirements;
import com.example.nutritiontracker.model.UserProfile;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.util.List;

/**
 * REQUIREMENT: The View in MVC.
 * REQUIREMENT: Observer Pattern (The Observer).
 *
 * The main UI window for the application. It observes the AppModel and
 * updates its display whenever the model's state changes.
 */
public class MainView extends JFrame implements Observer {

    private AppController controller;

    // Profile Components
    private JTextField weightField;
    private JTextField heightField;
    private JTextField ageField;
    private JComboBox<UserProfile.ActivityLevel> activityLevelBox;
    private JComboBox<UserProfile.Goal> goalBox;

    // Meal Components
    private JTextField mealNameField;
    private JTextField proteinField;
    private JTextField carbsField;
    private JTextField fatField;
    private JTextField caloriesField;
    private DefaultListModel<Meal> mealListModel;
    private JList<Meal> mealList;

    private DefaultListModel<Food> customFoodListModel;
    private JList<Food> customFoodList;

    // Progress Components
    private JProgressBar caloriesBar;
    private JProgressBar proteinBar;
    private JProgressBar carbsBar;
    private JProgressBar fatBar;
    private JLabel remainingLabel;

    public MainView() {
        setTitle("Nutrition Tracker");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Layout ---
        setLayout(new BorderLayout(10, 10));

        JPanel profilePanel = createProfilePanel();
        JPanel mealPanel = createMealPanel();
        JPanel progressPanel = createProgressPanel();
        JPanel customFoodPanel = createCustomFoodPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mealPanel, progressPanel);
        splitPane.setResizeWeight(0.6);

        add(profilePanel, BorderLayout.WEST);
        add(splitPane, BorderLayout.CENTER);
        add(customFoodPanel, BorderLayout.EAST);
    }

    public void setController(AppController controller) {
        this.controller = controller;
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));
        panel.setBorder(new TitledBorder("User Profile"));

        weightField = new JTextField(5);
        heightField = new JTextField(5);
        ageField = new JTextField(5);
        activityLevelBox = new JComboBox<>(UserProfile.ActivityLevel.values());
        goalBox = new JComboBox<>(UserProfile.Goal.values());
        JButton updateProfileButton = new JButton("Update Profile");

        panel.add(createLabeledComponent("Weight (kg):", weightField));
        panel.add(createLabeledComponent("Height (cm):", heightField));
        panel.add(createLabeledComponent("Age:", ageField));
        panel.add(createLabeledComponent("Activity:", activityLevelBox));
        panel.add(createLabeledComponent("Goal:", goalBox));
        panel.add(updateProfileButton);

        updateProfileButton.addActionListener(e -> controller.updateProfile());

        return panel;
    }

    private JPanel createMealPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new TitledBorder("Daily Meals"));

        mealListModel = new DefaultListModel<>();
        mealList = new JList<>(mealListModel);
        panel.add(new JScrollPane(mealList), BorderLayout.CENTER);

        mealList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    Meal selected = mealList.getSelectedValue();
                    if (selected != null) {
                        mealNameField.setText(selected.getName());
                        proteinField.setText(String.valueOf(selected.getProtein()));
                        carbsField.setText(String.valueOf(selected.getCarbs()));
                        fatField.setText(String.valueOf(selected.getFat()));
                        caloriesField.setText(String.valueOf(selected.getCalories()));
                    }
                }
            }
        });

        JPanel entryPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        mealNameField = new JTextField();
        proteinField = new JTextField();
        carbsField = new JTextField();
        fatField = new JTextField();
        caloriesField = new JTextField();
        entryPanel.add(new JLabel("Name:"));
        entryPanel.add(mealNameField);
        entryPanel.add(new JLabel("Protein (g):"));
        entryPanel.add(proteinField);
        entryPanel.add(new JLabel("Carbs (g):"));
        entryPanel.add(carbsField);
        entryPanel.add(new JLabel("Fat (g):"));
        entryPanel.add(fatField);
        entryPanel.add(new JLabel("Calories:"));
        entryPanel.add(caloriesField);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addMealButton = new JButton("Add Meal");
        JButton removeMealButton = new JButton("Remove Selected");
        JButton updateMealButton = new JButton("Update Selected");
        JButton saveAsFoodButton = new JButton("Save as Food");
        buttonPanel.add(addMealButton);
        buttonPanel.add(removeMealButton);
        buttonPanel.add(updateMealButton);
        buttonPanel.add(saveAsFoodButton);

        addMealButton.addActionListener(e -> controller.addMeal());
        removeMealButton.addActionListener(e -> controller.removeMeal());
        updateMealButton.addActionListener(e -> controller.updateMeal());
        saveAsFoodButton.addActionListener(e -> controller.saveAsCustomFood());

        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.add(entryPanel, BorderLayout.CENTER);
        bottomContainer.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(bottomContainer, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createProgressPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.setBorder(new TitledBorder("Daily Progress"));

        caloriesBar = createProgressBar("Calories");
        proteinBar = createProgressBar("Protein");
        carbsBar = createProgressBar("Carbs");
        fatBar = createProgressBar("Fat");

        remainingLabel = new JLabel("Remaining: ...");
        remainingLabel.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(caloriesBar);
        panel.add(proteinBar);
        panel.add(carbsBar);
        panel.add(fatBar);
        panel.add(remainingLabel);

        JButton exportButton = new JButton("Export CSV");
        exportButton.addActionListener(e -> {
            if (controller != null) {
                controller.exportNutritionData();
            } else {
                showError("Controller not ready.");
            }
        });
        panel.add(exportButton);

        return panel;
    }

    // --- Getters for Controller ---
    public UserProfile getProfileInput() {
        UserProfile profile = new UserProfile();
        profile.setWeightKg(Double.parseDouble(weightField.getText()));
        profile.setHeightCm(Double.parseDouble(heightField.getText()));
        profile.setAge(Integer.parseInt(ageField.getText()));
        profile.setActivityLevel((UserProfile.ActivityLevel) activityLevelBox.getSelectedItem());
        profile.setGoal((UserProfile.Goal) goalBox.getSelectedItem());
        return profile;
    }

    public String[] getMealInput() {
        return new String[]{
            mealNameField.getText(),
            proteinField.getText(),
            carbsField.getText(),
            fatField.getText(),
            caloriesField.getText()
        };
    }

    public Meal getSelectedMeal() {
        return mealList.getSelectedValue();
    }

    public void clearMealInput() {
        mealNameField.setText("");
        proteinField.setText("");
        carbsField.setText("");
        fatField.setText("");
        caloriesField.setText("");
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    // --- Update method for Observer pattern ---
    @Override
    public void update(AppModel model) {
        UserProfile profile = model.getUserProfile();
        weightField.setText(String.valueOf(profile.getWeightKg()));
        heightField.setText(String.valueOf(profile.getHeightCm()));
        ageField.setText(String.valueOf(profile.getAge()));
        activityLevelBox.setSelectedItem(profile.getActivityLevel());
        goalBox.setSelectedItem(profile.getGoal());

        List<Meal> meals = model.getMealLog().getMeals();
        mealListModel.clear();
        meals.forEach(mealListModel::addElement);

        List<Food> customFoods = model.getFoodDatabase().getAllFoods();
        customFoodListModel.clear();
        customFoods.forEach(customFoodListModel::addElement);

        DailyRequirements reqs = model.getDailyRequirements();
        MealLog totals = model.getMealLog();

        updateProgressBar(caloriesBar, totals.getTotalCalories(), reqs.calories());
        updateProgressBar(proteinBar, totals.getTotalProtein(), reqs.protein());
        updateProgressBar(carbsBar, totals.getTotalCarbs(), reqs.carbs());
        updateProgressBar(fatBar, totals.getTotalFat(), reqs.fat());

        double remainingCals = totals.remainingCalories(reqs);
        remainingLabel.setText(String.format("Remaining Calories: %.0f", remainingCals));
    }

    // --- Helper methods for UI creation ---
    private JPanel createLabeledComponent(String labelText, JComponent component) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel(labelText));
        panel.add(component);
        return panel;
    }

    private JProgressBar createProgressBar(String name) {
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setStringPainted(true);
        bar.setName(name); // Use setName to identify the bar
        return bar;
    }

    private void updateProgressBar(JProgressBar bar, double current, double goal) {
        bar.setMaximum((int) Math.round(goal));
        bar.setValue((int) Math.round(current));
        bar.setString(String.format("%s: %.0f / %.0f", bar.getName(), current, goal));
    }

    // --- Custom Food Panel ---
    private JPanel createCustomFoodPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new TitledBorder("My Custom Foods"));

        customFoodListModel = new DefaultListModel<>();
        customFoodList = new JList<>(customFoodListModel);
        panel.add(new JScrollPane(customFoodList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        JButton loadButton = new JButton("Load to Meal");
        JButton removeButton = new JButton("Remove Food");
        JButton saveButton = new JButton("Save as Food");
        JLabel infoLabel = new JLabel("<html><center>Save foods for<br>quick reuse!</center></html>");
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        buttonPanel.add(loadButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(infoLabel);

        loadButton.addActionListener(e -> controller.loadCustomFood());
        removeButton.addActionListener(e -> controller.removeCustomFood());
        saveButton.addActionListener(e -> controller.saveAsCustomFood());

        panel.add(buttonPanel, BorderLayout.SOUTH);
        panel.setPreferredSize(new java.awt.Dimension(250, 0));

        return panel;
    }

    // --- Custom Food Helper Methods ---
    public Food getSelectedCustomFood() {
        return customFoodList.getSelectedValue();
    }

    public void loadFoodIntoMealInput(Food food) {
        mealNameField.setText(food.getName());
        proteinField.setText(String.valueOf(food.getProtein()));
        carbsField.setText(String.valueOf(food.getCarbs()));
        fatField.setText(String.valueOf(food.getFat()));
        caloriesField.setText(String.valueOf(food.getCalories()));
    }

    public File promptForCsvExport() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export Nutrition Data");
        chooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
        chooser.setSelectedFile(new File("nutrition_data.csv"));

        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();
            if (selected == null) {
                return null;
            }
            String absolutePath = selected.getAbsolutePath();
            if (!absolutePath.toLowerCase().endsWith(".csv")) {
                absolutePath += ".csv";
            }
            return new File(absolutePath);
        }
        return null;
    }
}
