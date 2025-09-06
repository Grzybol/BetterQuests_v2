package org.bestservers.command;

import org.bestservers.DailyQuestsPlugin;
import org.bestservers.model.ActiveQuest;
import org.bestservers.model.QuestDefinition;
import org.bestservers.model.ItemMatchRule;
import org.bestservers.service.InventoryScanner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import java.util.List;

/**
 * Handles /claim command: scan inventory, apply contributions, show summary.
 */
public class ClaimCommand implements CommandExecutor {
    private final DailyQuestsPlugin plugin;
    private final InventoryScanner scanner = new InventoryScanner();
    public ClaimCommand(DailyQuestsPlugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Tylko gracz może użyć tej komendy.");
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("dailyquests.user")) {
            player.sendMessage(ChatColor.RED + "Brak permisji.");
            return true;
        }
        List<ActiveQuest> active = plugin.getActiveQuestsService().getActive();
        if (active.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Brak aktywnych questów.");
            return true;
        }
        player.sendMessage(ChatColor.GOLD + "Podsumowanie rozliczenia questów:");
        for (ActiveQuest aq : active) {
            QuestDefinition def = aq.getDef();
            org.bestservers.model.PlayerQuestProgress progress = plugin.getProgressService().getProgress(player.getUniqueId(), def.getId());
            int tier = (progress != null) ? progress.getTier() : 1;
            int contributed = (progress != null) ? progress.getContributedCount() : 0;
            int required = plugin.getProgressService().getRequiredForTier(def, tier);
            int needed = required - contributed;
            if (needed <= 0) {
                player.sendMessage(ChatColor.GREEN + "Quest " + def.getDisplayName() + ": ukończony tier!");
                continue;
            }
            ItemMatchRule rule = plugin.getPluginConfig().getItemMatchRule();
            rule.setMaterial(def.getMaterial());
            int removed = scanner.scanAndRemove(player, def, needed, rule);
            org.bestservers.service.ProgressService.Result result = plugin.getProgressService().applyContribution(player.getUniqueId(), def, removed);
            player.sendMessage(ChatColor.YELLOW + "Quest " + def.getDisplayName() + ChatColor.GRAY + ": oddano " + result.usedItems + ", tier " + result.tierBefore + " -> " + result.tierAfter + ", nagroda: " + result.rewardPaid);
        }
        return true;
    }
}
