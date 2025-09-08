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
        int removed = 0;
        if (maxNeeded <= 0) return 0;
        org.bukkit.inventory.PlayerInventory inv = p.getInventory();
        for (org.bukkit.inventory.ItemStack item : inv.getContents()) {
            if (item == null || item.getType() == org.bukkit.Material.AIR) continue;
            if (!item.getType().equals(rule.getMaterial())) continue;
            // Opcjonalne: sprawdzanie customModelData, displayName, metaStrict, nbtTags
            if (rule.getCustomModelData() != null && (!item.hasItemMeta() || item.getItemMeta().getCustomModelData() != rule.getCustomModelData())) continue;
            if (rule.getDisplayName() != null && (!item.hasItemMeta() || !rule.getDisplayName().equals(item.getItemMeta().getDisplayName()))) continue;
            int toRemove = Math.min(item.getAmount(), maxNeeded - removed);
            if (toRemove <= 0) break;
            item.setAmount(item.getAmount() - toRemove);
            removed += toRemove;
            if (item.getAmount() <= 0) inv.remove(item);
            if (removed >= maxNeeded) break;
        }
        p.updateInventory();
        return removed;
    }
}
