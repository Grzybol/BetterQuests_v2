package org.bestservers.service;

import org.bestservers.model.QuestDefinition;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProgressServiceTest {
    @Test
    void testGetRequiredForTier() {
        QuestDefinition def = new QuestDefinition("test", "Test", Material.STONE, 1, 100.0, 2.0, 10, 5, 3);
        ProgressService service = new ProgressService(null);
        assertEquals(10, service.getRequiredForTier(def, 1));
        assertEquals(15, service.getRequiredForTier(def, 2));
        assertEquals(20, service.getRequiredForTier(def, 3));
    }

    @Test
    void testGetRewardForTier() {
        QuestDefinition def = new QuestDefinition("test", "Test", Material.STONE, 1, 100.0, 2.0, 10, 5, 3);
        ProgressService service = new ProgressService(null);
        assertEquals(100.0, service.getRewardForTier(def, 1));
        assertEquals(200.0, service.getRewardForTier(def, 2));
        assertEquals(400.0, service.getRewardForTier(def, 3));
    }
}
