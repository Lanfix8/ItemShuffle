package fr.lanfix.itemshuffle.storage;

import org.bukkit.entity.Player;

import java.util.UUID;

public class BestTime implements Comparable<BestTime> {

    private int time; // in seconds
    private String name;
    private final UUID uuid;

    public BestTime(int ticks, String name, UUID uuid) {
        this.time = ticks;
        this.name = name;
        this.uuid = uuid;
    }

    @Override
    public int compareTo(BestTime other) {
        return this.time - other.getTime();
    }

    public BestTime(int ticks, Player player) {
        this.time = ticks;
        this.name = player.getName();
        this.uuid = player.getUniqueId();
    }

    public boolean isBetterThan(BestTime other) {
        return this.time > other.getTime();
    }

    public int getTime() {
        return time;
    }

    public void setTime(int ticks) {
        this.time = ticks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    /**
     * @param object the reference object with which to compare.
     * @return true if the both inherit from the same player uuid (not if time equals)
     */
    @Override
    public boolean equals(Object object) {
        if (object instanceof BestTime other) {
            return this.uuid.equals(other.getUuid());
        } else {
            return super.equals(object);
        }
    }
}
