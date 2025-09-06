package org.bestservers;

import org.bukkit.plugin.Plugin;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Loads and provides plugin config.yml values.
 */
public class PluginConfig {
    public org.bestservers.model.ItemMatchRule getItemMatchRule() {
        org.bestservers.model.ItemMatchRule rule = new org.bestservers.model.ItemMatchRule();
    rule.setMetaStrict(config.getBoolean("itemMatching.metaStrict", false));
    rule.setCustomModelData(config.isSet("itemMatching.customModelData") ? config.getInt("itemMatching.customModelData") : null);
    rule.setDisplayName(config.getString("itemMatching.displayName", null));
    rule.setNbtTags(config.getString("itemMatching.nbtTags", null));
        return rule;
    }
    private final FileConfiguration config;
    public PluginConfig(Plugin plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        }
        this.config = plugin.getConfig();
    }
    public int getActiveQuestsCount() {
        return config.getInt("activeQuestsCount", 3);
    }
    public int getRotationIntervalHours() {
        return config.getInt("rotation.intervalHours", 24);
    }
    public boolean isEconomyEnabled() {
        return config.getBoolean("economy.enabled", true);
    }
    public String getStorageType() {
        return config.getString("storage.type", "yaml");
    }
    public boolean isMetaStrict() {
        return config.getBoolean("itemMatching.metaStrict", false);
    }
    public int getAutosaveSeconds() {
        return config.getInt("autosaveSeconds", 120);
    }
}
