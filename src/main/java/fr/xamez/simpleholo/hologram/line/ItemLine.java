package fr.xamez.simpleholo.hologram.line;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import fr.xamez.simpleholo.hologram.Hologram;
import fr.xamez.simpleholo.hologram.HologramManager;
import fr.xamez.simpleholo.hologram.view.HologramViewer;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

public record ItemLine(ItemStack itemStack) implements Line {

    @Override
    public int apply(Hologram hologram, Location location, Player... viewers) {
        final int entityId = (int) System.currentTimeMillis();
        final WrappedDataWatcher metadata = new WrappedDataWatcher();
        final WrappedDataWatcher.Serializer boolSerializer = WrappedDataWatcher.Registry.get(Boolean.class);
        //final WrappedDataWatcher.Serializer itemStackSerializer = WrappedDataWatcher.Registry.getItemStackSerializer(false);
        metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(5, boolSerializer), true); // gravity
        //metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(6, itemStackSerializer), itemStack); // item stack

        final PacketContainer[] packets = createDefaultPackets(entityId, location, hologram.getUUID(), EntityType.DROPPED_ITEM, metadata, false);
        packets[0].getItemModifier().write(0, itemStack);

        try {
            for (Player viewer : viewers)
                for (PacketContainer packet : packets)
                    HologramManager.getProtocolManager().sendServerPacket(viewer, packet);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return entityId;


        /*item.setItemStack(itemStack);
        item.setVelocity(new Vector());
        item.setPickupDelay(Integer.MAX_VALUE);
        return item;*/
    }
}
