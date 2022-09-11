package fr.xamez.simpleholo.hologram.line;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import fr.xamez.simpleholo.hologram.Hologram;
import fr.xamez.simpleholo.hologram.HologramManager;
import fr.xamez.simpleholo.hologram.view.HologramViewer;
import fr.xamez.simpleholo.utils.ReflectionUtils;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public record StringLine(String text) implements Line {

    @Override
    public int apply(Hologram hologram, Location location, Player... viewers) {
        final int entityId = ReflectionUtils.generateEntityId();
        final PacketContainer entityPacket = createEntityPacket(entityId, location, EntityType.ARMOR_STAND);

        final WrappedDataWatcher metadata = generateDataWatcher();
        sendEntitySpawnPacket(entityId, entityPacket, metadata, viewers);
        return entityId;
    }

    private WrappedDataWatcher generateDataWatcher() {
        final WrappedDataWatcher metadata = new WrappedDataWatcher();
        final WrappedDataWatcher.Serializer boolSerializer = WrappedDataWatcher.Registry.get(Boolean.class);
        final WrappedDataWatcher.Serializer byteSerializer = WrappedDataWatcher.Registry.get(Byte.class);
        final WrappedDataWatcher.Serializer chatComponentSerializer = WrappedDataWatcher.Registry.getChatComponentSerializer(true);
        final Optional<Object> customName = Optional.of(WrappedChatComponent.fromChatMessage(text)[0].getHandle());
        metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, byteSerializer), (byte) 0x20); // invisible
        metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, chatComponentSerializer), customName); // custom name
        metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, boolSerializer), true); // custom name visible
        metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(5, boolSerializer), true); // gravity
        metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(15, byteSerializer), (byte) (0x08 | 0x01)); // small, no BasePlate (0x10 marker)
        return metadata;
    }
}
