package org.bestservers.storage;

import org.bestservers.model.PlayerQuestProgress;
import org.bukkit.plugin.Plugin;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * YAML-based storage for player quest progress (players.yml).
 * All IO is async, cache is in memory.
 */
public class YamlStorage implements Storage {
    private final Plugin plugin;
    private final File file;
    private YamlConfiguration config;

    public YamlStorage(Plugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "players.yml");
        reload();
    }

    public void reload() {
        if (!file.exists()) {
            try { file.createNewFile(); } catch (IOException ignored) {}
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public Map<String, PlayerQuestProgress> loadPlayer(UUID playerId) {
        Map<String, PlayerQuestProgress> result = new HashMap<>();
        String path = "players." + playerId;
        if (!config.contains(path)) return result;
        Map<String, Object> quests = config.getConfigurationSection(path).getValues(false);
        for (Map.Entry<String, Object> entry : quests.entrySet()) {
            if (entry.getValue() instanceof Map) {
                Map<String, Object> data = (Map<String, Object>) entry.getValue();
                String questId = entry.getKey();
                Object tierObj = data.getOrDefault("tier", 1);
                Object contributedObj = data.getOrDefault("contributedCount", 0);
                int tier = (tierObj instanceof Number) ? ((Number) tierObj).intValue() : 1;
                int contributed = (contributedObj instanceof Number) ? ((Number) contributedObj).intValue() : 0;
                result.put(questId, new PlayerQuestProgress(questId, tier, contributed));
            }
        }
        return result;
    }

    @Override
    public void savePlayer(UUID playerId, Map<String, PlayerQuestProgress> map) {
        String path = "players." + playerId;
        for (Map.Entry<String, PlayerQuestProgress> entry : map.entrySet()) {
            String questId = entry.getKey();
            PlayerQuestProgress progress = entry.getValue();
            config.set(path + "." + questId + ".tier", progress.getTier());
            config.set(path + "." + questId + ".contributedCount", progress.getContributedCount());
        }
        asyncSave();
    }

    @Override
    public void saveAll(Map<UUID, Map<String, PlayerQuestProgress>> snapshot) {
        for (Map.Entry<UUID, Map<String, PlayerQuestProgress>> entry : snapshot.entrySet()) {
            savePlayer(entry.getKey(), entry.getValue());
        }
        asyncSave();
    }

    private void asyncSave() {
        CompletableFuture.runAsync(() -> {
            try { config.save(file); } catch (IOException ignored) {}
        });
    }
}
