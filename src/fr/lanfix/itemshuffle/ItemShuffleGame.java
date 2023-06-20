package fr.lanfix.itemshuffle;

import fr.lanfix.itemshuffle.storage.BestTime;
import fr.lanfix.itemshuffle.storage.ItemTimes;
import fr.lanfix.itemshuffle.utils.ScoreboardManager;
import fr.lanfix.itemshuffle.utils.TimeUtils;
import fr.lanfix.itemshuffle.utils.WorldManager;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

import static java.lang.Math.abs;

public class ItemShuffleGame {

    private final Main main;
    private final WorldManager worldManager;

    private World world;

    private final Random random;
    private Material item;

    private boolean running;
    private BukkitRunnable gameLoop;

    private int ticks;
    private int seconds;
    private int minutes;

    private final Set<Player> players;
    private final List<ItemTimes> allItemTimes;
    private ItemTimes currentItemTimes;

    public ItemShuffleGame(Main main) {
        this.main = main;
        this.worldManager = main.getWorldManager();
        this.random = new Random();
        this.running = false;
        this.players = new HashSet<>();
        this.allItemTimes = new ArrayList<>();
    }

    private void start() {
        this.createWorld();
        this.preparePlayers();
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

    public void start(int maximumAverageTime) {
        this.chooseItem(maximumAverageTime);
        this.start();
    }

    public void start(Material material) {
        this.item = material;
        ItemTimes itemTimes = ItemTimes.empty(material);
        if (!allItemTimes.contains(itemTimes)) allItemTimes.add(itemTimes);
        this.currentItemTimes = allItemTimes.stream().filter(element -> element.getMaterial().equals(material)).toList().get(0);
        this.start();
    }

    public void loadTimes(File timesFolder) {
        File[] timesFiles = timesFolder.listFiles();
        if (timesFiles == null) return;
        for (File file : timesFiles) {
            allItemTimes.add(ItemTimes.loadFromYaml(
                    Material.valueOf(file.getName().replace(".yml", "")),
                    YamlConfiguration.loadConfiguration(file)));
        }
    }

    private void createWorld() {
        if (worldManager.hasNextWorld()) worldManager.prepareNextWorld();
        this.world = worldManager.getNewWorld();
        this.random.setSeed(this.world.getSeed());
    }

    private void chooseItem(int maximumAverageTime) {
        List<Material> materials = new ArrayList<>();
        for (Material material: Material.values()) {
            if (material.isItem()) {
                if (!this.main.getConfig().getStringList("items-blacklist").contains(material.name())) {
                    // material not in blacklist
                    List<ItemTimes> correspondingItemTimes = allItemTimes.stream().filter(itemTimes -> itemTimes.getMaterial().equals(material)).toList();
                    double averageTicks = 0;
                    if (correspondingItemTimes.size() > 0) averageTicks = correspondingItemTimes.get(0).getAverage();
                    if (averageTicks / 20 < maximumAverageTime) {
                        // is below maximum average time
                        materials.add(material);
                    }
                }
            }
        }
        Material selected = materials.get(this.random.nextInt(materials.size()));
        ItemTimes itemTimes = ItemTimes.empty(selected);
        if (!allItemTimes.contains(itemTimes)) allItemTimes.add(itemTimes);
        this.item = selected;
        this.currentItemTimes = allItemTimes.stream().filter(element -> element.getMaterial().equals(selected)).toList().get(0);
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
            ScoreboardManager.newScoreboard(player, 0, 0, this.item.name(), this.currentItemTimes);
        }
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
                if (this.seconds == 0 && this.minutes == 0) {
                    player.sendTitle("Game started", "Good Luck", 10, 20, 10);
                    player.setGameMode(GameMode.SURVIVAL);
                } else if (this.seconds < 0) {
                    player.sendTitle("Game starts in " + abs(this.seconds), this.item.name(), 0, 10, 5);
                } else {
                    ScoreboardManager.updateScoreboard(player, this.minutes, this.seconds, this.item.name(), this.currentItemTimes);
                }
            }
        }
        boolean hasWon = false;
        for (Player player: players) {
            if (player.getInventory().contains(this.item)) {
                Bukkit.broadcastMessage(
                        ChatColor.GOLD + "[ItemShuffle] " + ChatColor.GREEN + player.getName()
                                + " found the item in " + TimeUtils.getTimeString(this.minutes, this.seconds, this.ticks));
                BestTime bestTime = new BestTime((this.minutes * 60 + this.seconds) * 20 + this.ticks, player);
                currentItemTimes.updateTimes(bestTime);
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

    public void saveTimes(File timesFolder) {
        for (ItemTimes itemTimes : allItemTimes) {
            itemTimes.saveData(timesFolder);
        }
    }

    public boolean isRunning() {
        return running;
    }
}
