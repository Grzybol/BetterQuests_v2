package org.bestservers.storage;

import org.bestservers.model.PlayerQuestProgress;
import java.util.Map;
import java.util.UUID;

/**
 * Interface for player quest progress storage.
 */
public interface Storage {
    Map<String, PlayerQuestProgress> loadPlayer(UUID playerId);
    void savePlayer(UUID playerId, Map<String, PlayerQuestProgress> map);
    void saveAll(Map<UUID, Map<String, PlayerQuestProgress>> snapshot);
}
