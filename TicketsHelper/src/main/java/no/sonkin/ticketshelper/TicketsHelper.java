package no.sonkin.ticketshelper;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

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
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        if (subchannel.equals("Subchannel")) {
            // Use the code sample in the 'Response' sections below to read
            // the data.
            getLogger().info("PLUGINMSG RECEIVED");
            getLogger().info("SENDING BACK!");

            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Subchannel");
            out.writeUTF("Argument");

            player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
        }
    }

    private void checkIfBungee()
    {
        // we check if the server is Spigot/Paper (because of the spigot.yml file)
        if ( !getServer().getVersion().contains( "Spigot" ) && !getServer().getVersion().contains( "Paper" ) )
        {
            getLogger().severe( "You probably run CraftBukkit... Please update atleast to spigot for this to work..." );
            getLogger().severe( "Plugin disabled!" );
            getServer().getPluginManager().disablePlugin( this );
            return;
        }
        // TODO: this check does not work! Checking for bungee is disabled until this works
        if ( !getServer().spigot().getConfig().getConfigurationSection("settings").getBoolean( "settings.bungeecord" ) )
        {
            getLogger().severe( "This server is not connected to a bungee server." );
            getLogger().severe( "If the server is already hooked to BungeeCord, please enable it into your spigot.yml aswell." );
            getLogger().severe( "Plugin disabled!" );
            getServer().getPluginManager().disablePlugin( this );
        }
    }
}
