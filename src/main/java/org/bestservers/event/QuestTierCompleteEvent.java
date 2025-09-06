package org.bestservers.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import java.util.UUID;

/**
 * Event fired when player completes a quest tier.
 */
public class QuestTierCompleteEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final UUID player;
    private final String questId;
    private final int completedTier;
    private final double rewardAmount;

    public QuestTierCompleteEvent(UUID player, String questId, int completedTier, double rewardAmount) {
        this.player = player;
        this.questId = questId;
        this.completedTier = completedTier;
        this.rewardAmount = rewardAmount;
    }
    public UUID getPlayer() { return player; }
    public String getQuestId() { return questId; }
    public int getCompletedTier() { return completedTier; }
    public double getRewardAmount() { return rewardAmount; }
    @Override
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
