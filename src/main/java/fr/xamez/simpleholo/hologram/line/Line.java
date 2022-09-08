package fr.xamez.simpleholo.hologram.line;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import fr.xamez.simpleholo.hologram.Hologram;
import fr.xamez.simpleholo.hologram.HologramManager;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

    default PacketContainer[] createDefaultPackets(int entityId, Location location, UUID holoUUID, EntityType entityType, WrappedDataWatcher metadata, boolean isEntityLiving) {
        // Spawn entity
        final PacketType packetType = isEntityLiving ? PacketType.Play.Server.SPAWN_ENTITY_LIVING : PacketType.Play.Server.SPAWN_ENTITY;
        final PacketContainer entitySpawnPacketContainer = HologramManager.getProtocolManager().createPacket(packetType);
        entitySpawnPacketContainer.getModifier().writeDefaults();
        entitySpawnPacketContainer.getIntegers().write(0, entityId);
        entitySpawnPacketContainer.getEntityTypeModifier().write(0, entityType);
        entitySpawnPacketContainer.getUUIDs().write(0, holoUUID);
        entitySpawnPacketContainer.getDoubles()
                .write(0, location.getX())
                .write(1, location.getY())
                .write(2, location.getZ());

        // Update metadata
        final PacketContainer entityMetaDataPacketContainer = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        entityMetaDataPacketContainer.getModifier().writeDefaults();
        entityMetaDataPacketContainer.getIntegers().write(0, entityId);
        entityMetaDataPacketContainer.getWatchableCollectionModifier().write(0, metadata.getWatchableObjects());

        return new PacketContainer[] { entitySpawnPacketContainer, entityMetaDataPacketContainer };
    }

    int apply(Hologram hologram, Location location, Player... viewers);
}
