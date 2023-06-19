package fr.lanfix.itemshuffle.storage;

import org.bukkit.entity.Player;

import java.util.UUID;

public class BestTime implements Comparable<BestTime> {

    private int time; // in seconds
    private final String name;
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

    public int getTime() {
        return time;
    }

    public void setTime(int ticks) {
        this.time = ticks;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }
}
