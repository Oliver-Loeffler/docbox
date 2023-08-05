package net.raumzeitfalle.docdrop.utils;

public class OS {
    public static boolean isLinux() {
        return !System.getProperty("os.name").toLowerCase().contains("win");
    }
}
