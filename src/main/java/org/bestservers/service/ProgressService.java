package org.bestservers.service;

import org.bestservers.model.QuestDefinition;
import org.bestservers.model.PlayerQuestProgress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

/**
 * Service for managing player quest progress and rewards.
 */
public class ProgressService {
    /**
     * Gets PlayerQuestProgress for a player and questId, or null if not present.
     */
    public PlayerQuestProgress getProgress(UUID player, String questId) {
        Map<String, PlayerQuestProgress> playerMap = progressCache.get(player);
        if (playerMap == null) return null;
        return playerMap.get(questId);
    }

    /**
     * Returns a snapshot of the entire progress cache (for saving).
     */
    public Map<UUID, Map<String, PlayerQuestProgress>> getProgressCacheSnapshot() {
        return new HashMap<>(progressCache);
    }
    private final Map<UUID, Map<String, PlayerQuestProgress>> progressCache = new ConcurrentHashMap<>();
    private EconomyService economyService;

    public ProgressService(EconomyService economyService) {
        this.economyService = economyService;
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
        Map<String, PlayerQuestProgress> playerMap = progressCache.computeIfAbsent(player, k -> new HashMap<>());
        PlayerQuestProgress progress = playerMap.get(def.getId());
        if (progress == null) {
            progress = new PlayerQuestProgress(def.getId(), 1, 0);
            playerMap.put(def.getId(), progress);
        }
        int tierBefore = progress.getTier();
        int contributedBefore = progress.getContributedCount();
        int required = getRequiredForTier(def, tierBefore);
        int toAdd = Math.min(contributed, required - contributedBefore);
        progress.setContributedCount(contributedBefore + toAdd);
        int tierAfter = tierBefore;
        double rewardPaid = 0.0;
        boolean completedTier = false;
        if (progress.getContributedCount() >= required) {
            completedTier = true;
            rewardPaid = getRewardForTier(def, tierBefore);
            if (economyService != null && rewardPaid > 0.0) {
                economyService.pay(player, rewardPaid);
            }
            tierAfter++;
            progress.setTier(tierAfter);
            progress.setContributedCount(0);
            // Jeśli maxTier != null i tierAfter > maxTier -> oznacz quest complete (do podpięcia)
            // Wywołaj QuestTierCompleteEvent
            org.bukkit.Bukkit.getPluginManager().callEvent(
                new org.bestservers.event.QuestTierCompleteEvent(player, def.getId(), tierBefore, rewardPaid)
            );
        }
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
        return result;
    }

    public static class Result {
        public int usedItems;
        public int tierBefore;
        public int tierAfter;
        public double rewardPaid;
    }
}
