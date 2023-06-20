package fr.lanfix.itemshuffle;

import fr.lanfix.itemshuffle.commands.ItemShuffleCommand;
import fr.lanfix.itemshuffle.events.Events;
import fr.lanfix.itemshuffle.utils.WorldManager;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

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
        getCommand("itemshuffle").setExecutor(new ItemShuffleCommand(this, this.game));

        // load times
        this.game.loadTimes(new File(getDataFolder().getPath() + "/times"));
    }

    @Override
    public void onDisable() {
        this.saveConfig();
        if (this.game.isRunning()) {
            this.game.stop();
        }
        this.game.saveTimes(new File(getDataFolder().getPath() + "/times"));
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }

    /*
    For the moment it is impossible to reduce the lag when creating a game for the world
    Fixed bug where you were told "Game started" every minute instead of just at the start
    Fixed bug with the scoreboard
     */
}

// TODO Server records, averages, personal bests
// TODO Give item information for weird items likes TIPPED_ARROW
