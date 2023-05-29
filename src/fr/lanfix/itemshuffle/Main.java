package fr.lanfix.itemshuffle;

import fr.lanfix.itemshuffle.events.Events;
import fr.lanfix.itemshuffle.utils.WorldManager;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private ItemShuffleGame game;
    private WorldManager worldManager;

    @Override
    public void onEnable() {
        this.worldManager = new WorldManager();
        this.game = new ItemShuffleGame(this);
        // save default config
        this.saveDefaultConfig();
        // register events
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new Events(this.game), this);
        // register commands
        getCommand("itemshuffle").setExecutor(new fr.lanfix.itemshuffle.commands.ItemShuffle(this, this.game));
    }

    @Override
    public void onDisable() {
        this.saveConfig();
        if (this.game.isRunning()) {
            this.game.stop();
        }
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }

    /*
    For the moment it is impossible to reduce the lag when creating a game for the world
     */
}

// TODO Give item information for weird items likes TIPPED_ARROW
// TODO Server records, averages, personal bests
