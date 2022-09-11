package fr.xamez.simpleholo.hologram.view;

import fr.xamez.simpleholo.hologram.Hologram;
import fr.xamez.simpleholo.hologram.HologramManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class HologramViewer {

    private final static Map<Player, List<Hologram>> HOLOGRAM_VIEWS = new HashMap<>();

    public static void addView(Player player, Hologram hologram){
        HologramManager.getInstance().createHologramPacket(hologram, player);
        if (HOLOGRAM_VIEWS.get(player) == null)
            HOLOGRAM_VIEWS.put(player, new ArrayList<>(Collections.singletonList(hologram)));
        else
            HOLOGRAM_VIEWS.get(player).add(hologram);
    }

    public static void removeView(Player player, Hologram hologram){
        if (HOLOGRAM_VIEWS.get(player) == null) return;
        HologramManager.getInstance().destroyHologramPacket(hologram, player);
        HOLOGRAM_VIEWS.get(player).remove(hologram);
    }

    public static boolean isViewing(Player player, Hologram hologram){
        if (HOLOGRAM_VIEWS.get(player) == null) return false;
        return HOLOGRAM_VIEWS.get(player).contains(hologram);
    }

    public static List<Player> getViewersList(Hologram hologram) {
        return Arrays.stream(getViewers(hologram)).toList();
    }

    public static Player[] getViewers(Hologram hologram) {
        return HOLOGRAM_VIEWS.entrySet().stream()
                .filter(entry -> entry.getValue().contains(hologram))
                .map(Map.Entry::getKey)
                .toArray(Player[]::new);
    }

    public static void addViewAll(Hologram hologram){
        for (Player player : Bukkit.getOnlinePlayers()) { // code duplication but it's more efficient than calling addView
                                                          // that will create a packet for each player instead of 1 for all
            if (HOLOGRAM_VIEWS.get(player) == null)
                HOLOGRAM_VIEWS.put(player, new ArrayList<>(Collections.singletonList(hologram)));
            else
                HOLOGRAM_VIEWS.get(player).add(hologram);
        }
        HologramManager.getInstance().createHologramPacket(hologram, HologramViewer.getViewers(hologram));
    }

    public static void clearViews(Hologram hologram) {
        HologramManager.getInstance().destroyHologramPacket(hologram, HologramViewer.getViewers(hologram));
        for (Player player : HOLOGRAM_VIEWS.keySet()) {
            if (HOLOGRAM_VIEWS.get(player) == null) continue;
            HOLOGRAM_VIEWS.get(player).remove(hologram);
        }
    }

    public static void clearViews(Player player){
        HOLOGRAM_VIEWS.remove(player);
    }

    public static void deletePlayer(Player player) {
        if (HOLOGRAM_VIEWS.get(player) == null) return;
        HOLOGRAM_VIEWS.remove(player);
    }
}
