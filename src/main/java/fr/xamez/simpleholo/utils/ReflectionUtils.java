package fr.xamez.simpleholo.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class ReflectionUtils {

    private static String version;

    public static String getVersion() {
        if (version == null) version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];;
        return version;
    }

    public static Class<?> getClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class " + className + " not found");
        }
    }

    public static Object createEntityItem(Location location, ItemStack itemStack) {
        try {
            final Class<?> entityItemClazz = getClass("net.minecraft.world.entity.item.EntityItem");
            final Class<?> nmWorldClazz = getClass("net.minecraft.world.level.World");
            final Object nmWorld = createNMWorld(location.getWorld());
            final Object nmsItemStack = createNMSItemstack(itemStack);
            final Constructor<?> entityItemConstructor = entityItemClazz.getDeclaredConstructor(nmWorldClazz, double.class, double.class, double.class, nmsItemStack.getClass());
            return entityItemConstructor.newInstance(nmWorld, location.getX(), location.getY(), location.getZ(), nmsItemStack);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Object createNMWorld(World world) {
        try {
            final Method getHandleMethod = world.getClass().getMethod("getHandle");
            return getHandleMethod.invoke(world);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static Object createNMSItemstack(ItemStack itemStack) {
        try {
            final Class<?> craftItemStackClazz = getClass("org.bukkit.craftbukkit." + getVersion() + ".inventory.CraftItemStack");
            final Method asNMSCopyMethod = craftItemStackClazz.getDeclaredMethod("asNMSCopy", ItemStack.class);
            return asNMSCopyMethod.invoke(null, itemStack);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static int generateEntityId() {
        final Class<?> entityClazz = getClass("net.minecraft.world.entity.Entity");
        for (Field f : entityClazz.getDeclaredFields())
            if (f.getType().equals(AtomicInteger.class)) {
                f.setAccessible(true);
                try {
                    final AtomicInteger value = (AtomicInteger) f.get(null);
                    value.incrementAndGet();
                    return value.get();
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Unable to get entity id");
                }
            }
        return new Random().nextInt() * Integer.MAX_VALUE; // if the field is not found or something goes wrong, return a random value
    }

    public static Object getDataWatcher(Object entityItem) {
        try {
            final Class<?> entityItemClazz = getClass("net.minecraft.world.entity.item.EntityItem");
            final Method getDataWatcherMethod = entityItemClazz.getDeclaredMethod("ai"); // getDataWatcher
            return getDataWatcherMethod.invoke(entityItem);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
