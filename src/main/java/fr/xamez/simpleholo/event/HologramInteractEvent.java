package fr.xamez.simpleholo.event;

import fr.xamez.simpleholo.hologram.Hologram;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.EquipmentSlot;

public class HologramInteractEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;

    private final EquipmentSlot hand;
    private final Hologram hologram;
    private final String line;

    public HologramInteractEvent(Player player, EquipmentSlot hand, Hologram hologram, String line) {
        this.player = player;
        this.hand = hand;
        this.hologram = hologram;
        this.line = line;
    }


    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return player;
    }
    
    public EquipmentSlot getHand() {
        return hand;
    }

    public Hologram getHologram() {
        return hologram;
    }

    public String getLine() {
        return line;
    }

}
