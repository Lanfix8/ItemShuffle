package fr.lanfix.itemshuffle.storage;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemTimes {

    private final Material material;
    private double average;
    private int averageWeight;
    private BestTime serverBest;
    private List<BestTime> personalBests;

    private ItemTimes(Material material, double average, int averageWeight, BestTime serverBest, List<BestTime> personalBests) {
        this.material = material;
        this.average = average;
        this.averageWeight = averageWeight;
        this.serverBest = serverBest;
        this.personalBests = personalBests;
    }

    public static ItemTimes fromYaml(YamlConfiguration data) {
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

    public void setServerBest(BestTime serverBest) {
        this.serverBest = serverBest;
    }

    public void addPersonalBest(BestTime personalBest) {
        UUID uuid = personalBest.getUuid();
        // TODO Add personal best (make BestTime unique ? (how ?))
    }

}
