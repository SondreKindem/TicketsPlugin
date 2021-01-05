package no.sonkin.ticketshelper;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Listener that will teleport a given player once that player joins the server
 * TODO: remove this listener if player does not join withing x seconds
 */
public class TeleportOnJoinListener implements Listener {
    private String playerName;
    private int x;
    private int y;
    private int z;
    private String world;

    public TeleportOnJoinListener(String player, int x, int y, int z, String world) {
        this.playerName = player;
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Bukkit.getServer().getLogger().info("PLAYER JOINED");

        if (event.getPlayer().getName().equals(playerName)) {
            event.getPlayer().teleport(new Location(Bukkit.getServer().getWorld(world), x, y, z));
        }

        // self-destruct
        event.getHandlers().unregister(this);
    }
}
