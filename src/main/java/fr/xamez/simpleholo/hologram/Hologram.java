package fr.xamez.simpleholo.hologram;

import fr.xamez.simpleholo.exception.HologramSpacingOutOfBoundException;
import fr.xamez.simpleholo.hologram.line.Line;
import fr.xamez.simpleholo.hologram.line.StringLine;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;

public class Hologram {

    public final static float MINIMUM_SPACING = 0.0f;
    public final static float MAXIMUM_SPACING = 20f; // An arbitrary decision to limit the spacing
    private transient final UUID uuid;
    private List<Line> lines;

    private final List<Integer> entitiesId;
    private float spacing;
    private Location location;
    private boolean created = false;


    private final Consumer<Consumer<Hologram>> hologramUpdater = hologramConsumer -> {
        if (created) {
            HologramManager.getInstance().removeHologram(this);
            hologramConsumer.accept(this);
            HologramManager.getInstance().createHologram(this);
        } else {
            hologramConsumer.accept(this);
        }
    };

    public Hologram(Location location, float spacing, Line... lines) {
        this.uuid = UUID.randomUUID();
        this.lines = new ArrayList<>(List.of(lines));
        this.location = location;
        this.spacing = spacing;
        this.entitiesId = new ArrayList<>();
    }

    public Hologram(Location location, Line... lines) {
        this(location, 0.25f, lines);
    }

    public Hologram(Location location, float spacing, String... lines) {
        this(location, spacing, Arrays.stream(lines).map(StringLine::new).toArray(Line[]::new));
    }

    public Hologram(Location location, String... lines) {
        this(location, 0.25f, lines);
    }

    public void addLine(String text) {
        hologramUpdater.accept(hologram -> hologram.lines.add(Line.of(text)));
    }

    public void addLine(ItemStack itemStack) {
        hologramUpdater.accept(hologram -> hologram.lines.add(Line.of(itemStack)));
    }

    public void addLine(Line line) {
        hologramUpdater.accept(hologram -> hologram.lines.add(line));
    }

    public void removeLine(Line line) {
        hologramUpdater.accept(hologram -> hologram.lines.remove(line));
    }

    public void removeLine(int index) {
        if (index < 0 || index >= lines.size())
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for lines of size " + lines.size());
        hologramUpdater.accept(hologram -> hologram.lines.remove(index));
    }

    public void updateLine(int index, Line line) {
        if (index < 0 || index >= lines.size())
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for lines of size " + lines.size());
        hologramUpdater.accept(hologram -> hologram.lines.set(index, line));
    }

    public void setLines(List<Line> lines) {
        hologramUpdater.accept(hologram -> hologram.lines = lines);
    }

    public void setLines(Line... lines) {
        setLines(Arrays.asList(lines));
    }

    public List<Line> getLines() {
        return lines;
    }

    public void setSpacing(float spacing) {
        if (spacing < MINIMUM_SPACING || spacing > MAXIMUM_SPACING)
            throw new HologramSpacingOutOfBoundException(spacing);
        hologramUpdater.accept(hologram -> hologram.spacing = spacing);
    }

    public float getSpacing() {
        return spacing;
    }

    public void setLocation(Location location) {
        hologramUpdater.accept(hologram -> hologram.location = location);
    }

    public Location getLocation() {
        return location;
    }

    public UUID getUUID() {
        return uuid;
    }

    public List<Integer> getEntitiesId() {
        return entitiesId;
    }

    public void markAsCreated() {
        this.created = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Hologram hologram = (Hologram) o;

        return Objects.equals(uuid, hologram.uuid);
    }

    @Override
    public int hashCode() {
        return uuid != null ? uuid.hashCode() : 0;
    }
}
