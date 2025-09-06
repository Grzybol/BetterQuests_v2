package org.bestservers.model;

import org.bukkit.Material;

/**
 * Definition of a quest loaded from quests.yml
 */
public class QuestDefinition {
    public QuestDefinition(String id, String displayName, Material material, int weight, double baseReward, double moneyTierMultiplier, int baseRequiredItems, int additionalRequiredItemsPerTier, Integer maxTier) {
        this.id = id;
        this.displayName = displayName;
        this.material = material;
        this.weight = weight;
        this.baseReward = baseReward;
        this.moneyTierMultiplier = moneyTierMultiplier;
        this.baseRequiredItems = baseRequiredItems;
        this.additionalRequiredItemsPerTier = additionalRequiredItemsPerTier;
        this.maxTier = maxTier;
    }
    private String id;
    private String displayName;
    private Material material;
    private int weight;
    private double baseReward;
    private double moneyTierMultiplier;
    private int baseRequiredItems;
    private int additionalRequiredItemsPerTier;
    private Integer maxTier; // nullable

    // Getters, setters, constructor, equals, hashCode
    // ...
    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public Material getMaterial() { return material; }
    public int getWeight() { return weight; }
    public double getBaseReward() { return baseReward; }
    public double getMoneyTierMultiplier() { return moneyTierMultiplier; }
    public int getBaseRequiredItems() { return baseRequiredItems; }
    public int getAdditionalRequiredItemsPerTier() { return additionalRequiredItemsPerTier; }
    public Integer getMaxTier() { return maxTier; }
    // ...
}
