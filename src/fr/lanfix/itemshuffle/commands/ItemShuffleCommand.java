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

public class ItemShuffleCommand implements CommandExecutor, TabCompleter {

    private final Main main;
    private final ItemShuffleGame game;

    public ItemShuffleCommand(Main main, ItemShuffleGame game) {
        this.main = main;
        this.game = game;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        if (args.length == 0) {
            if (game.isRunning()) {
                Bukkit.broadcastMessage(ChatColor.GOLD + "[ItemShuffle]" + ChatColor.RED + " Forced stop of the game.");
                game.stop();
            } else {
                game.start(Integer.MAX_VALUE);
            }
        } else {
            BlacklistManager blacklistManager = new BlacklistManager(main);
            switch (args[0].toLowerCase()) {
                case "start" -> {
                    if (args.length == 1) {
                        if (!game.isRunning()) {
                            game.start(Integer.MAX_VALUE);
                        } else {
                            sender.sendMessage(ChatColor.RED + "A game is already running");
                        }
                        return true;
                    }
                    switch (args[1].toLowerCase()) {
                        case "maximumaveragetime" -> {
                            if (args.length == 2) {
                                sender.sendMessage(ChatColor.RED + "Please precise a maximum average time.");
                                sender.sendMessage(ChatColor.RED + "/itemshuffle start maximumAverageTime <maximumAverageTime>");
                                return true;
                            }
                            int minimumTime;
                            try {
                                minimumTime = Integer.parseInt(args[2]);
                            } catch (NumberFormatException e) {
                                sender.sendMessage(ChatColor.RED + "This is not a valid number.");
                                return true;
                            }
                            if (!game.isRunning()) {
                                game.start(minimumTime);
                            } else {
                                sender.sendMessage(ChatColor.RED + "A game is already running");
                            }
                        }
                        case "item" -> {
                            if (args.length == 2) {
                                sender.sendMessage(ChatColor.RED + "Please precise an item to select.");
                                sender.sendMessage(ChatColor.RED + "/itemshuffle start item <item>");
                                return true;
                            }
                            Material material;
                            try {
                                material = Material.valueOf(args[2]);
                            } catch (IllegalArgumentException e) {
                                sender.sendMessage(ChatColor.RED + "This is not a valid item.");
                                return true;
                            }
                            if (!game.isRunning()) {
                                game.start(material);
                            } else {
                                sender.sendMessage(ChatColor.RED + "A game is already running");
                            }
                        }
                    }
                }
                case "stop" -> {
                    if (game.isRunning()) {
                        Bukkit.broadcastMessage(ChatColor.GOLD + "[ItemShuffle]" + ChatColor.RED + " Forced stop of the game.");
                        game.stop();
                    }
                }
                case "blacklistadd", "blacklist_add", "blacklist-add" -> {
                    if (args.length == 1) {
                        blacklistManager.addItemInHand(sender);
                    } else {
                        blacklistManager.addItem(sender, args[1]);
                    }
                }
                case "blacklistaddhotbar", "blacklist_addhotbar", "blacklistadd_hotbar", "blacklist_add_hotbar",
                        "blacklist-addhotbar", "blacklistadd-hotbar", "blacklist-add-hotbar" ->
                            blacklistManager.addHotBar(sender);
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
                List<String> completeArgs = List.of("start", "stop", "blacklistAdd", "blacklistAddHotBar", "blacklistRemove");
                completeArgs.forEach(arg -> {
                    if (arg.toLowerCase().startsWith(args[0].toLowerCase())) r.add(arg);
                });
            }
            case 2 -> {
                switch (args[0].toLowerCase()) {
                    case "start" -> {
                        List<String> startArgs = List.of("maximumAverageTime", "item");
                        startArgs.forEach(arg -> {
                            if (arg.toLowerCase().startsWith(args[1].toLowerCase())) r.add(arg);
                        });
                    }
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
            case 3 -> {
                if (args[0].equalsIgnoreCase("start")) {
                    switch (args[1].toLowerCase()) {
                        case "maximumaveragetime" -> r.add("<maximumAverageTime>");
                        case "item" -> {
                            List<Material> possibleItems = List.of(Material.values());
                            possibleItems.forEach(material -> {
                                if (material.name().toLowerCase().startsWith(args[2].toLowerCase()))
                                    r.add(material.name());
                            });
                        }
                    }
                }
            }
        }
        return r;
    }
}
