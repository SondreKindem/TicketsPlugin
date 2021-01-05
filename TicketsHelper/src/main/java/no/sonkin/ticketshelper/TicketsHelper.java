package no.sonkin.ticketshelper;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.EOFException;
import java.util.UUID;

public class TicketsHelper extends JavaPlugin implements PluginMessageListener {
    @Override
    public void onEnable() {
        //checkIfBungee();
        getLogger().info("onEnable is called!");

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void onPluginMessageReceived(String channel, Player firstOnlinePlayer, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();

        // Handle generic messages (an example)
        if (subchannel.equals("Subchannel")) {
            // Use the code sample in the 'Response' sections below to read
            // the data.
            getLogger().info("PLUGINMSG RECEIVED");
            getLogger().info("SENDING BACK!");

            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Subchannel");
            out.writeUTF("Argument");

            firstOnlinePlayer.sendPluginMessage(this, "BungeeCord", out.toByteArray());

        }
        // Handle location requests
        else if (subchannel.equals("Location")) {
            getLogger().info("GOT POSITION REQUEST");

            Player player = getServer().getPlayer(UUID.fromString(in.readUTF()));

            if (player != null) {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Location");  // Subchannel
                out.writeUTF(in.readUTF());  // Ticket ID
                out.writeInt((int) player.getLocation().getX());
                out.writeInt((int) player.getLocation().getY());
                out.writeInt((int) player.getLocation().getZ());
                out.writeUTF(player.getLocation().getWorld().getName());

                player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
            } else {
                getLogger().severe("Error while trying to get player location! Requested player was null.");
            }
        }
        // Handle teleport requests
        else if (subchannel.equals("Teleport")) {
            getLogger().info("GOT TELEPORT REQUEST");
            int x = in.readInt();
            int y = in.readInt();
            int z = in.readInt();
            String world = in.readUTF();
            String playerName = in.readUTF();

            Player player = getServer().getPlayer(playerName);

            if(player != null) {
                player.teleport(new Location(getServer().getWorld(world), x, y, z));
            } else {
                getLogger().severe("Could not teleport player " + playerName + "! Player was null");
            }
        }
        //
        else if (subchannel.equals("TeleportOnJoin")) {
            getLogger().info("GOT TELEPORT ON JOIN REQUEST");
            int x = in.readInt();
            int y = in.readInt();
            int z = in.readInt();
            String world = in.readUTF();
            String playerName = in.readUTF();

            // Register a listener for when the player actually joins the server
            // The player does not exists before the PlayerJoinEvent on the server, thus we cannot actually tp before this
            getServer().getPluginManager().registerEvents(new TeleportOnJoinListener(playerName, x, y, z, world), this);
        }
    }

    private void checkIfBungee() {
        // we check if the server is Spigot/Paper (because of the spigot.yml file)
        if (!getServer().getVersion().contains("Spigot") && !getServer().getVersion().contains("Paper")) {
            getLogger().severe("You probably run CraftBukkit... Please update atleast to spigot for this to work...");
            getLogger().severe("Plugin disabled!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        // TODO: this check does not work! Checking for bungee is disabled until this works
        if (!getServer().spigot().getConfig().getConfigurationSection("settings").getBoolean("settings.bungeecord")) {
            getLogger().severe("This server is not connected to a bungee server.");
            getLogger().severe("If the server is already hooked to BungeeCord, please enable it into your spigot.yml aswell.");
            getLogger().severe("Plugin disabled!");
            getServer().getPluginManager().disablePlugin(this);
        }
    }
}
