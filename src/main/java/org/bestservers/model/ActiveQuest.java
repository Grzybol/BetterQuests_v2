package org.bestservers.model;

/**
 * Represents an active quest instance.
 */
public class ActiveQuest {
    private QuestDefinition def;
    private long activatedAtEpoch;

    public ActiveQuest(QuestDefinition def, long activatedAtEpoch) {
        this.def = def;
        this.activatedAtEpoch = activatedAtEpoch;
    }
    public QuestDefinition getDef() { return def; }
    public long getActivatedAtEpoch() { return activatedAtEpoch; }
}
