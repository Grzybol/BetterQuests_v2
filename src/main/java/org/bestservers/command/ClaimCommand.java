package org.bestservers.command;

import org.bestservers.DailyQuestsPlugin;
import org.bestservers.util.PluginLogger;
import org.bestservers.model.ActiveQuest;
import org.bestservers.model.QuestDefinition;
import org.bestservers.model.ItemMatchRule;
import org.bestservers.service.InventoryScanner;
import org.bestservers.util.QuestProgressFormatter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import java.util.List;

/**
 * Handles /oddaj command: scan inventory, apply contributions, show summary.
 */
public class ClaimCommand implements CommandExecutor {
    private final DailyQuestsPlugin plugin;
    private final InventoryScanner scanner = new InventoryScanner();
    private final PluginLogger pluginLogger;
    public ClaimCommand(DailyQuestsPlugin plugin, PluginLogger pluginLogger) {
        this.plugin = plugin;
        this.pluginLogger = pluginLogger;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        pluginLogger.log(PluginLogger.LogLevel.DEBUG, "ClaimCommand: onCommand called by " + sender.getName());

        if (!(sender instanceof Player)) {
            pluginLogger.log(PluginLogger.LogLevel.WARNING, "ClaimCommand: Command called by non-player: " + sender.getName());
            sender.sendMessage(ChatColor.RED + "Tylko gracz może użyć tej komendy.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("dailyquests.user")) {
            pluginLogger.log(PluginLogger.LogLevel.WARNING, "ClaimCommand: No permission for player " + player.getName());
            player.sendMessage(ChatColor.RED + "Brak permisji.");
            return true;
        }

        List<ActiveQuest> active = plugin.getActiveQuestsService().getActive();
        pluginLogger.log(PluginLogger.LogLevel.DEBUG, "ClaimCommand: Active quests count: " + active.size());

        if (active.isEmpty()) {
            pluginLogger.log(PluginLogger.LogLevel.WARNING, "ClaimCommand: No active quests for player " + player.getName());
            player.sendMessage(ChatColor.RED + "Brak aktywnych questów.");
            return true;
        }

        // Jeśli podano argument (np. /oddaj gold_miner)
        if (args.length == 1) {
            String questId = args[0]; // alfanumeryczne ID

            ActiveQuest target = active.stream()
                    .filter(aq -> aq.getDef().getId().equalsIgnoreCase(questId))
                    .findFirst()
                    .orElse(null);

            if (target == null) {
                player.sendMessage(ChatColor.RED + "Nie znaleziono questa o ID '" + questId + "'.");
                return true;
            }

            processQuest(player, target);
            return true;
        }
        // Domyślnie: oddaj wszystkie
        player.sendMessage(ChatColor.GOLD + "Podsumowanie rozliczenia questów:");
        for (ActiveQuest aq : active) {
            processQuest(player, aq);
        }

        return true;
    }

    private void processQuest(Player player, ActiveQuest aq) {
        QuestDefinition def = aq.getDef();
        pluginLogger.log(PluginLogger.LogLevel.DEBUG, "ClaimCommand: Processing quest " + def.getId() + " for player " + player.getName());

        org.bestservers.model.PlayerQuestProgress progress = plugin.getProgressService().getProgress(player.getUniqueId(), def.getId());
        int tier = (progress != null) ? progress.getTier() : 1;
        int contributed = (progress != null) ? progress.getContributedCount() : 0;
        int required = plugin.getProgressService().getRequiredForTier(def, tier);
        int needed = required - contributed;

        pluginLogger.log(PluginLogger.LogLevel.DEBUG, "ClaimCommand: Quest " + def.getId() + " tier: " + tier + ", contributed: " + contributed + ", required: " + required + ", needed: " + needed);

        if (needed <= 0) {
            pluginLogger.log(PluginLogger.LogLevel.INFO, "ClaimCommand: Quest completed for player " + player.getName() + ", quest: " + def.getId());
            player.sendMessage(ChatColor.GREEN + "Quest " + def.getDisplayName() + ": ukończony tier!");
            return;
        }

        ItemMatchRule rule = plugin.getPluginConfig().getItemMatchRule();
        rule.setMaterial(def.getMaterial());

        int removed = scanner.scanAndRemove(player, def, needed, rule);
        pluginLogger.log(PluginLogger.LogLevel.DEBUG, "ClaimCommand: Items removed for quest " + def.getId() + ": " + removed);

        org.bestservers.service.ProgressService.Result result = plugin.getProgressService().applyContribution(player.getUniqueId(), def, removed);

        pluginLogger.log(PluginLogger.LogLevel.INFO, "ClaimCommand: Contribution applied for player " + player.getName() +
                ", quest: " + def.getId() + ", usedItems: " + result.usedItems + ", tierBefore: " + result.tierBefore +
                ", tierAfter: " + result.tierAfter + ", rewardPaid: " + result.rewardPaid);

        /*
        player.sendMessage(ChatColor.YELLOW + "Quest " + def.getDisplayName() + ChatColor.GRAY +
                ": oddano " + result.usedItems +
                ", tier " + result.tierBefore + " -> " + result.tierAfter +
                ", nagroda: " + result.rewardPaid);

         */
        // pobierz zaktualizowany progres PO oddaniu itemów
        org.bestservers.model.PlayerQuestProgress newProgress =
                plugin.getProgressService().getProgress(player.getUniqueId(), def.getId());

        int newContributed = (newProgress != null) ? newProgress.getContributedCount() : 0;
        int newTier = (newProgress != null) ? newProgress.getTier() : 1;
        int newRequired = plugin.getProgressService().getRequiredForTier(def, newTier);

// teraz już pokazujesz faktyczny stan
        QuestProgressFormatter formatter = new QuestProgressFormatter();
        if (result.tierAfter > result.tierBefore) {
            // nowy tier – pokazujemy gratulacje
            int nextRequired = plugin.getProgressService().getRequiredForTier(def, result.tierAfter);
            formatter.sendLevelUpMessage(player, def, result.tierBefore, result.tierAfter,
                    result.rewardPaid, nextRequired);
        } else {
            // zwykły progres
            String progressMsg = formatter.formatProgressMessage(def, newContributed, newRequired);
            player.sendMessage(progressMsg);
        }

        // Dodatkowa informacja (opcjonalnie)
        player.sendMessage(ChatColor.GRAY + "Tier " + result.tierBefore + " → " + result.tierAfter
                + ", nagroda: " + result.rewardPaid);
    }

}
