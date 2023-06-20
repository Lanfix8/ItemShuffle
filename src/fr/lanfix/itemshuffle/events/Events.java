package fr.lanfix.itemshuffle.events;

import fr.lanfix.itemshuffle.ItemShuffleGame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Events implements Listener {

    private final ItemShuffleGame game;

    public Events(ItemShuffleGame game) {
        this.game = game;
    }
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if (this.game.isRunning()) {
            this.game.removePlayer(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        if (this.game.isRunning()) {
            this.game.removePlayer(event.getPlayer());
        }
    }

}
