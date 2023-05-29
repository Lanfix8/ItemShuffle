package fr.lanfix.itemshuffle;

import fr.lanfix.itemshuffle.utils.ScoreboardManager;
import fr.lanfix.itemshuffle.utils.WorldManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.*;
import java.util.logging.Level;

import static java.lang.Math.abs;

public class ItemShuffleGame {

    private final Main main;
    private WorldManager worldManager;

    private World world;

    private final Random random;
    private Material item;

    private boolean running;
    private BukkitRunnable gameLoop;

    private int ticks;
    private int seconds;
    private int minutes;

    private final Set<Player> players;

    public ItemShuffleGame(Main main) {
        this.main = main;
        this.worldManager = main.getWorldManager();
        this.random = new Random();
        this.running = false;
        this.players = new HashSet<>();
    }

    public void start() {
        this.createWorld();
        this.preparePlayers();
        this.chooseItem();
        this.ticks = 0;
        this.seconds = -10;
        this.minutes = 0;
        this.running = true;
        this.gameLoop = new BukkitRunnable() {
            @Override
            public void run() {
                newTick();
            }
        };
        this.gameLoop.runTaskTimer(main, 5, 1);
    }

    private void createWorld() {
        if (worldManager.hasNextWorld()) worldManager.prepareNextWorld();
        this.world = worldManager.getNewWorld();
        this.random.setSeed(this.world.getSeed());
    }

    private void preparePlayers() {
        players.clear();
        players.addAll(Bukkit.getOnlinePlayers());
        Location spawnLocation = this.world.getSpawnLocation();
        for (Player player: players) {
            player.teleport(spawnLocation);
            player.setHealth(20);
            player.setSaturation(20);
            player.setFoodLevel(20);
            player.setGameMode(GameMode.ADVENTURE);
            player.getInventory().clear();
            player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        }
    }

    private void chooseItem() {
        List<Material> materials = new ArrayList<>();
        for (Material material: Material.values()) {
            if (material.isItem()) {
                if (!this.main.getConfig().getStringList("items-blacklist").contains(material)) {
                    materials.add(material);
                }
            }
        }
        this.item = materials.get(this.random.nextInt(materials.size()));
    }

    public void newTick() {
        this.ticks++;
        if (this.ticks == 20) {
            this.ticks -= 20;
            this.seconds += 1;
            if (this.seconds == 60) {
                this.seconds -= 60;
                this.minutes += 1;
            }
            for (Player player: players) {
                if (this.seconds == 0) {
                    player.sendTitle("Game started", "Good Luck", 10, 20, 10);
                    player.setGameMode(GameMode.SURVIVAL);
                } else if (this.seconds < 0) {
                    player.sendTitle("Game starts in " + abs(this.seconds), this.item.name(), 0, 10, 5);
                } else {
                    ScoreboardManager.updateScoreboard(player, this.minutes, this.seconds, this.item.name());
                }
            }
        }
        boolean hasWon = false;
        for (Player player: players) {
            if (player.getInventory().contains(this.item)) {
                Bukkit.broadcastMessage(
                        ChatColor.GOLD + "[ItemShuffle] " + ChatColor.GREEN + player.getName()
                                + " found the item in " + (this.minutes == 0 ? "" : this.minutes + ":")
                                + (this.seconds < 10 ? "0": "") + (this.seconds + ((double) this.ticks) / 20)
                                + (this.minutes == 0 ? " seconds." : "."));
                hasWon = true;
            }
        }
        if (hasWon) this.stop();
    }

    public void removePlayer(Player player) {
        this.players.remove(player);
    }

    public void stop() {
        this.gameLoop.cancel();
        for (Player player: this.players) {
            player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        }
        players.clear();
        if (!WorldManager.deleteWorld(this.world)) {
            Bukkit.getLogger().log(Level.WARNING,
                    ChatColor.RED + "[ItemShuffle] Could not delete world " + this.world.getName());
        }
        this.running = false;
        Bukkit.broadcastMessage(ChatColor.GOLD + "[ItemShuffle]"  + ChatColor.BLUE + " End of the game !");
        if (worldManager.hasNextWorld()) worldManager.prepareNextWorld();
    }

    public boolean isRunning() {
        return running;
    }
}
