package etu.seinksansdoozebank.dechetri.model.observable;

public interface Observable<T> {
    void addObserver(T observer);

    void removeObserver(T observer);

    void notifyObservers();
}
