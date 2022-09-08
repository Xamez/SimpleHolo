package fr.xamez.simpleholo.utils;

import org.bukkit.Bukkit;

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
            e.printStackTrace();
        }
        return null;
    }

    public static Class<?> getNmClass(String className) {
        return getClass("net.minecraft." + getVersion() + "." + className);
    }

    public static Class<?> getNmsClass(String className) {
        return getClass("net.minecraft.server." + getVersion() + "." + className);
    }

    public static Class<?> getCraftBukkitClass(String className) {
        return getClass("org.bukkit.craftbukkit." + getVersion() + "." + className);
    }



}
