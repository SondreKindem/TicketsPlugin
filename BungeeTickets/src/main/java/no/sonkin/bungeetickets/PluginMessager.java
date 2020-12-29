package no.sonkin.bungeetickets;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import no.sonkin.ticketscore.exceptions.TicketException;
import no.sonkin.ticketscore.models.Ticket;

import java.util.Collection;

@SuppressWarnings("UnstableApiUsage")
public class PluginMessager implements Listener {

    /**
     * Send a plugin message requesting the positon of a player
     * This resource has a lot of useful information: https://www.spigotmc.org/wiki/sending-a-custom-plugin-message-from-bungeecord/
     *
     * @param player    The player we want to get the location of
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
     *
     * @param event the received event
     */
    @EventHandler
    public void pluginMessageRecieved(PluginMessageEvent event) {
        if (!event.getTag().equalsIgnoreCase("BungeeCord")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        String subChannel = in.readUTF();

        if (subChannel.equalsIgnoreCase("Location")) {

            ProxyServer.getInstance().getLogger().info("RECEIVED POSITION BACK");
            String ticketID = in.readUTF();  // ticket id
            int x = in.readInt();
            int y = in.readInt();
            int z = in.readInt();
            String world = in.readUTF();

            if (event.getReceiver() instanceof ProxiedPlayer) {
                ProxiedPlayer receiver = (ProxiedPlayer) event.getReceiver();
                Ticket ticket = BungeeTickets.getInstance().waitingTickets.get(ticketID);

                if (ticket != null) {
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
