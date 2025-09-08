package org.bestservers;
import net.milkbowl.vault.economy.Economy;

import org.bestservers.PluginConfig;
import org.bestservers.QuestDefinitionsLoader;
import org.bestservers.service.*;
import org.bestservers.storage.*;
import org.bestservers.util.PluginLogger;
import org.betterbox.elasticBuffer.ElasticBuffer;
import org.betterbox.elasticBuffer.ElasticBufferAPI;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import net.milkbowl.vault.economy.Economy;

import java.util.EnumSet;
import java.util.Set;

/**
 * Main plugin class for DailyQuests.
 */
public class DailyQuestsPlugin extends JavaPlugin {
    private PluginConfig pluginConfig;
    private QuestDefinitionsLoader questLoader;
    private ActiveQuestsService activeQuestsService;
    private ProgressService progressService;
    private EconomyService economyService;
    private static Economy econ = null;
    private Storage storage;
    private RotationScheduler rotationScheduler;
    private PluginLogger pluginLogger;

    @Override
    public void onEnable() {
        java.util.logging.Logger logger = this.getLogger();
        logger.info("[BetterQuests_v2] Starting plugin enable sequence...");
        pluginLogger = null; // Defensive, will be set below
        saveDefaultConfig();
        Set<PluginLogger.LogLevel> defaultLogLevels = EnumSet.of(PluginLogger.LogLevel.INFO, PluginLogger.LogLevel.WARNING, PluginLogger.LogLevel.ERROR);
        pluginLogger = new PluginLogger(getDataFolder().getAbsolutePath(), defaultLogLevels, this);
        pluginLogger.log(PluginLogger.LogLevel.INFO, "DailyQuestsPlugin: onEnable called");
        reloadConfigs();
        pluginLogger.log(PluginLogger.LogLevel.INFO, "DailyQuestsPlugin: Configs reloaded");
        economyService = new EconomyService();
        setupEconomy();
        pluginLogger.log(PluginLogger.LogLevel.INFO, "DailyQuestsPlugin: Economy setup complete");
        storage = new YamlStorage(this);
        pluginLogger.log(PluginLogger.LogLevel.INFO, "DailyQuestsPlugin: Storage initialized");

    economyService.setLogger(pluginLogger);
    progressService = new ProgressService(economyService, pluginLogger,econ);
    activeQuestsService = new ActiveQuestsService(pluginLogger);
        pluginLogger.log(PluginLogger.LogLevel.INFO, "DailyQuestsPlugin: Services initialized");
        // Initial rotation
        activeQuestsService.rotateActive(questLoader.getAll(), pluginConfig.getActiveQuestsCount(), true);
        pluginLogger.log(PluginLogger.LogLevel.INFO, "DailyQuestsPlugin: Initial quest rotation complete");
    // Register commands with logger
    getCommand("zadania").setExecutor(new org.bestservers.command.QuestsCommand(this, pluginLogger));
    getCommand("oddaj").setExecutor(new org.bestservers.command.ClaimCommand(this, pluginLogger));
        rotationScheduler = new RotationScheduler(this);
        rotationScheduler.startScheduler();
        rotationScheduler.startAutoSave();
        pluginLogger.log(PluginLogger.LogLevel.INFO, "DailyQuestsPlugin: Schedulers started");
        // Register PlaceholderAPI hook if available
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new org.bestservers.hook.PlaceholderExpansionImpl(this).register();
            pluginLogger.log(PluginLogger.LogLevel.INFO, "DailyQuestsPlugin: PlaceholderAPI hook registered");
        }
        pluginLogger.log(PluginLogger.LogLevel.INFO, "DailyQuestsPlugin: onEnable finished");
        logger.info("[BetterQuests_v2] Plugin enabled successfully.");
    }

    @Override
    public void onDisable() {
        pluginLogger.log(PluginLogger.LogLevel.INFO, "DailyQuestsPlugin: onDisable called");
            // Flush save synchronously
            if (progressService != null) {
                storage.saveAll(progressService.getProgressCacheSnapshot());
                pluginLogger.log(PluginLogger.LogLevel.INFO, "DailyQuestsPlugin: Progress cache saved");
            } else {
                pluginLogger.log(PluginLogger.LogLevel.WARNING, "DailyQuestsPlugin: ProgressService is null, skipping cache save.");
            }
            if (rotationScheduler != null) {
                rotationScheduler.stopSchedulers();
                pluginLogger.log(PluginLogger.LogLevel.INFO, "DailyQuestsPlugin: Schedulers stopped");
            }
            pluginLogger.log(PluginLogger.LogLevel.INFO, "DailyQuestsPlugin: onDisable finished");
    }

    public void reloadConfigs() {
    pluginLogger.log(PluginLogger.LogLevel.INFO, "DailyQuestsPlugin: reloadConfigs called");
    pluginConfig = new PluginConfig(this, pluginLogger);
    questLoader = new QuestDefinitionsLoader(this);
    pluginLogger.log(PluginLogger.LogLevel.INFO, "DailyQuestsPlugin: reloadConfigs finished");
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
            econ = rsp.getProvider();
            pluginLogger.log(PluginLogger.LogLevel.INFO, "DailyQuestsPlugin: Vault economy provider set.");
        } else if (rsp == null) {
            if (pluginLogger != null) {
                pluginLogger.log(PluginLogger.LogLevel.WARNING, "DailyQuestsPlugin: rsp is null, cannot set economy.");
            }
        }
    }
    private void loadElasticBuffer(){
        try{
            PluginManager pm = Bukkit.getPluginManager();
            try {
                // Opóźnienie o 5 sekund, aby dać ElasticBuffer czas na pełną inicjalizację
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                pluginLogger.log(PluginLogger.LogLevel.WARNING, "[BetterElo] Initialization delay interrupted: " + e.getMessage());
                Thread.currentThread().interrupt(); // Przywrócenie statusu przerwania wątku
            }
            ElasticBuffer elasticBuffer = (ElasticBuffer) pm.getPlugin("ElasticBuffer");
            pluginLogger.isElasticBufferEnabled=true;
            pluginLogger.api= new ElasticBufferAPI(elasticBuffer);
        }catch (Exception e){
            pluginLogger.log(PluginLogger.LogLevel.ERROR, "ElasticBufferAPI instance found via ServicesManager, exception: "+e.getMessage());
        }
    }
}
