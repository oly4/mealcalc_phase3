package com.example.nutritiontracker.model;

/**
 * Observer component for the application model.
 */
public interface Observer {

    /**
     * Reacts to model changes.
     *
     * @param model updated application model
     * @return nothing
     * @requires {@code model} is non-null
     * @effects allows the observer to refresh its state based on {@code model}
     */
    void update(AppModel model);
}

interface Observable {

    /**
     * Registers an observer.
     *
     * @param observer observer to register
     * @return nothing
     * @requires {@code observer} is non-null
     * @effects adds {@code observer} to the notification list
     */
    void addObserver(Observer observer);

    /**
     * Unregisters an observer.
     *
     * @param observer observer to remove
     * @return nothing
     * @requires {@code observer} is non-null
     * @effects removes {@code observer} from the notification list
     */
    void removeObserver(Observer observer);

    /**
     * Notifies all registered observers.
     *
     * @return nothing
     * @requires nothing
     * @effects triggers {@link Observer#update(AppModel)} on each observer
     */
    void notifyObservers();
}
