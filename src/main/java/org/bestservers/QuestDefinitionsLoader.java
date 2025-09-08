package org.bestservers;

import org.bestservers.model.QuestDefinition;
import org.bestservers.util.Validations;
import org.bukkit.plugin.Plugin;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.Material;
import java.io.File;
import java.util.*;

/**
 * Loads and validates quest definitions from quests.yml.
 */
public class QuestDefinitionsLoader {
    private final List<QuestDefinition> all = new ArrayList<>();
    public QuestDefinitionsLoader(Plugin plugin) {
        File file = new File(plugin.getDataFolder(), "quests.yml");
        // Sprawdź typ storage w configu
        String storageType = "yaml";
        if (plugin.getConfig().contains("storage.type")) {
            storageType = plugin.getConfig().getString("storage.type", "yaml");
        }
        // Jeśli storage: yaml i plik nie istnieje, kopiuj domyślny plik z resources
        if ("yaml".equalsIgnoreCase(storageType) && !file.exists()) {
            try (java.io.InputStream in = plugin.getResource("quests.yml")) {
                if (in != null) {
                    java.nio.file.Files.copy(in, file.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Nie można skopiować domyślnego quests.yml: " + e.getMessage());
            }
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        Set<String> ids = new HashSet<>();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> quests = (List<Map<String, Object>>) (List<?>) config.getMapList("quests");
        for (Map<String, Object> map : quests) {
            String id = Objects.toString(map.get("id"), "");
            if (id.isEmpty() || ids.contains(id)) continue;
            ids.add(id);
            String displayName = Objects.toString(map.get("displayName"), id);
            String materialName = Objects.toString(map.get("material"), "STONE");
            if (!Validations.isValidMaterial(materialName)) continue;
            Material material = Material.valueOf(materialName);
            int weight = Math.max(1, ((Number) map.getOrDefault("weight", 1)).intValue());

            double baseReward = ((Number) map.getOrDefault("baseReward", 0)).doubleValue();

            double moneyTierMultiplier = ((Number) map.getOrDefault("moneyTierMultiplier", 1.0)).doubleValue();

            Object baseRequiredItemsObj = map.getOrDefault("baseRequiredItems", 1);
            int baseRequiredItems = (baseRequiredItemsObj instanceof Number) ? Math.max(1, ((Number) baseRequiredItemsObj).intValue()) : Math.max(1, Integer.parseInt(baseRequiredItemsObj.toString()));

            Object additionalRequiredItemsPerTierObj = map.getOrDefault("additionalRequiredItemsPerTier", 0);
            int additionalRequiredItemsPerTier = (additionalRequiredItemsPerTierObj instanceof Number) ? Math.max(0, ((Number) additionalRequiredItemsPerTierObj).intValue()) : Math.max(0, Integer.parseInt(additionalRequiredItemsPerTierObj.toString()));
            Integer maxTier = null;
            if (map.containsKey("maxTier")) {
                Object maxTierObj = map.get("maxTier");
                if (maxTierObj instanceof Number) {
                    maxTier = ((Number) maxTierObj).intValue();
                } else if (maxTierObj != null) {
                    try {
                        maxTier = Integer.parseInt(maxTierObj.toString());
                    } catch (NumberFormatException e) {
                        maxTier = null;
                    }
                }
            }
            all.add(new QuestDefinition(id, displayName, material, weight, baseReward, moneyTierMultiplier, baseRequiredItems, additionalRequiredItemsPerTier, maxTier));
        }
    }
    public List<QuestDefinition> getAll() { return Collections.unmodifiableList(all); }
}
