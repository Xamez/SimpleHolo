package fr.xamez.simpleholo.exception;

import fr.xamez.simpleholo.hologram.Hologram;

public class HologramSpacingOutOfBoundException extends RuntimeException {

    public HologramSpacingOutOfBoundException(float spacing) {
        super("Spacing out of bound: " + spacing + " (must be between " + Hologram.MINIMUM_SPACING + " and " + Hologram.MAXIMUM_SPACING + ")");
    }
}
