package org.bestservers.util;

import org.bestservers.model.QuestDefinition;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class QuestProgressFormatter {
    /**
     * Generates a Minecraft-style progress bar string.
     * @param percent Progress percent (0-100)
     * @param barLength Total length of the bar
     * @return Progress bar string
     */
    public static String getProgressBar(int percent, int barLength) {
        int filledLength = (int) Math.round(barLength * percent / 100.0);
        StringBuilder bar = new StringBuilder();
        bar.append("§f[");
        for (int i = 0; i < barLength; i++) {
            if (i < filledLength) {
                bar.append("§l§a|"); // bold green for filled
            } else {
                bar.append("§7|"); // gray for empty
            }
        }
        bar.append("§f]");
        return bar.toString();
    }
    public String formatProgressMessage(QuestDefinition def, int current, int required) {
        int percent = (int) ((current * 100.0) / required);

        // ile bloczków wypełnionych
        int totalBars = 20; // długość paska
        int filledBars = (int) Math.round((percent / 100.0) * totalBars);

        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < totalBars; i++) {
            if (i < filledBars) {
                bar.append(ChatColor.GREEN).append("█");
            } else {
                bar.append(ChatColor.GRAY).append("█");
            }
        }

        return ChatColor.GOLD + "[" + ChatColor.GREEN + percent + "%" + ChatColor.GOLD + "] "
                + bar.toString() + ChatColor.RESET + " "
                + ChatColor.YELLOW + def.getDisplayName();
    }

    /**
     * Formats quest progress info for chat display when submitting an item.
     * @param id Quest ID
     * @param currentTier Current tier
     * @param nextTier Next tier
     * @param nextReward Next reward
     * @param percent Progress percent (0-100)
     * @return Formatted string for chat
     */
    public static String formatProgressMessage(String id, int currentTier, int nextTier, String nextReward, int percent) {
        String bar = getProgressBar(percent, 20);
        return String.format("§6[Progress GUI] §eQuest: §b%s §7| §eTier: §a%d §7→ §a%d\n§eProgress: §b%d%% %s\n§eNext Reward: §b%s", id, currentTier, nextTier, percent, bar, nextReward);
    }

    /**
     * Formats quest progress info for chat display when leveling up.
     * @param id Quest ID
     * @param currentTier Current tier
     * @param nextTier Next tier
     * @param paidReward Paid reward
     * @return Formatted string for chat
     */
    public static String formatLevelUpMessage(String id, int currentTier, int nextTier, String paidReward) {
        return String.format("§6[Progress GUI] §eQuest: §b%s §7| §eTier: §a%d §7→ §a%d\n§eReward Claimed: §b%s", id, currentTier, nextTier, paidReward);
    }
    public void sendLevelUpMessage(Player player, QuestDefinition def,
                                    int tierBefore, int tierAfter, double rewardPaid,
                                    int nextRequired, double nextReward) {
        player.sendMessage(ChatColor.AQUA + "===============================");
        player.sendMessage(ChatColor.GREEN + "🎉 Gratulacje! " + ChatColor.YELLOW + def.getDisplayName());
        player.sendMessage(ChatColor.GRAY + "Tier " + ChatColor.GOLD + tierBefore
                + ChatColor.GRAY + " ➝ " + ChatColor.GOLD + tierAfter);
        player.sendMessage(ChatColor.GREEN + "Otrzymano nagrodę: " + ChatColor.GOLD + rewardPaid);

        if (nextRequired > 0) {
            player.sendMessage(ChatColor.YELLOW + "Następny tier: "
                    + ChatColor.AQUA + tierAfter + " ➝ " + (tierAfter + 1));
            player.sendMessage(ChatColor.GRAY + "Nagroda: " + ChatColor.GOLD + nextReward+"$"); // tu możesz dodać system nagród
            player.sendMessage(ChatColor.GRAY + "Wymagane itemki: " + ChatColor.GOLD + nextRequired);
        } else {
            player.sendMessage(ChatColor.DARK_GREEN + "Osiągnąłeś maksymalny tier!");
        }

        player.sendMessage(ChatColor.AQUA + "===============================");
        player.sendMessage(ChatColor.AQUA + "");
    }

}
