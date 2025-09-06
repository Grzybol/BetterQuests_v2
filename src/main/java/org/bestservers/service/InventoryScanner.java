package org.bestservers.service;

import org.bestservers.model.QuestDefinition;
import org.bestservers.model.ItemMatchRule;
import org.bukkit.entity.Player;

/**
 * Scans and removes items from player inventory for quest contribution.
 */
public class InventoryScanner {
    /**
     * Scans and removes up to maxNeeded items matching rule from player inventory.
     * @return number of items removed
     */
    public int scanAndRemove(Player p, QuestDefinition def, int maxNeeded, ItemMatchRule rule) {
        // TODO: Implement inventory scan and removal
        return 0;
    }
}
