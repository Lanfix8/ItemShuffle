package fr.lanfix.itemshuffle.storage;

import org.bukkit.entity.Player;

import java.util.UUID;

public class BestTime implements Comparable<BestTime> {

    private int time;
    private final String name;
    private final UUID uuid;

    public BestTime(int time, String name, UUID uuid) {
        this.time = time;
        this.name = name;
        this.uuid = uuid;
    }

    @Override
    public int compareTo(BestTime other) {
        return this.time - other.getTime();
    }

    public BestTime(int time, Player player) {
        this.time = time;
        this.name = player.getName();
        this.uuid = player.getUniqueId();
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }
}
