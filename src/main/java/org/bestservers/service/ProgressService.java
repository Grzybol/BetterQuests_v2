package org.bestservers.service;

import net.milkbowl.vault.economy.Economy;
import org.bestservers.model.QuestDefinition;
import org.bestservers.model.PlayerQuestProgress;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

/**
 * Service for managing player quest progress and rewards.
 */
public class ProgressService {
    private final org.bestservers.util.PluginLogger pluginLogger;
    private static Economy econ = null;
    /**
     * Gets PlayerQuestProgress for a player and questId, or null if not present.
     */
    public PlayerQuestProgress getProgress(UUID player, String questId) {
        Map<String, PlayerQuestProgress> playerMap = progressCache.get(player);
        if (playerMap == null) return null;
        return playerMap.get(questId);
    }
    public static Economy getEconomy() {
        return econ;
    }

    /**
     * Returns a snapshot of the entire progress cache (for saving).
     */
    public Map<UUID, Map<String, PlayerQuestProgress>> getProgressCacheSnapshot() {
        return new HashMap<>(progressCache);
    }
    private final Map<UUID, Map<String, PlayerQuestProgress>> progressCache = new ConcurrentHashMap<>();
    private EconomyService economyService;

    public ProgressService(EconomyService economyService, org.bestservers.util.PluginLogger pluginLogger,Economy economy) {
        this.economyService = economyService;
        this.pluginLogger = pluginLogger;
        econ = economy;
        pluginLogger.log(org.bestservers.util.PluginLogger.LogLevel.DEBUG, "ProgressService: Initialized");
    }

    public int getRequiredForTier(QuestDefinition def, int tier) {
        if (tier < 1) return 0;
        int base = def.getBaseRequiredItems();
        int add = def.getAdditionalRequiredItemsPerTier();
        return base + add * (tier - 1);
    }

    public double getRewardForTier(QuestDefinition def, int tier) {
        if (tier < 1) return 0.0;
        double base = def.getBaseReward();
        double mult = def.getMoneyTierMultiplier();
        return base * Math.pow(mult, tier - 1);
    }

    /**
     * Applies contribution for a player and quest.
     * @param player player UUID
     * @param def quest definition
     * @param contributed items contributed
     * @return result structure
     */
    public Result applyContribution(UUID player, QuestDefinition def, int contributed) {
        org.bestservers.util.QuestProgressFormatter formatter = new org.bestservers.util.QuestProgressFormatter();
        pluginLogger.log(org.bestservers.util.PluginLogger.LogLevel.DEBUG, "ProgressService: Applying contribution for player " + player + ", quest " + def.getId() + ", contributed " + contributed);
        Map<String, PlayerQuestProgress> playerMap = progressCache.computeIfAbsent(player, k -> new HashMap<>());
        PlayerQuestProgress progress = playerMap.get(def.getId());
        if (progress == null) {
            progress = new PlayerQuestProgress(def.getId(), 1, 0);
            playerMap.put(def.getId(), progress);
        }
        pluginLogger.log(org.bestservers.util.PluginLogger.LogLevel.DEBUG, "ProgressService: Current progress - tier: " + progress.getTier() + ", contributed: " + progress.getContributedCount());
        int tierBefore = progress.getTier();
        int contributedBefore = progress.getContributedCount();
        int required = getRequiredForTier(def, tierBefore);
        int toAdd = Math.min(contributed, required - contributedBefore);
        progress.setContributedCount(contributedBefore + toAdd);
        int tierAfter = tierBefore;
        double rewardPaid = 0.0;
        boolean completedTier = false;
        pluginLogger.log(org.bestservers.util.PluginLogger.LogLevel.DEBUG, "ProgressService: After contribution - tier: " + progress.getTier() + ", contributed: " + progress.getContributedCount() + "/" + required);
        if (progress.getContributedCount() >= required) {
            pluginLogger.log(org.bestservers.util.PluginLogger.LogLevel.INFO, "ProgressService: Player " + player + " completed tier " + tierBefore + " for quest " + def.getId());
            completedTier = true;
            rewardPaid = getRewardForTier(def, tierBefore);
                org.bestservers.util.PluginLogger.LogLevel logLevel;
                //Economy vaultEconomy = getEconomy();
                if (econ != null && rewardPaid > 0.0) {
                    org.bukkit.OfflinePlayer offlinePlayer = org.bukkit.Bukkit.getOfflinePlayer(player);
                    net.milkbowl.vault.economy.EconomyResponse r = econ.depositPlayer(offlinePlayer, rewardPaid);
                    if (r.transactionSuccess()) {
                        pluginLogger.log(org.bestservers.util.PluginLogger.LogLevel.INFO, "ProgressService: Paid $" + r.amount + " to player " + offlinePlayer.getName() + " for quest " + def.getId() + ". New balance: $" + r.balance);
                    } else {
                        pluginLogger.log(org.bestservers.util.PluginLogger.LogLevel.ERROR, "ProgressService: Transaction failed: " + r.errorMessage);
                    }
                } else {
                    pluginLogger.log(org.bestservers.util.PluginLogger.LogLevel.WARNING, "ProgressService: Economy service not available or reward is zero, no payment made to player " + player + " for quest " + def.getId());
                }

            pluginLogger.log(org.bestservers.util.PluginLogger.LogLevel.DEBUG, "ProgressService: Reward for tier " + tierBefore + " is " + rewardPaid);
            tierAfter++;
            progress.setTier(tierAfter);
            progress.setContributedCount(0);
            // Jeśli maxTier != null i tierAfter > maxTier -> oznacz quest complete (do podpięcia)
            // Wywołaj QuestTierCompleteEvent
            pluginLogger.log(org.bestservers.util.PluginLogger.LogLevel.DEBUG, "ProgressService: New tier is " + tierAfter);
            org.bukkit.Bukkit.getPluginManager().callEvent(
                new org.bestservers.event.QuestTierCompleteEvent(player, def.getId(), tierBefore, rewardPaid)
            );
        }
        pluginLogger.log(org.bestservers.util.PluginLogger.LogLevel.DEBUG, "ProgressService: Contribution applied - usedItems: " + toAdd + ", tierBefore: " + tierBefore + ", tierAfter: " + tierAfter + ", rewardPaid: " + rewardPaid);
        // Wywołaj QuestProgressEvent
        org.bukkit.Bukkit.getPluginManager().callEvent(
            new org.bestservers.event.QuestProgressEvent(
                player,
                def.getId(),
                contributedBefore,
                progress.getContributedCount(),
                required,
                tierBefore
            )
        );
        Result result = new Result();
        result.usedItems = toAdd;
        result.tierBefore = tierBefore;
        result.tierAfter = tierAfter;
        result.rewardPaid = rewardPaid;
        pluginLogger.log(org.bestservers.util.PluginLogger.LogLevel.DEBUG, "ProgressService: Returning result from applyContribution. Player: " + player + ", Quest: " + def.getId() + ", UsedItems: " + result.usedItems + ", TierBefore: " + result.tierBefore + ", TierAfter: " + result.tierAfter + ", RewardPaid: " + result.rewardPaid+", RewardMultiplier: "+def.getMoneyTierMultiplier());
        return result;
    }

    public static class Result {
        public int usedItems;
        public int tierBefore;
        public int tierAfter;
        public double rewardPaid;
    }
}
