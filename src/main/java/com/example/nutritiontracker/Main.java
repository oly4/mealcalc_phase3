package com.example.nutritiontracker;

import com.example.nutritiontracker.controller.AppController;
import com.example.nutritiontracker.model.AppModel;
import com.example.nutritiontracker.utils.StorageManager;
import com.example.nutritiontracker.view.MainView;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. Load the Model using the Singleton StorageManager.
            StorageManager storageManager = StorageManager.getInstance();
            AppModel model = storageManager.load();

            // 2. Create the View.
            MainView view = new MainView();

            // 3. Create the Controller.
            AppController controller = new AppController(model, view);

            // 4. Wire everything together.
            view.setController(controller);
            model.addObserver(view);

            // 5. Initial update of the view with loaded data.
            model.notifyObservers();

            // 6. Add a shutdown hook to save data automatically on exit.
            Runtime.getRuntime().addShutdownHook(new Thread(() -> storageManager.save(model)));

            // 7. Make the application visible.
            view.setVisible(true);
        });
    }
}
