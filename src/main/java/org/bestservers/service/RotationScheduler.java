package org.bestservers.service;

import org.bestservers.DailyQuestsPlugin;

/**
 * Schedules quest rotation and autosave tasks.
 */
public class RotationScheduler {
    private final DailyQuestsPlugin plugin;
    private int rotationTaskId = -1;
    private int autosaveTaskId = -1;

    public RotationScheduler(DailyQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Starts quest rotation scheduler.
     */
    public void startScheduler() {
        int intervalTicks = plugin.getPluginConfig().getRotationIntervalHours() * 60 * 60 * 20;
        rotationTaskId = org.bukkit.Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            plugin.getActiveQuestsService().rotateActive(
                plugin.getQuestLoader().getAll(),
                plugin.getPluginConfig().getActiveQuestsCount(),
                true
            );
        }, intervalTicks, intervalTicks);
    }

    /**
     * Starts autosave scheduler.
     */
    public void startAutoSave() {
        int intervalTicks = plugin.getPluginConfig().getAutosaveSeconds() * 20;
        autosaveTaskId = org.bukkit.Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            plugin.getStorage().saveAll(plugin.getProgressService().getProgressCacheSnapshot());
        }, intervalTicks, intervalTicks).getTaskId();
    }

    public void stopSchedulers() {
        if (rotationTaskId != -1) org.bukkit.Bukkit.getScheduler().cancelTask(rotationTaskId);
        if (autosaveTaskId != -1) org.bukkit.Bukkit.getScheduler().cancelTask(autosaveTaskId);
    }
}
