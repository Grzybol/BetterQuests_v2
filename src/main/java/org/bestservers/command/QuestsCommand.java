package org.bestservers.command;

import org.bestservers.DailyQuestsPlugin;
import org.bestservers.util.PluginLogger;
import org.bestservers.model.ActiveQuest;
import org.bestservers.model.QuestDefinition;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import java.io.Console;
import java.util.List;

/**
 * Handles /quests command: list, info, reload.
 */
public class QuestsCommand implements CommandExecutor {
    private final DailyQuestsPlugin plugin;
    private final PluginLogger pluginLogger;
    public QuestsCommand(DailyQuestsPlugin plugin, PluginLogger pluginLogger) {
        this.plugin = plugin;
        this.pluginLogger = pluginLogger;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        pluginLogger.log(PluginLogger.LogLevel.DEBUG, "QuestsCommand: onCommand called with args: " + java.util.Arrays.toString(args));
        if (args.length == 0) {
            pluginLogger.log(PluginLogger.LogLevel.DEBUG, "QuestsCommand: Showing active quests");
            showActiveQuests(sender);
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            pluginLogger.log(PluginLogger.LogLevel.DEBUG, "QuestsCommand: Reload command called by " + sender.getName());
            if (!sender.hasPermission("dailyquests.admin") || sender.isOp()) {
                pluginLogger.log(PluginLogger.LogLevel.WARNING, "QuestsCommand: No permission for reload by " + sender.getName());
                sender.sendMessage(ChatColor.RED + "Brak permisji.");
                return true;
            }
            plugin.getPluginConfig().reloadConfig();
            plugin.reloadConfigs();
            plugin.getActiveQuestsService().rotateActive(plugin.getQuestLoader().getAll(), plugin.getPluginConfig().getActiveQuestsCount(), true);
            sender.sendMessage(ChatColor.GREEN + "Config i questy przeładowane!");
            pluginLogger.log(PluginLogger.LogLevel.INFO, "QuestsCommand: Config and quests reloaded by " + sender.getName());
            return true;
        }
        if (args[0].equalsIgnoreCase("info") && args.length > 1) {
            String id = args[1];
            pluginLogger.log(PluginLogger.LogLevel.DEBUG, "QuestsCommand: Info command for quest id: " + id);
            QuestDefinition def = plugin.getQuestLoader().getAll().stream().filter(q -> q.getId().equals(id)).findFirst().orElse(null);
            if (def == null) {
                pluginLogger.log(PluginLogger.LogLevel.WARNING, "QuestsCommand: Quest not found for id: " + id);
                sender.sendMessage(ChatColor.RED + "Nie znaleziono questu o id: " + id);
                return true;
            }
            sender.sendMessage(ChatColor.YELLOW + "Quest: " + def.getDisplayName());
            sender.sendMessage(ChatColor.GRAY + "Material: " + def.getMaterial());
            sender.sendMessage(ChatColor.GRAY + "Nagroda bazowa: " + def.getBaseReward());
            sender.sendMessage(ChatColor.GRAY + "Waga: " + def.getWeight());
            sender.sendMessage(ChatColor.GRAY + "Max tier: " + (def.getMaxTier() != null ? def.getMaxTier() : "∞"));
            pluginLogger.log(PluginLogger.LogLevel.INFO, "QuestsCommand: Info displayed for quest id: " + id);
            return true;
        }
        showActiveQuests(sender);
        return true;
    }
    private void showActiveQuests(CommandSender sender) {
        List<ActiveQuest> active = plugin.getActiveQuestsService().getActive();
        pluginLogger.log(PluginLogger.LogLevel.DEBUG, "QuestsCommand: showActiveQuests called, active count: " + active.size());

        if (active.isEmpty()) {
            pluginLogger.log(PluginLogger.LogLevel.WARNING, "QuestsCommand: No active quests to show");
            sender.sendMessage(ChatColor.RED + "Brak aktywnych questów.");
            return;
        }

        sender.sendMessage(ChatColor.GOLD + "Aktywne questy:");
        for (int i = 0; i < active.size(); i++) {
            ActiveQuest aq = active.get(i);
            QuestDefinition def = aq.getDef();

            pluginLogger.log(PluginLogger.LogLevel.DEBUG,
                    "QuestsCommand: Showing quest " + def.getId() + " - " + def.getDisplayName());

            sender.sendMessage(ChatColor.YELLOW + "" + (i + 1) + ". "
                    + def.getDisplayName()
                    + ChatColor.GRAY + " [ID: " + def.getId() + "]"
                    + ChatColor.DARK_GRAY + " [" + def.getMaterial() + "]");

        }
    }

    private void showActiveQuestsOld(CommandSender sender) {
        List<ActiveQuest> active = plugin.getActiveQuestsService().getActive();
        pluginLogger.log(PluginLogger.LogLevel.DEBUG, "QuestsCommand: showActiveQuests called, active count: " + active.size());
        if (active.isEmpty()) {
            pluginLogger.log(PluginLogger.LogLevel.WARNING, "QuestsCommand: No active quests to show");
            sender.sendMessage(ChatColor.RED + "Brak aktywnych questów.");
            return;
        }
        sender.sendMessage(ChatColor.GOLD + "Aktywne questy:");
        for (int i = 0; i < active.size(); i++) {
            ActiveQuest aq = active.get(i);
            QuestDefinition def = aq.getDef();
            pluginLogger.log(PluginLogger.LogLevel.DEBUG, "QuestsCommand: Showing quest " + def.getId() + " - " + def.getDisplayName());
            sender.sendMessage(ChatColor.YELLOW + String.valueOf(i+1) + ". " + def.getDisplayName() + ChatColor.GRAY + " [" + def.getMaterial() + "]");
        }
    }
}
