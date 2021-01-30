package no.sonkin.bungeetickets;

import net.md_5.bungee.api.ProxyServer;
import no.sonkin.ticketscore.exceptions.TicketException;
import no.sonkin.ticketscore.models.Comment;
import no.sonkin.ticketscore.models.Ticket;

public class HandleSockets {

    public static void addComment(Comment comment, Ticket ticket) {
        ProxyServer.getInstance().getScheduler().runAsync(
                BungeeTickets.getInstance(),
                () -> {
                    ProxyServer.getInstance().getLogger().info("TRYING TO SEND COMMENT");
                    if(ticket.getDiscordChannel() != null) {
                        comment.setDiscordChannel(ticket.getDiscordChannel());
                        if(BungeeTickets.getInstance().getSocketsClientHelper().getClient().sendComment(comment)) {
                            // OK
                            return;
                        }
                    }

                    // ADD TO BUFFER
                    ProxyServer.getInstance().getLogger().info("CANT SEND COMMENT, ADDING TO BUFFER");
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
                    ProxyServer.getInstance().getLogger().info("CANT SEND TICKET, ADDING TO BUFFER");
                }
        );
    }
}
