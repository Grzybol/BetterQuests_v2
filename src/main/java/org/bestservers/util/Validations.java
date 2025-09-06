package org.bestservers.util;

/**
 * Validation utilities for config and quest definitions.
 */
public class Validations {
    public static boolean isValidMaterial(String materialName) {
        try {
            org.bukkit.Material.valueOf(materialName);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    public static boolean isPositive(int value) {
        return value > 0;
    }
}
