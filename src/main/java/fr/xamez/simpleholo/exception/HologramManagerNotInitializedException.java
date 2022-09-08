package fr.xamez.simpleholo.exception;

public class HologramManagerNotInitializedException extends RuntimeException {

    public HologramManagerNotInitializedException() {
        super("HologramManager not initialized. Please call HologramManager#initialize() before using it.");
    }
}
