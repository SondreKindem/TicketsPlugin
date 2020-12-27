package no.sonkin.bungeetickets;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Collection;

public class PluginMessager implements Listener {

    public void sendCustomData(ProxiedPlayer player, String data1, int data2)
    {
        Collection<ProxiedPlayer> networkPlayers = ProxyServer.getInstance().getPlayers();
        // perform a check to see if globally are no players
        if ( networkPlayers == null || networkPlayers.isEmpty() )
        {
            return;
        }
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF( "Subchannel" ); // the channel could be whatever you want
        out.writeUTF( data1 ); // this data could be whatever you want
        out.writeInt( data2 ); // this data could be whatever you want

        // we send the data to the server
        // using ServerInfo the packet is being queued if there are no players in the server
        // using only the server to send data the packet will be lost if no players are in it
        player.getServer().getInfo().sendData( "BungeeCord", out.toByteArray() );
    }

    @SuppressWarnings("UnstableApiUsage")
    @EventHandler
    public void pluginMessageRecieved(PluginMessageEvent event)
    {
        if ( !event.getTag().equalsIgnoreCase( "BungeeCord" ) )
        {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput( event.getData() );
        String subChannel = in.readUTF();
        if ( subChannel.equalsIgnoreCase( "Subchannel" ) )
        {
            ProxyServer.getInstance().getLogger().info("RECIEVED MESSAGE");
            // the receiver is a ProxiedPlayer when a server talks to the proxy
            if ( event.getReceiver() instanceof ProxiedPlayer )
            {
                ProxiedPlayer receiver = (ProxiedPlayer) event.getReceiver();
                // do things
                ProxyServer.getInstance().getLogger().info("FROM " + receiver.getName());
            }
            // the receiver is a server when the proxy talks to a server
            if ( event.getReceiver() instanceof Server )
            {
                Server receiver = (Server) event.getReceiver();
                // do things
                ProxyServer.getInstance().getLogger().info("FROM " + receiver.getInfo().getName());
            }
        }
    }


}
