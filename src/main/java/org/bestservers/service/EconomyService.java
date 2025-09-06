package org.bestservers.service;

import java.util.UUID;

/**
 * Handles economy payouts using Vault.
 */
public class EconomyService {
    private net.milkbowl.vault.economy.Economy economy;

    public void setEconomy(net.milkbowl.vault.economy.Economy economy) {
        this.economy = economy;
    }
    /**
     * Pays the player the given amount. Returns false if Vault is not available.
     */
    public boolean pay(UUID player, double amount) {
    if (economy == null) return false;
    org.bukkit.OfflinePlayer offlinePlayer = org.bukkit.Bukkit.getOfflinePlayer(player);
    return economy.depositPlayer(offlinePlayer, amount).transactionSuccess();
    }
}
