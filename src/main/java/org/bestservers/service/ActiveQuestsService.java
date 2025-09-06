package org.bestservers.service;

import org.bestservers.model.ActiveQuest;
import java.util.*;

/**
 * Holds the current list of active quests.
 */
public class ActiveQuestsService {
    private List<ActiveQuest> activeQuests = new ArrayList<>();
    private long lastRotationEpoch = 0;
    /**
     * Rotates active quests using QuestSelectionService.
     * @param all all quest definitions
     * @param count number of quests to select
     * @param weighted use weighted selection
     */
    public void rotateActive(Collection<org.bestservers.model.QuestDefinition> all, int count, boolean weighted) {
        org.bestservers.service.QuestSelectionService selector = new org.bestservers.service.QuestSelectionService();
        this.activeQuests = selector.selectActive(all, count, weighted);
        this.lastRotationEpoch = System.currentTimeMillis() / 1000L;
    }

    public long getLastRotationEpoch() {
        return lastRotationEpoch;
    }

    public List<ActiveQuest> getActive() {
        return Collections.unmodifiableList(activeQuests);
    }

    public void setActive(List<ActiveQuest> quests) {
        this.activeQuests = new ArrayList<>(quests);
    }

    public boolean isActive(String id) {
        return activeQuests.stream().anyMatch(q -> q.getDef().getId().equals(id));
    }
}
