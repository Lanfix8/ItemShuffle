package fr.lanfix.itemshuffle.utils;

import org.bukkit.*;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class WorldManager {


    private World nextWorld;

    public World getNewWorld() {
        World world = nextWorld;
        nextWorld = null;
        return world;
    }

    public boolean hasNextWorld() {
        return this.nextWorld == null;
    }

    public void prepareNextWorld() {
        WorldCreator worldCreator = new WorldCreator("ItemShuffle-" + UUID.randomUUID());
        World world = worldCreator.createWorld();
        assert world != null;
        world.setTime(0);
        world.setDifficulty(Difficulty.NORMAL);
        this.nextWorld = world;
    }

    public static boolean deleteWorld(World world) {
        // kick all players from the world
        World mainWorld = Bukkit.getWorld("world");
        assert mainWorld != null;
        Location location = mainWorld.getSpawnLocation();
        for (Player player: world.getPlayers()) {
            player.teleport(location);
        }
        // unload the world
        Bukkit.unloadWorld(world, false);
        // delete the world
        File worldFolder = world.getWorldFolder();
        return deleteFile(worldFolder);
    }

    private static boolean deleteFile(File file) {
        if (file.isDirectory()) {
            File[] subFiles = file.listFiles();
            if (subFiles != null) {
                for (File subFile : subFiles) {
                    deleteFile(subFile);
                }
            }
        }
        return file.delete();
    }

}
