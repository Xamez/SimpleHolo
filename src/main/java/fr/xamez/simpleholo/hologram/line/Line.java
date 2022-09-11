package fr.xamez.simpleholo.hologram.line;

import com.comphenix.protocol.PacketType;
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
import java.util.List;
import java.util.UUID;

public interface Line {

    static Line of(){
        return new EmptyLine();
    }

    static Line of(String text){
        return new StringLine(text);
    }

    static Line of(ItemStack itemStack){
        return new ItemLine(itemStack);
    }

    default PacketContainer createEntityPacket(int entityId, Location location, EntityType entityType) {
        final PacketContainer entitySpawnPacketContainer = HologramManager.getProtocolManager().createPacket(PacketType.Play.Server.SPAWN_ENTITY);
        entitySpawnPacketContainer.getIntegers().write(0, entityId);
        entitySpawnPacketContainer.getEntityTypeModifier().write(0, entityType);
        entitySpawnPacketContainer.getUUIDs().write(0, UUID.randomUUID());
        entitySpawnPacketContainer.getDoubles()
                .write(0, location.getX())
                .write(1, location.getY())
                .write(2, location.getZ());
        return entitySpawnPacketContainer;
    }

    default void sendEntitySpawnPacket(int entityId, PacketContainer entityPacketContainer, WrappedDataWatcher metadata, Player... viewers) {
        final PacketContainer entityMetaDataPacketContainer = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        entityMetaDataPacketContainer.getIntegers().write(0, entityId);
        entityMetaDataPacketContainer.getWatchableCollectionModifier().write(0, metadata.getWatchableObjects());
        try {
            for (Player viewer : viewers) {
                HologramManager.getProtocolManager().sendServerPacket(viewer, entityPacketContainer);
                HologramManager.getProtocolManager().sendServerPacket(viewer, entityMetaDataPacketContainer);
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    int apply(Hologram hologram, Location location, Player... viewers);
}
