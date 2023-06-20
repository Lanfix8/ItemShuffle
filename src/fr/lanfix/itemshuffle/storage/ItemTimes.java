package fr.lanfix.itemshuffle.storage;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

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

    public static ItemTimes loadFromYaml(YamlConfiguration data) {
        Material material = Material.getMaterial(data.getName().replace(".yml", ""));
        int average = data.getInt("average");
        int averageWeight = data.getInt("average_weight");
        BestTime serverBest = new BestTime(
                data.getInt("server_best.time"),
                data.getString("server_best.name"),
                UUID.fromString(data.getString("server_best.uuid", ""))
        );
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

    public void setAverage(double average) {
        this.average = average;
    }

    public int getAverageWeight() {
        return averageWeight;
    }

    public void setAverageWeight(int averageWeight) {
        this.averageWeight = averageWeight;
    }

    public BestTime getServerBest() {
        return serverBest;
    }

    public void updateServerBest(BestTime newServerBest) {
        if (newServerBest.isBetterThan(this.serverBest)) {
            this.serverBest = newServerBest;
        }
    }

    public void addPersonalBest(BestTime personalBest) {
        if (this.personalBests.contains(personalBest)) {
            this.personalBests.forEach(bestTime -> {
                if (bestTime.getUuid() == personalBest.getUuid() && personalBest.isBetterThan(bestTime)) {
                    bestTime.setTime(personalBest.getTime());
                    bestTime.setName(personalBest.getName());
                }
            });
        } else {
            this.personalBests.add(personalBest);
        }
    }

    public void updateAverage(int ticks) {
        this.average = (average * averageWeight + ticks) / (averageWeight + 1);
        this.averageWeight++;
    }

    public void saveData(File dataFolder) {
        File file = new File(dataFolder.getPath() + "/" + material.name() + ".yml");
        YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
        data.set("average", average);
        data.set("average_weight", averageWeight);
        data.set("server_best.time", serverBest.getTime());
        data.set("server_best.name", serverBest.getName());
        data.set("server_best.uuid", serverBest.getUuid().toString());
        ConfigurationSection personalBestsSection = data.getConfigurationSection("personal_bests");
        assert personalBestsSection != null;
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

}
