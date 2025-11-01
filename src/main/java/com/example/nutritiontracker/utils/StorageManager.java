package com.example.nutritiontracker.utils;

import com.example.nutritiontracker.model.AppModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * REQUIREMENT: Singleton Design Pattern.
 *
 * Manages saving and loading of the application's data model.
 *
 * Justification for Singleton:
 * The application requires one, and only one, mechanism for managing data persistence
 * to avoid file conflicts and ensure data consistency. The Singleton pattern provides a
 * single, global point of access to the storage functionality, guaranteeing that all
 * parts of the app use the same instance to save and load data.
 */
public class StorageManager {

    private static volatile StorageManager instance;
    private static final String SAVE_FILE = "nutrition_data.ser";

    private StorageManager() {
        if (instance != null) {
            throw new IllegalStateException("Cannot create new instance of a Singleton.");
        }
    }

    public static StorageManager getInstance() {
        if (instance == null) {
            synchronized (StorageManager.class) {
                if (instance == null) {
                    instance = new StorageManager();
                }
            }
        }
        return instance;
    }

    /**
     * Saves the application model to a file.
     * @param model The model to save.
     * @effects Writes the model object to "nutrition_data.ser".
     */
    public void save(AppModel model) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(model);
            System.out.println("Data saved successfully.");
            System.out.println("Saved " + model.getFoodDatabase().size() + " custom foods.");
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads the application model from a file.
     * @return The loaded AppModel, or a new one if no save file exists.
     * @effects Reads the model object from "nutrition_data.ser" if it exists.
     */
    public AppModel load() {
        File file = new File(SAVE_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                AppModel model = (AppModel) ois.readObject();
                System.out.println("Data loaded successfully.");
                System.out.println("Loaded " + model.getFoodDatabase().size() + " custom foods.");
                return model;
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading data, creating new model: " + e.getMessage());
                e.printStackTrace();
                // Delete corrupted file
                boolean deleted = file.delete();
                if (deleted) {
                    System.out.println("Deleted corrupted data file.");
                }
                return new AppModel();
            }
        }
        System.out.println("No existing data file found, creating new model.");
        return new AppModel(); // Return new model if no file
    }
}
