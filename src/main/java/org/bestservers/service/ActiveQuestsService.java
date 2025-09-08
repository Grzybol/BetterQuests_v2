package org.bestservers.service;

import org.bestservers.model.ActiveQuest;
import java.util.*;
import org.bestservers.util.PluginLogger;

/**
 * Holds the current list of active quests.
 */
public class ActiveQuestsService {
    private List<ActiveQuest> activeQuests = new ArrayList<>();
    private long lastRotationEpoch = 0;
    private final PluginLogger pluginLogger;

    public ActiveQuestsService(PluginLogger pluginLogger) {
        this.pluginLogger = pluginLogger;
        pluginLogger.log(PluginLogger.LogLevel.DEBUG, "ActiveQuestsService: Initialized");
    }
    /**
     * Rotates active quests using QuestSelectionService.
     * @param all all quest definitions
     * @param count number of quests to select
     * @param weighted use weighted selection
     */
    public void rotateActive(Collection<org.bestservers.model.QuestDefinition> all, int count, boolean weighted) {
        pluginLogger.log(PluginLogger.LogLevel.INFO, "ActiveQuestsService: Rotating active quests. Count: " + count + ", Weighted: " + weighted);
        org.bestservers.service.QuestSelectionService selector = new org.bestservers.service.QuestSelectionService();
        this.activeQuests = selector.selectActive(all, count, weighted);
        this.lastRotationEpoch = System.currentTimeMillis() / 1000L;
        pluginLogger.log(PluginLogger.LogLevel.INFO, "ActiveQuestsService: Rotation complete. Active quests: " + activeQuests.size());
    }

    public long getLastRotationEpoch() {
        return lastRotationEpoch;
    }

    public List<ActiveQuest> getActive() {
        return Collections.unmodifiableList(activeQuests);
    }

    public void setActive(List<ActiveQuest> quests) {
        this.activeQuests = new ArrayList<>(quests);
        pluginLogger.log(PluginLogger.LogLevel.INFO, "ActiveQuestsService: setActive called. New active quests: " + quests.size());
    }

    public boolean isActive(String id) {
        boolean found = activeQuests.stream().anyMatch(q -> q.getDef().getId().equals(id));
        pluginLogger.log(PluginLogger.LogLevel.DEBUG, "ActiveQuestsService: isActive called for id: " + id + ", found: " + found);
        return found;
    }
}
