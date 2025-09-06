package org.bestservers.model;

import org.bukkit.Material;

/**
 * Rule for matching items in inventory.
 */
public class ItemMatchRule {
    private Material material;
    private boolean metaStrict;
    private Integer customModelData;
    private String displayName;
    private String nbtTags;

    // Getters, setters, constructor
    public Material getMaterial() { return material; }
    public void setMaterial(Material material) { this.material = material; }
    public boolean isMetaStrict() { return metaStrict; }
    public void setMetaStrict(boolean metaStrict) { this.metaStrict = metaStrict; }
    public Integer getCustomModelData() { return customModelData; }
    public void setCustomModelData(Integer customModelData) { this.customModelData = customModelData; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getNbtTags() { return nbtTags; }
    public void setNbtTags(String nbtTags) { this.nbtTags = nbtTags; }
}
