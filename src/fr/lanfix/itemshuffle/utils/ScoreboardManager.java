package fr.lanfix.itemshuffle.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardManager {

    public static void updateScoreboard(Player player, int minutes, int seconds, String item) {
        Scoreboard scoreboard = player.getScoreboard();
        scoreboard.getObjective("ItemShuffle").setDisplaySlot(DisplaySlot.SIDEBAR);

        scoreboard.getTeam("time").setSuffix((minutes == 0 ? "" : minutes + ":")
                + (seconds < 10 ? "0": "") + seconds + (minutes == 0 ? " seconds" : ""));
        scoreboard.getTeam("item").setSuffix(item);

        player.setScoreboard(scoreboard);
    }

    public static void newScoreboard(Player player, int minutes, int seconds, String item) {
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
        // set player scoreboard
        player.setScoreboard(scoreboard);
    }

}
