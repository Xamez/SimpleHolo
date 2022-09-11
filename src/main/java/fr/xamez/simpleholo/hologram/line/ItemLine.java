package fr.xamez.simpleholo.hologram.line;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import fr.xamez.simpleholo.hologram.Hologram;
import fr.xamez.simpleholo.utils.ReflectionUtils;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public record ItemLine(ItemStack itemStack) implements Line {

    @Override
    public int apply(Hologram hologram, Location location, Player... viewers) {
        final int entityId = ReflectionUtils.generateEntityId();
        final PacketContainer entityPacket = createEntityPacket(entityId, location, EntityType.DROPPED_ITEM);
        entityPacket.getIntegers().write(0, entityId);
        entityPacket.getDoubles().write(1, location.getY() + 1);

        final WrappedDataWatcher metadata = generateDataWatcher(location);
        sendEntitySpawnPacket(entityId, entityPacket, metadata, viewers);
        return entityId;
    }

    private WrappedDataWatcher generateDataWatcher(Location location) {
        final Object entityItem = ReflectionUtils.createEntityItem(location, itemStack);
        final WrappedDataWatcher metadata = new WrappedDataWatcher(ReflectionUtils.getDataWatcher(entityItem));
        final WrappedDataWatcher.Serializer boolSerializer = WrappedDataWatcher.Registry.get(Boolean.class);
        //final WrappedDataWatcher.Serializer itemStackSerializer = WrappedDataWatcher.Registry.getItemStackSerializer(false);
        metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(5, boolSerializer), true); // gravity
        //metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(6, itemStackSerializer), CraftItemStack.asNMSCopy(itemStack)); // item stack
        return metadata;
    }
}
