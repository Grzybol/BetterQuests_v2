package org.bestservers.model;

/**
 * Player's progress for a quest.
 */
public class PlayerQuestProgress {
    private String questId;
    private int tier; // >= 1
    private int contributedCount;

    public PlayerQuestProgress(String questId, int tier, int contributedCount) {
        this.questId = questId;
        this.tier = tier;
        this.contributedCount = contributedCount;
    }
    public String getQuestId() { return questId; }
    public int getTier() { return tier; }
    public int getContributedCount() { return contributedCount; }
    public void setTier(int tier) { this.tier = tier; }
    public void setContributedCount(int count) { this.contributedCount = count; }
}
