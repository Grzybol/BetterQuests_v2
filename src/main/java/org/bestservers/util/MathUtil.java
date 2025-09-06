package org.bestservers.util;

/**
 * Math utilities for pow and rounding.
 */
public class MathUtil {
    public static double pow(double base, int exp) {
        return Math.pow(base, exp);
    }
    public static double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
}
