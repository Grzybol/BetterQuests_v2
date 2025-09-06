package org.bestservers.util;

import java.text.DecimalFormat;

/**
 * Utility for formatting currency and numbers.
 */
public class Formatting {
    private static final DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");
    public static String formatCurrency(double amount) {
        return "$" + currencyFormat.format(amount);
    }
}
