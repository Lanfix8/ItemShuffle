package fr.lanfix.itemshuffle.utils;

import fr.lanfix.itemshuffle.storage.BestTime;
import fr.lanfix.itemshuffle.storage.ItemTimes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardManager {

    public static void updateScoreboard(Player player, int minutes, int seconds, String item, ItemTimes itemTimes) {
        Scoreboard scoreboard = player.getScoreboard();

        Objective objective = scoreboard.getObjective("ItemShuffle");
        if (objective == null) {
            newScoreboard(player, minutes, seconds, item, itemTimes);
            return;
        }
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        scoreboard.getTeam("time").setSuffix((minutes == 0 ? "" : minutes + ":")
                + (seconds < 10 ? "0": "") + seconds + (minutes == 0 ? " seconds" : ""));
        scoreboard.getTeam("item").setSuffix(item);

        int average = (int) Math.round(itemTimes.getAverage());
        scoreboard.getTeam("average").setSuffix(TimeUtils.getTimeString(average));
        BestTime serverBest = itemTimes.getServerBest();
        scoreboard.getTeam("serverBest").setSuffix((serverBest == null) ? "Undefined" :
                (TimeUtils.getTimeString(serverBest.getTime()) + " (" + serverBest.getName() + ")"));
        BestTime personalBest = itemTimes.getPlayersPersonalBest(player);
        scoreboard.getTeam("personalBest").setSuffix((personalBest == null) ? "Undefined" :
                TimeUtils.getTimeString(personalBest.getTime()));

        player.setScoreboard(scoreboard);
    }

    public static void newScoreboard(Player player, int minutes, int seconds, String item, ItemTimes itemTimes) {
        // Create new scoreboard
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("ItemShuffle", "dummy", ChatColor.GOLD + "Item Shuffle");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        // time part
        if (scoreboard.getTeam("time") == null) scoreboard.registerNewTeam("time");
        scoreboard.getTeam("time").addEntry(ChatColor.LIGHT_PURPLE + "Time elapsed: ");
        scoreboard.getTeam("time").setSuffix((minutes == 0 ? "" : minutes + ":")
                + (seconds < 10 ? "0": "") + seconds + (minutes == 0 ? " seconds" : ""));
        objective.getScore(ChatColor.LIGHT_PURPLE + "Time elapsed: ").setScore(15);
        // add blank
        if (scoreboard.getTeam("blank") == null) scoreboard.registerNewTeam("blank");
        scoreboard.getTeam("blank").addEntry("   ");
        objective.getScore("   ").setScore(14);
        // objective item
        if (scoreboard.getTeam("item") == null) scoreboard.registerNewTeam("item");
        scoreboard.getTeam("item").addEntry(ChatColor.GREEN + "Item to get: ");
        scoreboard.getTeam("item").setSuffix(item);
        objective.getScore(ChatColor.GREEN + "Item to get: ").setScore(13);
        // add second blank
        if (scoreboard.getTeam("blank2") == null) scoreboard.registerNewTeam("blank2");
        scoreboard.getTeam("blank2").addEntry("   ");
        objective.getScore("   ").setScore(12);
        // add average info
        int average = (int) Math.round(itemTimes.getAverage());
        if (scoreboard.getTeam("average") == null) scoreboard.registerNewTeam("average");
        scoreboard.getTeam("average").addEntry(ChatColor.BLUE + "Average time: ");
        scoreboard.getTeam("average").setSuffix(TimeUtils.getTimeString(average));
        objective.getScore(ChatColor.BLUE + "Average time: ").setScore(11);
        // add server best info
        BestTime serverBest = itemTimes.getServerBest();
        if (scoreboard.getTeam("serverBest") == null) scoreboard.registerNewTeam("serverBest");
        scoreboard.getTeam("serverBest").addEntry(ChatColor.GOLD + "Server best: ");
        scoreboard.getTeam("serverBest").setSuffix((serverBest == null) ? "Undefined" :
                (TimeUtils.getTimeString(serverBest.getTime()) + " (" + serverBest.getName() + ")"));
        objective.getScore(ChatColor.GOLD + "Server best: ").setScore(10);
        // add personal best info
        BestTime personalBest = itemTimes.getPlayersPersonalBest(player);
        if (scoreboard.getTeam("personalBest") == null) scoreboard.registerNewTeam("personalBest");
        scoreboard.getTeam("personalBest").addEntry(ChatColor.YELLOW + "Personal best: ");
        scoreboard.getTeam("personalBest").setSuffix((personalBest == null) ? "Undefined" :
                TimeUtils.getTimeString(personalBest.getTime()));
        objective.getScore(ChatColor.YELLOW + "Personal best: ").setScore(10);
        // set player scoreboard
        player.setScoreboard(scoreboard);
    }

}
