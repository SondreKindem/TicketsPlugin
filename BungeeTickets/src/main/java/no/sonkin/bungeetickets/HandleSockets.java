package no.sonkin.bungeetickets;

import net.md_5.bungee.api.ProxyServer;
import no.sonkin.ticketscore.exceptions.BufferException;
import no.sonkin.ticketscore.exceptions.TicketException;
import no.sonkin.ticketscore.models.BufferItem;
import no.sonkin.ticketscore.models.Comment;
import no.sonkin.ticketscore.models.Ticket;

public class HandleSockets {

    public static void addComment(Comment comment, Ticket ticket) {
        ProxyServer.getInstance().getScheduler().runAsync(
                BungeeTickets.getInstance(),
                () -> {
                    ProxyServer.getInstance().getLogger().info("SOCKETS: TRYING TO SEND COMMENT");
                    if(ticket.getDiscordChannel() != null) {
                        comment.setDiscordChannel(ticket.getDiscordChannel());
                        if(BungeeTickets.getInstance().getSocketsClientHelper().getClient().sendComment(comment)) {
                            // OK
                            return;
                        }
                    }

                    // ADD TO BUFFER
                    ProxyServer.getInstance().getLogger().info("SOCKETS: CANT SEND COMMENT, ADDING TO BUFFER");
                    try {
                        BungeeTickets.getInstance().getTicketsCore().getBufferController().addToBuffer(new BufferItem("comment", comment.getID()));
                    } catch (BufferException e) {
                        ProxyServer.getInstance().getLogger().severe("BufferException: " + e.getMessage());
                    }
                }
        );

    }

    public static void sendTicket(Ticket ticket) {
        ProxyServer.getInstance().getScheduler().runAsync(
                BungeeTickets.getInstance(),
                () -> {
                    String channelId = BungeeTickets.getInstance().getSocketsClientHelper().getClient().sendTicket(ticket);
                    if(channelId != null) {
                        try {
                            ticket.setDiscordChannel(channelId);
                            BungeeTickets.getInstance().getTicketsCore().getTicketController().updateTicket(ticket);
                            return;
                        } catch (TicketException e) {
                            BungeeTickets.getInstance().getLogger().severe("Error while trying to set discord channel for ticket: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    // ADD TO BUFFER
                    ProxyServer.getInstance().getLogger().info("SOCKETS: CANT SEND TICKET, ADDING TO BUFFER");
                    try {
                        BungeeTickets.getInstance().getTicketsCore().getBufferController().addToBuffer(new BufferItem("create", ticket.getID()));
                    } catch (BufferException e) {
                        ProxyServer.getInstance().getLogger().severe("BufferException: " + e.getMessage());
                    }
                }
        );
    }

    public static void closeTicket(Ticket ticket) {
        ProxyServer.getInstance().getScheduler().runAsync(
                BungeeTickets.getInstance(),
                () -> {
                    if(BungeeTickets.getInstance().getSocketsClientHelper().getClient().closeTicket(ticket)) {
                        return;
                    }

                    // ADD TO BUFFER
                    ProxyServer.getInstance().getLogger().info("SOCKETS: CANT CLOSE TICKET, ADDING TO BUFFER");
                    try {
                        BungeeTickets.getInstance().getTicketsCore().getBufferController().addToBuffer(new BufferItem("close", ticket.getID()));
                    } catch (BufferException e) {
                        ProxyServer.getInstance().getLogger().severe("BufferException: " + e.getMessage());
                    }
                }
        );
    }

    public static void reopenTicket(Ticket ticket) {
        ProxyServer.getInstance().getScheduler().runAsync(
                BungeeTickets.getInstance(),
                () -> {
                    if(BungeeTickets.getInstance().getSocketsClientHelper().getClient().reopenTicket(ticket)) {
                        return;
                    }

                    // ADD TO BUFFER
                    ProxyServer.getInstance().getLogger().info("SOCKETS: CANT REOPEN TICKET, ADDING TO BUFFER");
                    try {
                        BungeeTickets.getInstance().getTicketsCore().getBufferController().addToBuffer(new BufferItem("reopen", ticket.getID()));
                    } catch (BufferException e) {
                        ProxyServer.getInstance().getLogger().severe("BufferException: " + e.getMessage());
                    }
                }
        );
    }
}
