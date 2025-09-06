package org.bestservers.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import java.util.UUID;

/**
 * Event fired when player makes progress in a quest.
 */
public class QuestProgressEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final UUID player;
    private final String questId;
    private final int contributedBefore;
    private final int contributedAfter;
    private final int requiredForTier;
    private final int tier;

    public QuestProgressEvent(UUID player, String questId, int contributedBefore, int contributedAfter, int requiredForTier, int tier) {
        this.player = player;
        this.questId = questId;
        this.contributedBefore = contributedBefore;
        this.contributedAfter = contributedAfter;
        this.requiredForTier = requiredForTier;
        this.tier = tier;
    }
    public UUID getPlayer() { return player; }
    public String getQuestId() { return questId; }
    public int getContributedBefore() { return contributedBefore; }
    public int getContributedAfter() { return contributedAfter; }
    public int getRequiredForTier() { return requiredForTier; }
    public int getTier() { return tier; }
    @Override
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
