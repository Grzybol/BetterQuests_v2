package org.bestservers.service;

import java.util.UUID;

/**
 * Handles economy payouts using Vault.
 */
public class EconomyService {
    private net.milkbowl.vault.economy.Economy economy;
    private org.bestservers.util.PluginLogger pluginLogger;

    public void setLogger(org.bestservers.util.PluginLogger pluginLogger) {
        this.pluginLogger = pluginLogger;
    }

    public void setEconomy(net.milkbowl.vault.economy.Economy economy) {
        this.economy = economy;
    }
    /**
     * Pays the player the given amount. Returns false if Vault is not available.
     */
    public boolean pay(UUID player, double amount) {
        if (pluginLogger != null) {
            pluginLogger.log(org.bestservers.util.PluginLogger.LogLevel.INFO, "EconomyService: pay called for player=" + player + ", amount=" + amount);
        }
        if (economy == null) {
            if (pluginLogger != null) {
                pluginLogger.log(org.bestservers.util.PluginLogger.LogLevel.ERROR, "EconomyService: Vault Economy is null, cannot pay.");
            }
            return false;
        }
        org.bukkit.OfflinePlayer offlinePlayer = org.bukkit.Bukkit.getOfflinePlayer(player);
        boolean success = economy.depositPlayer(offlinePlayer, amount).transactionSuccess();
        if (pluginLogger != null) {
            pluginLogger.log(org.bestservers.util.PluginLogger.LogLevel.INFO, "EconomyService: depositPlayer result=" + success);
        }
        return success;
    }
}
