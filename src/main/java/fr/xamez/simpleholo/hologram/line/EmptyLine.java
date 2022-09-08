package fr.xamez.simpleholo.hologram.line;

import fr.xamez.simpleholo.hologram.Hologram;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class EmptyLine implements Line {

    @Override
    public int apply(Hologram hologram, Location location, Player... viewers) { return -1; }
}
