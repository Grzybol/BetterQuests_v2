package org.bestservers;

import org.bukkit.plugin.Plugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bestservers.util.PluginLogger;

import java.io.File;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;

/**
 * Loads and provides plugin config.yml values.
 */
public class PluginConfig {
    private final FileConfiguration config;
    private final Plugin plugin;
    private final PluginLogger pluginLogger;
    private java.util.Set<PluginLogger.LogLevel> enabledLogLevels;
    private java.io.File configFile;
    private java.util.List<String> logLevels;

    public PluginConfig(Plugin plugin, PluginLogger pluginLogger) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        }
        if (pluginLogger == null) {
            throw new IllegalArgumentException("PluginLogger cannot be null");
        }
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.pluginLogger = pluginLogger;
        pluginLogger.log(PluginLogger.LogLevel.DEBUG, "PluginConfig: Initialized");
    }

    public org.bestservers.model.ItemMatchRule getItemMatchRule() {
        pluginLogger.log(PluginLogger.LogLevel.DEBUG, "PluginConfig: getItemMatchRule called");
        org.bestservers.model.ItemMatchRule rule = new org.bestservers.model.ItemMatchRule();
        rule.setMetaStrict(config.getBoolean("itemMatching.metaStrict", false));
        rule.setCustomModelData(config.isSet("itemMatching.customModelData") ? config.getInt("itemMatching.customModelData") : null);
        rule.setDisplayName(config.getString("itemMatching.displayName", null));
        rule.setNbtTags(config.getString("itemMatching.nbtTags", null));
        return rule;
    }
    public int getActiveQuestsCount() {
        int count = config.getInt("activeQuestsCount", 3);
        pluginLogger.log(PluginLogger.LogLevel.DEBUG, "PluginConfig: getActiveQuestsCount = " + count);
        return count;
    }
    public int getRotationIntervalHours() {
        int hours = config.getInt("rotation.intervalHours", 24);
        pluginLogger.log(PluginLogger.LogLevel.DEBUG, "PluginConfig: getRotationIntervalHours = " + hours);
        return hours;
    }
    public boolean isEconomyEnabled() {
        boolean enabled = config.getBoolean("economy.enabled", true);
        pluginLogger.log(PluginLogger.LogLevel.DEBUG, "PluginConfig: isEconomyEnabled = " + enabled);
        return enabled;
    }
    public String getStorageType() {
        String type = config.getString("storage.type", "yaml");
        pluginLogger.log(PluginLogger.LogLevel.DEBUG, "PluginConfig: getStorageType = " + type);
        return type;
    }
    public boolean isMetaStrict() {
        boolean strict = config.getBoolean("itemMatching.metaStrict", false);
        pluginLogger.log(PluginLogger.LogLevel.DEBUG, "PluginConfig: isMetaStrict = " + strict);
        return strict;
    }
    public int getAutosaveSeconds() {
        int seconds = config.getInt("autosaveSeconds", 120);
        pluginLogger.log(PluginLogger.LogLevel.DEBUG, "PluginConfig: getAutosaveSeconds = " + seconds);
        return seconds;
    }
    public void reloadConfig(){
        pluginLogger.log(PluginLogger.LogLevel.INFO, "PluginConfig: Reloading config...");
        configFile = new java.io.File(plugin.getDataFolder(), "config.yml");
        plugin.reloadConfig();
        logLevels = plugin.getConfig().getStringList("log_level");
        enabledLogLevels = new java.util.HashSet<>();
        if (logLevels == null || logLevels.isEmpty()) {
            pluginLogger.log(PluginLogger.LogLevel.WARNING, "PluginConfig: No log levels configured, using defaults.");
            enabledLogLevels = java.util.EnumSet.of(PluginLogger.LogLevel.INFO, PluginLogger.LogLevel.WARNING, PluginLogger.LogLevel.ERROR);
            // Optionally update config file here
        }
        for (String level : logLevels) {
            try {
                enabledLogLevels.add(PluginLogger.LogLevel.valueOf(level.toUpperCase()));
            } catch (IllegalArgumentException e) {
                pluginLogger.log(PluginLogger.LogLevel.ERROR, "PluginConfig: Invalid log level in config: " + level);
            }
        }
        pluginLogger.setEnabledLogLevels(enabledLogLevels);
        pluginLogger.log(PluginLogger.LogLevel.INFO, "PluginConfig: Config reloaded. Enabled log levels: " + enabledLogLevels);
    }
    public java.util.Set<PluginLogger.LogLevel> getEnabledLogLevels() {
        return enabledLogLevels;
    }
}
