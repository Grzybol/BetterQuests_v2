package org.bestservers.service;

import org.bestservers.model.ActiveQuest;
import org.bestservers.model.QuestDefinition;
import java.util.*;

/**
 * Service for selecting active quests based on weights.
 */
public class QuestSelectionService {
    /**
     * Selects a list of active quests from all definitions.
     * @param all all quest definitions
     * @param count number of quests to select
     * @param weighted if true, use weights
     * @return list of active quests
     */
    public List<ActiveQuest> selectActive(Collection<QuestDefinition> all, int count, boolean weighted) {
        List<QuestDefinition> defs = new ArrayList<>(all);
        if (count <= 0 || defs.isEmpty()) return Collections.emptyList();
        if (!weighted || defs.size() <= count) {
            Collections.shuffle(defs);
            List<ActiveQuest> result = new ArrayList<>();
            for (int i = 0; i < Math.min(count, defs.size()); i++) {
                result.add(new ActiveQuest(defs.get(i), System.currentTimeMillis() / 1000L));
            }
            return result;
        }
        // Weighted random selection without replacement
        List<ActiveQuest> selected = new ArrayList<>();
        List<QuestDefinition> pool = new ArrayList<>(defs);
        Random rand = new Random();
        for (int i = 0; i < count && !pool.isEmpty(); i++) {
            int totalWeight = pool.stream().mapToInt(QuestDefinition::getWeight).sum();
            int r = rand.nextInt(totalWeight);
            int acc = 0;
            for (QuestDefinition def : pool) {
                acc += def.getWeight();
                if (r < acc) {
                    selected.add(new ActiveQuest(def, System.currentTimeMillis() / 1000L));
                    pool.remove(def);
                    break;
                }
            }
        }
        return selected;
    }
}
