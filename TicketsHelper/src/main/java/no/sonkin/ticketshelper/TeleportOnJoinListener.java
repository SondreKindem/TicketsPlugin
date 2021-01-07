package no.sonkin.ticketshelper;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

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

    Integer taskId;

    public TeleportOnJoinListener(String player, int x, int y, int z, String world) {
        this.playerName = player;
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;

        TeleportOnJoinListener self = this;

        Plugin helperPlugin = Bukkit.getPluginManager().getPlugin("TicketsHelper");

        if (helperPlugin != null) {
            taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(helperPlugin, () -> {
                // TODO: does unregistering interfere if there are multiple people trying to tp at the same time?
                HandlerList.unregisterAll(self);
                Bukkit.getLogger().severe("UNREGISTERED EVENT");
            }, 2400L); // 2 minutes
        } else {
            Bukkit.getLogger().severe("Could not get plugin instance!");
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Bukkit.getServer().getLogger().info("PLAYER JOINED");

        if (event.getPlayer().getName().equals(playerName)) {
            event.getPlayer().teleport(new Location(Bukkit.getServer().getWorld(world), x, y, z));
        }

        // self-destruct
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
        event.getHandlers().unregister(this);
    }
}
