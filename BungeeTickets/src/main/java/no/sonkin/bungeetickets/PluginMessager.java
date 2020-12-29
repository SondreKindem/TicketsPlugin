package no.sonkin.bungeetickets;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import no.sonkin.ticketscore.exceptions.TicketException;
import no.sonkin.ticketscore.models.Ticket;

import java.util.Collection;

public class PluginMessager implements Listener {

    public void sendCustomData(ProxiedPlayer player, String data1, int data2) {
        Collection<ProxiedPlayer> networkPlayers = ProxyServer.getInstance().getPlayers();
        // perform a check to see if globally are no players
        if (networkPlayers == null || networkPlayers.isEmpty()) {
            return;
        }
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Subchannel"); // the channel could be whatever you want
        out.writeUTF(data1); // this data could be whatever you want
        out.writeInt(data2); // this data could be whatever you want

        // we send the data to the server
        // using ServerInfo the packet is being queued if there are no players in the server
        // using only the server to send data the packet will be lost if no players are in it
        player.getServer().getInfo().sendData("BungeeCord", out.toByteArray());
    }

    /**
     * Send a plugin message requesting the positon of a player
     * @param player The player we want to get the location of
     * @param ticketKey the key for the ticket
     */
    public void requestLocation(ProxiedPlayer player, String ticketKey) {
        Collection<ProxiedPlayer> networkPlayers = ProxyServer.getInstance().getPlayers();
        // perform a check to see if globally are no players
        if (networkPlayers == null || networkPlayers.isEmpty()) {
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Location");
        out.writeUTF(ticketKey);

        // Send the request
        player.getServer().getInfo().sendData("BungeeCord", out.toByteArray());

        // TODO: create a timeout for when no location is returned
    }

    /**
     * This event receives all plugin messages sent on the BungeeCord channel
     * @param event the received event
     */
    @SuppressWarnings("UnstableApiUsage")
    @EventHandler
    public void pluginMessageRecieved(PluginMessageEvent event) {
        if (!event.getTag().equalsIgnoreCase("BungeeCord")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        String subChannel = in.readUTF();

        // Generic example
        if (subChannel.equalsIgnoreCase("Subchannel")) {
            ProxyServer.getInstance().getLogger().info("RECIEVED MESSAGE");

            // the receiver is a ProxiedPlayer when a server talks to the proxy
            if (event.getReceiver() instanceof ProxiedPlayer) {
                ProxiedPlayer receiver = (ProxiedPlayer) event.getReceiver();
                // do things
                ProxyServer.getInstance().getLogger().info("FROM " + receiver.getName());
            }

            // the receiver is a server when the proxy talks to a server
            if (event.getReceiver() instanceof Server) {
                Server receiver = (Server) event.getReceiver();
                // do things
                ProxyServer.getInstance().getLogger().info("FROM " + receiver.getInfo().getName());
            }

        }
        // Handle location request returns
        else if (subChannel.equalsIgnoreCase("Location")) {

            ProxyServer.getInstance().getLogger().info("RECEIVED POSITION BACK");
            String ticketID = in.readUTF();  // ticket id
            int x = in.readInt();
            int y = in.readInt();
            int z = in.readInt();
            String world = in.readUTF();

            if (event.getReceiver() instanceof ProxiedPlayer) {
                ProxiedPlayer receiver = (ProxiedPlayer) event.getReceiver();

                Ticket ticket = BungeeTickets.getInstance().waitingTickets.get(ticketID);

                if(ticket != null) {
                    ticket.setX(x);
                    ticket.setY(y);
                    ticket.setZ(z);
                    ticket.setWorld(world);

                    BungeeTickets.getInstance().waitingTickets.remove(ticketID);
                    try {
                        Ticket createdTicket = BungeeTickets.getInstance().getTicketsCore().getTicketController().createTicket(ticket);

                        // do things
                        receiver.sendMessage(MessageBuilder.info("Created ticket!"));
                        receiver.sendMessage(MessageBuilder.ticket(createdTicket));

                    } catch (TicketException e) {
                        receiver.sendMessage(new TextComponent("§cCould not create ticket: " + e.getMessage()));
                    }

                }
            }
        }
    }


}
