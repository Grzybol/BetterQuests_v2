package org.bestservers.hook;

import org.bestservers.DailyQuestsPlugin;
import org.bestservers.model.ActiveQuest;
import org.bestservers.model.QuestDefinition;
import org.bestservers.model.PlayerQuestProgress;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * PlaceholderAPI expansion for DailyQuests.
 */
public class PlaceholderExpansionImpl extends PlaceholderExpansion {
    private final DailyQuestsPlugin plugin;
    public PlaceholderExpansionImpl(DailyQuestsPlugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public @NotNull String getIdentifier() { return "dailyquests"; }
    @Override
    public @NotNull String getAuthor() { return "yourorg"; }
    @Override
    public @NotNull String getVersion() { return "1.0.0"; }
    @Override
    public String onRequest(org.bukkit.OfflinePlayer player, String params) {
        if (player == null || !player.isOnline()) return "";
        Player onlinePlayer = (Player) player;
        if (params.startsWith("current_")) {
            String[] split = params.split("_");
            if (split.length < 3) return "";
            int idx;
            try { idx = Integer.parseInt(split[1]) - 1; } catch (NumberFormatException e) { return ""; }
            java.util.List<ActiveQuest> active = plugin.getActiveQuestsService().getActive();
            if (idx < 0 || idx >= active.size()) return "";
            ActiveQuest aq = active.get(idx);
            QuestDefinition def = aq.getDef();
            PlayerQuestProgress progress = plugin.getProgressService().getProgress(onlinePlayer.getUniqueId(), def.getId());
            switch (split[2]) {
                case "name": return def.getDisplayName();
                case "progress": return String.valueOf(progress != null ? progress.getContributedCount() : 0);
                case "required": return String.valueOf(plugin.getProgressService().getRequiredForTier(def, progress != null ? progress.getTier() : 1));
                case "tier": return String.valueOf(progress != null ? progress.getTier() : 1);
                default: return "";
            }
        }
        if (params.equals("current_count")) {
            return String.valueOf(plugin.getActiveQuestsService().getActive().size());
        }
        return "";
    }
}
