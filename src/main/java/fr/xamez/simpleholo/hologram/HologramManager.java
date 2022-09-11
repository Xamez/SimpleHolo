package fr.xamez.simpleholo.hologram;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import fr.xamez.simpleholo.exception.HologramManagerNotInitializedException;
import fr.xamez.simpleholo.hologram.line.Line;
import fr.xamez.simpleholo.hologram.view.HologramViewer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class HologramManager {

    private JavaPlugin plugin;

    private static ProtocolManager protocolManager;

    private static HologramManager instance;

    public static HologramManager getInstance() {
        if (instance == null)
            throw new HologramManagerNotInitializedException();
        return instance;
    }

    public static HashSet<Hologram> HOLOGRAMS = new HashSet<>();

    public void removeHologram(Hologram hologram) {
        HOLOGRAMS.remove(hologram);
        HologramViewer.clearViews(hologram);
        hologram.getEntitiesId().clear();
    }

    public void destroyHologramPacket(Hologram hologram, Player... viewers) {
        final PacketContainer destroyPackContainer = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        destroyPackContainer.getModifier().writeDefaults();
        destroyPackContainer.getIntLists().write(0, hologram.getEntitiesId().stream().filter(i -> i != -1).mapToInt(i -> i).boxed().collect(Collectors.toList()));
        try {
            for (Player viewer : viewers) protocolManager.sendServerPacket(viewer.getPlayer(), destroyPackContainer);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void destroyLinePacket(Hologram hologram, Line line, Player... viewers) {
        final PacketContainer destroyPackContainer = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        destroyPackContainer.getModifier().writeDefaults();
        destroyPackContainer.getIntLists().write(0, Collections.singletonList(hologram.getEntitiesId().get(hologram.getLines().indexOf(line))));
        try {
            for (Player viewer : viewers) protocolManager.sendServerPacket(viewer.getPlayer(), destroyPackContainer);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void removeHolograms() {
        new HashSet<>(HOLOGRAMS).forEach(this::removeHologram);
    }

    public void createHologram(Hologram hologram) {
        HOLOGRAMS.add(hologram);
        HologramViewer.addViewAll(hologram); // by default all players can see the hologram
        hologram.markAsCreated();
    }
    public void createHologramPacket(Hologram hologram, Player... viewers) {
        Location hologramLocation = hologram.getLocation().clone();
        for (Line line : hologram.getLines())
            hologram.getEntitiesId().add(createLinePacket(hologram, line, hologramLocation, viewers));
    }

    public int createLinePacket(Hologram hologram, Line line, Location hologramLocation, Player... viewers) {
        int entityId = line.apply(hologram, hologramLocation, viewers);
        hologramLocation.subtract(0, hologram.getSpacing(), 0);
        return entityId;
    }

    public void editLine(Hologram hologram, int index, Line newLine) {
        if (index < 0 || index >= hologram.getLines().size())
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for this hologram. Max index is " + (hologram.getLines().size() - 1));
        if (!hologram.getEntitiesId().isEmpty()) {
            destroyLinePacket(hologram, hologram.getLines().get(index), HologramViewer.getViewers(hologram));
            int entityId = createLinePacket(hologram, newLine, hologram.getLocation().clone().subtract(0, index * hologram.getSpacing(), 0), HologramViewer.getViewers(hologram));
            hologram.getEntitiesId().set(index, entityId);
        }
        hologram.getLines().set(index, newLine);
    }

    public void createHolograms() {
        new HashSet<>(HOLOGRAMS).forEach(this::createHologram);
    }

    public void reloadHologram(Hologram hologram) {
        removeHologram(hologram);
        createHologram(hologram);
    }

    public void reloadHolograms() {
        HOLOGRAMS.forEach(this::reloadHologram);
    }

    public Optional<Hologram> getHologramFrom(Location location) {
        return HOLOGRAMS.stream().filter(h -> h.getLocation().equals(location)).findFirst();
    }

    @Deprecated
    public Optional<Hologram> getHologramFrom(Location location, double offset) {
        return getHologramFrom(location).or(() -> HOLOGRAMS.stream().filter(h -> h.getLocation().distance(location) <= offset).findFirst());
    }

    private void registerHologramInteraction() {
        protocolManager.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                PacketContainer packetContainer = event.getPacket();
                int entityId = packetContainer.getIntegers().read(0);
                HOLOGRAMS.stream().filter(h -> h.getEntitiesId().contains(entityId)).findFirst().ifPresent(hologram -> {
                    hologram.getEntitiesId().stream().filter(i -> i == entityId).findFirst().ifPresent(ignored -> {
                        final int lineNumber = hologram.getEntitiesId().indexOf(entityId);
                        final Line line = hologram.getLines().get(lineNumber);
                        EnumWrappers.Hand hand = packetContainer.getEnumEntityUseActions().read(0).getHand();
                        EnumWrappers.EntityUseAction action = packetContainer.getEnumEntityUseActions().read(0).getAction();

                        //Bukkit.getPluginManager().callEvent(new HologramInteractEvent(hologram, line, event.getPlayer()));
                    });
                });
            }
        });
    }

    public static void initialize(JavaPlugin plugin) {
        instance = new HologramManager();
        instance.plugin = plugin;
        protocolManager = ProtocolLibrary.getProtocolManager();
        plugin.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void playerQuit(PlayerQuitEvent e) {
                HologramViewer.deletePlayer(e.getPlayer());
            }
        }, plugin);
    }

    public static ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }
}
