package fr.lanfix.itemshuffle.storage;

import fr.lanfix.itemshuffle.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemTimes {

    private final Material material;
    private double average;
    private int averageWeight;
    private BestTime serverBest;
    private final List<BestTime> personalBests;

    private ItemTimes(Material material, double average, int averageWeight, BestTime serverBest, List<BestTime> personalBests) {
        this.material = material;
        this.average = average;
        this.averageWeight = averageWeight;
        this.serverBest = serverBest;
        this.personalBests = personalBests;
    }

    public static ItemTimes empty(Material material) {
        return new ItemTimes(material, 0, 0, null, new ArrayList<>());
    }

    public static ItemTimes loadFromYaml(Material material, YamlConfiguration data) {
        int average = data.getInt("average");
        int averageWeight = data.getInt("average_weight");
        BestTime serverBest = data.contains("server_best") ? new BestTime(
                data.getInt("server_best.time"),
                data.getString("server_best.name"),
                UUID.fromString(data.getString("server_best.uuid", ""))
        ) : null;
        ConfigurationSection personalBestsSection = data.getConfigurationSection("personal_bests");
        assert personalBestsSection != null;
        List<BestTime> personalBests = getBestTimesInSection(personalBestsSection);
        return new ItemTimes(material, average, averageWeight, serverBest, personalBests);
    }

    private static List<BestTime> getBestTimesInSection(ConfigurationSection section) {
        List<BestTime> bestTimes = new ArrayList<>();
        for (String uuid : section.getKeys(false)) {
            bestTimes.add(new BestTime(
                    section.getInt(uuid + ".time"),
                    section.getString(uuid + ".name"),
                    UUID.fromString(uuid)
            ));
        }
        return bestTimes;
    }

    public Material getMaterial() {
        return material;
    }

    public double getAverage() {
        return average;
    }

    public BestTime getServerBest() {
        return serverBest;
    }

    public BestTime getPlayersPersonalBest(Player player) {
        for (BestTime bestTime : this.personalBests) {
            if (bestTime.isFrom(player)) {
                return bestTime;
            }
        }
        return null;
    }

    public void updateTimes(BestTime bestTime) {
        updateServerBest(bestTime);
        updatePersonalBest(bestTime);
        updateAverage(bestTime.getTime());
    }

    private void updateServerBest(BestTime newServerBest) {
        if (newServerBest.isBetterThan(this.serverBest)) {
            if (this.serverBest != null) {
                Bukkit.getPlayer(newServerBest.getUuid()).sendMessage(
                        ChatColor.GOLD + "You have beaten the server record of "
                                + this.serverBest.getName() + " of " + TimeUtils.getTimeString(this.serverBest.getTime()));
            } else {
                Bukkit.getPlayer(newServerBest.getUuid()).sendMessage(
                        ChatColor.GOLD + "You have set the first server record for this item.");
            }
            this.serverBest = newServerBest;
        }
    }

    private void updatePersonalBest(BestTime newPersonalBest) {
        Player player = Bukkit.getPlayer(newPersonalBest.getUuid());
        assert player != null;
        BestTime previousBest = getPlayersPersonalBest(player);
        if (newPersonalBest.isBetterThan(previousBest)) {
            if (previousBest != null) {
                player.sendMessage(ChatColor.GOLD + "You have beaten your previous record of "
                                + TimeUtils.getTimeString(previousBest.getTime()));
            } else {
                player.sendMessage(ChatColor.GOLD + "You have set your personal record to "
                        + TimeUtils.getTimeString(newPersonalBest.getTime()));
            }
            personalBests.removeIf(bestTime -> bestTime.isFrom(player));
            personalBests.add(newPersonalBest);
        }
    }

    private void updateAverage(int ticks) {
        this.average = (average * averageWeight + ticks) / (averageWeight + 1);
        this.averageWeight++;
    }

    public void saveData(File dataFolder) {
        File file = new File(dataFolder.getPath() + "/" + material.name() + ".yml");
        YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
        data.set("average", average);
        data.set("average_weight", averageWeight);
        if (serverBest != null) {
            data.set("server_best.time", serverBest.getTime());
            data.set("server_best.name", serverBest.getName());
            data.set("server_best.uuid", serverBest.getUuid().toString());
        }
        ConfigurationSection personalBestsSection = data.getConfigurationSection("personal_bests");
        if (personalBestsSection == null) personalBestsSection = data.createSection("personal_bests");
        savePersonalBestsSection(personalBestsSection);
        data.set("personal_bests", personalBestsSection);
        try {
            data.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void savePersonalBestsSection(ConfigurationSection section) {
        for (BestTime personalBest : this.personalBests) {
            String uuid = personalBest.getUuid().toString();
            section.set(uuid + ".time", personalBest.getTime());
            section.set(uuid + ".name", personalBest.getName());
        }
    }


    /**
     * @param object Object to compare
     * @return true if they are from the same material
     */
    @Override
    public boolean equals(Object object) {
        if (object instanceof ItemTimes other) {
            return this.material.equals(other.getMaterial());
        } else {
            return super.equals(object);
        }
    }
}
