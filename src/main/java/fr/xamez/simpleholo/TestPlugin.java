package fr.xamez.simpleholo;

import fr.xamez.simpleholo.event.HologramInteractEvent;
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
        if (e.getItem() == null) return;
        if (e.getItem().getType() == Material.NETHER_STAR) {
            e.getPlayer().performCommand("plugman reload SimpleHolo");
        } else if (e.getItem().getType() == Material.DIAMOND_SWORD) {
            Hologram hologram = new Hologram(e.getPlayer().getLocation()/*.add(0, 1.5, 0)*/, Line.of(new ItemStack(Material.COMMAND_BLOCK)), Line.of(new ItemStack(Material.REDSTONE_BLOCK)), Line.of(), Line.of("§aCeci est un test !"), Line.of("§6Une autre ligne!"));
            getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
                //hologram.updateLine(1, Line.of("§c" + Math.random() * 100000));
                Material material = Material.values()[new Random().nextInt(Material.values().length)];
                if (!material.isItem() || material.isAir()) material = Material.STONE;
                hologram.updateLine(1, Line.of(new ItemStack(material)));
            }, 0L, 1L);
            HologramManager.getInstance().createHologram(hologram);
        }
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
