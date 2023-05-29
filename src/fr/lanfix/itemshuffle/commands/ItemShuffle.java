package fr.lanfix.itemshuffle.commands;

import fr.lanfix.itemshuffle.utils.BlacklistManager;
import fr.lanfix.itemshuffle.ItemShuffleGame;
import fr.lanfix.itemshuffle.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ItemShuffle implements CommandExecutor, TabCompleter {

    private final Main main;
    private final ItemShuffleGame game;

    public ItemShuffle(Main main, ItemShuffleGame game) {
        this.main = main;
        this.game = game;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        if (args.length == 0) {
            if (game.isRunning()) {
                Bukkit.broadcastMessage(ChatColor.RED + "[ItemShuffle] Forced stop of the game.");
                game.stop();
            } else {
                game.start();
            }
        } else {
            BlacklistManager blacklistManager = new BlacklistManager(main);
            switch (args[0].toLowerCase()) {
                case "blacklistadd", "blacklist_add", "blacklist-add" -> {
                    if (args.length == 1) {
                        blacklistManager.addItemInHand(sender);
                    } else {
                        blacklistManager.addItem(sender, args[1]);
                    }
                }
                case "blacklistaddhotbar", "blacklist_addhotbar", "blacklistadd_hotbar", "blacklist_add_hotbar",
                        "blacklist-addhotbar", "blacklistadd-hotbar", "blacklist-add-hotbar" -> {
                    blacklistManager.addHotBar(sender);
                }
                case "blacklistremove", "blacklist_remove", "blacklist-remove" -> {
                    if (args.length == 1) {
                        blacklistManager.removeItemInHand(sender);
                    } else {
                        blacklistManager.removeItem(sender, args[1]);
                    }
                }
                default -> {
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
        List<String> r = new ArrayList<>();
        switch (args.length) {
            case 1 -> {
                List<String> completeArgs = List.of("blacklistAdd", "blacklistAddHotBar", "blacklistRemove");
                completeArgs.forEach(arg -> {
                    if (arg.toLowerCase().startsWith(args[0].toLowerCase())) r.add(arg);
                });
            }
            case 2 -> {
                switch (args[0].toLowerCase()) {
                    case "blacklistadd", "blacklist_add", "blacklist-add" -> {
                        List<String> itemBlacklist = main.getConfig().getStringList("items-blacklist");
                        List<String> blacklistAddArgs = new ArrayList<>();
                        for (Material material: Material.values()) {
                            if (!itemBlacklist.contains(material.toString())) blacklistAddArgs.add(material.toString());
                        }
                        blacklistAddArgs.forEach(arg -> {
                            if (arg.toLowerCase().startsWith(args[1].toLowerCase())) {
                                r.add(arg);
                            }
                        });
                    }
                    case "blacklistremove", "blacklist_remove", "blacklist-remove" -> {
                        Bukkit.getLogger().log(Level.INFO, "remove");
                        List<String> blacklistRemoveArgs = main.getConfig().getStringList("items-blacklist");
                        blacklistRemoveArgs.forEach(arg -> {
                            if (arg.toLowerCase().startsWith(args[1].toLowerCase())) r.add(arg);
                        });
                    }
                }
            }
        }
        return r;
    }
}
