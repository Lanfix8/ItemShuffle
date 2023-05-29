package fr.lanfix.itemshuffle.utils;

import fr.lanfix.itemshuffle.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BlacklistManager {

    private Main main;

    public BlacklistManager(Main main) {
        this.main = main;
    }

    public void addItemInHand(CommandSender sender) {
        if (sender instanceof Player) {
            String toAdd = ((Player) sender).getInventory().getItemInMainHand().getType().toString();
            this.addItem(sender, toAdd);
        } else {
            sender.sendMessage(ChatColor.RED + "Please specify an item to add, or execute this command as a player.");
        }
    }

    public void addItem(CommandSender sender, String toAdd) {
        FileConfiguration config = main.getConfig();
        try {
            Material.valueOf(toAdd);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "Please provide a correct item name (example:'CREEPER_EGG')");
            return;
        }
        List<String> blacklist = config.getStringList("items-blacklist");
        if (blacklist.contains(toAdd)) {
            sender.sendMessage(ChatColor.RED + "The blacklist already contains this item");
        } else {
            blacklist.add(toAdd);
            config.set("items-blacklist", blacklist);
            sender.sendMessage(ChatColor.GREEN +
                    "Item successfully added to the blacklist ! ($ITEM)"
                            .replace("$ITEM", toAdd));
        }
    }

    public void addHotBar(CommandSender sender) {
        if (sender instanceof Player) {
            for (int i = 0; i <= 8; i++) {
                ItemStack item = ((Player) sender).getInventory().getItem(i);
                if (item == null) continue;
                String toAdd = item.getType().toString();
                this.addItem(sender, toAdd);
            }
        } else {
            sender.sendMessage(ChatColor.RED + "This can only be executed as a player.");
        }
    }

    public void removeItemInHand(CommandSender sender) {
        if (sender instanceof Player) {
            String toRemove = ((Player) sender).getInventory().getItemInMainHand().getType().toString();
            this.removeItem(sender, toRemove);
        } else {
            sender.sendMessage(ChatColor.RED + "Please specify an item to remove, or execute this command as a player.");
        }
    }

    public void removeItem(CommandSender sender, String toRemove) {
        FileConfiguration config = main.getConfig();
        try {
            Material.valueOf(toRemove);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "Please provide a correct item name (example:'CREEPER_EGG')");
            return;
        }
        List<String> blacklist = config.getStringList("items-blacklist");
        if (blacklist.contains(toRemove)) {
            blacklist.remove(toRemove);
            config.set("items-blacklist", blacklist);
            sender.sendMessage(ChatColor.GREEN + "item successfully removed from the blacklist ! ($ITEM)"
                    .replace("$ITEM", toRemove));
        } else {
            sender.sendMessage(ChatColor.RED + "The blacklist does not contain this item");
        }
    }
}
