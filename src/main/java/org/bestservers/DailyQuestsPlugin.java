package org.bestservers;
import net.milkbowl.vault.economy.Economy;

import org.bestservers.PluginConfig;
import org.bestservers.QuestDefinitionsLoader;
import org.bestservers.service.*;
import org.bestservers.storage.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import net.milkbowl.vault.economy.Economy;

/**
 * Main plugin class for DailyQuests.
 */
public class DailyQuestsPlugin extends JavaPlugin {
    private PluginConfig pluginConfig;
    private QuestDefinitionsLoader questLoader;
    private ActiveQuestsService activeQuestsService;
    private ProgressService progressService;
    private EconomyService economyService;
    private Storage storage;
    private RotationScheduler rotationScheduler;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfigs();
        setupEconomy();
        storage = new YamlStorage(this);
        economyService = new EconomyService();
        progressService = new ProgressService(economyService);
        activeQuestsService = new ActiveQuestsService();
        // Initial rotation
        activeQuestsService.rotateActive(questLoader.getAll(), pluginConfig.getActiveQuestsCount(), true);
    // Register commands
    getCommand("quests").setExecutor(new org.bestservers.command.QuestsCommand(this));
    getCommand("claim").setExecutor(new org.bestservers.command.ClaimCommand(this));
    // Start rotation and autosave scheduler
    rotationScheduler = new RotationScheduler(this);
    rotationScheduler.startScheduler();
    rotationScheduler.startAutoSave();
        // Register PlaceholderAPI hook if available
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new org.bestservers.hook.PlaceholderExpansionImpl(this).register();
        }
    }

    @Override
    public void onDisable() {
    // Flush save synchronously
    storage.saveAll(progressService.getProgressCacheSnapshot());
    if (rotationScheduler != null) rotationScheduler.stopSchedulers();
    }

    public void reloadConfigs() {
        pluginConfig = new PluginConfig(this);
        questLoader = new QuestDefinitionsLoader(this);
    }

    public ActiveQuestsService getActiveQuestsService() { return activeQuestsService; }
    public ProgressService getProgressService() { return progressService; }
    public EconomyService getEconomyService() { return economyService; }
    public Storage getStorage() { return storage; }
    public PluginConfig getPluginConfig() { return pluginConfig; }
    public QuestDefinitionsLoader getQuestLoader() { return questLoader; }

    private void setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) return;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            economyService.setEconomy(rsp.getProvider());
        }
    }
}
