package fr.xamez.simpleholo;

import fr.xamez.simpleholo.events.HologramInteractEvent;
import fr.xamez.simpleholo.hologram.Hologram;
import fr.xamez.simpleholo.hologram.HologramManager;
import fr.xamez.simpleholo.hologram.line.Line;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class TestPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        HologramManager.initialize(this);
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void test(PlayerInteractEvent e) {
        if (e.getItem() == null || !e.getItem().getType().equals(Material.DIAMOND_SWORD)) return;
        Hologram hologram = new Hologram(e.getPlayer().getLocation().add(0, 1.5, 0), "§aCeci est un test !", "§bDeuxième ligne", "Une troisième pour bien tester :)");
        hologram.addLine(Line.of()); // Empty line
        //hologram.addLine(new ItemStack(Material.REDSTONE_BLOCK));
        /*getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            hologram.updateLine(0, Line.of("§c" + Math.random() * 100000));
            Material material = Material.values()[new Random().nextInt(Material.values().length)];
            if (!material.isItem()) material = Material.STONE;
            hologram.updateLine(1, Line.of(new ItemStack(material)));
        }, 0L, 1L);*/
        HologramManager.getInstance().createHologram(hologram);
    }

    @EventHandler
    public void interact(HologramInteractEvent e){
        if (!e.getHand().equals(EquipmentSlot.HAND)) return;
        e.getPlayer().sendMessage("CLICKED ON: " + e.getLine());
    }

    @Override
    public void onDisable() {
        HologramManager.getInstance().removeHolograms();
    }
}
