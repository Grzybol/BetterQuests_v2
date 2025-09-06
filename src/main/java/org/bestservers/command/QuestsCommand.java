package org.bestservers.command;

import org.bestservers.DailyQuestsPlugin;
import org.bestservers.model.ActiveQuest;
import org.bestservers.model.QuestDefinition;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import java.util.List;

/**
 * Handles /quests command: list, info, reload.
 */
public class QuestsCommand implements CommandExecutor {
    private final DailyQuestsPlugin plugin;
    public QuestsCommand(DailyQuestsPlugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            showActiveQuests(sender);
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("dailyquests.admin")) {
                sender.sendMessage(ChatColor.RED + "Brak permisji.");
                return true;
            }
            plugin.reloadConfigs();
            plugin.getActiveQuestsService().rotateActive(plugin.getQuestLoader().getAll(), plugin.getPluginConfig().getActiveQuestsCount(), true);
            sender.sendMessage(ChatColor.GREEN + "Config i questy przeładowane!");
            return true;
        }
        if (args[0].equalsIgnoreCase("info") && args.length > 1) {
            String id = args[1];
            QuestDefinition def = plugin.getQuestLoader().getAll().stream().filter(q -> q.getId().equals(id)).findFirst().orElse(null);
            if (def == null) {
                sender.sendMessage(ChatColor.RED + "Nie znaleziono questu o id: " + id);
                return true;
            }
            sender.sendMessage(ChatColor.YELLOW + "Quest: " + def.getDisplayName());
            sender.sendMessage(ChatColor.GRAY + "Material: " + def.getMaterial());
            sender.sendMessage(ChatColor.GRAY + "Nagroda bazowa: " + def.getBaseReward());
            sender.sendMessage(ChatColor.GRAY + "Waga: " + def.getWeight());
            sender.sendMessage(ChatColor.GRAY + "Max tier: " + (def.getMaxTier() != null ? def.getMaxTier() : "∞"));
            return true;
        }
        showActiveQuests(sender);
        return true;
    }
    private void showActiveQuests(CommandSender sender) {
        List<ActiveQuest> active = plugin.getActiveQuestsService().getActive();
        if (active.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Brak aktywnych questów.");
            return;
        }
        sender.sendMessage(ChatColor.GOLD + "Aktywne questy:");
        for (int i = 0; i < active.size(); i++) {
            ActiveQuest aq = active.get(i);
            QuestDefinition def = aq.getDef();
            sender.sendMessage(ChatColor.YELLOW + String.valueOf(i+1) + ". " + def.getDisplayName() + ChatColor.GRAY + " [" + def.getMaterial() + "]");
        }
    }
}
