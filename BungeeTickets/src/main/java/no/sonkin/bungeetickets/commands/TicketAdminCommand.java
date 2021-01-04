package no.sonkin.bungeetickets.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import no.sonkin.bungeetickets.BungeeTickets;
import no.sonkin.bungeetickets.MessageBuilder;
import no.sonkin.ticketscore.exceptions.NotificationException;
import no.sonkin.ticketscore.exceptions.TicketException;
import no.sonkin.ticketscore.models.Notification;
import no.sonkin.ticketscore.models.Ticket;

import java.util.List;

@CommandAlias("ticketadmin|ta")
@CommandPermission("tickets.admin")
public class TicketAdminCommand extends BaseCommand {

    @Subcommand("close")
    @Syntax("<id>")
    @CommandCompletion("@allOpenTickets")
    @Description("Close a ticket")
    public static void close(CommandSender sender, Integer id) {
        try {
            Ticket ticket = BungeeTickets.getInstance().getTicketsCore().getTicketController().closeTicket(id, sender.getName());
            sender.sendMessage(MessageBuilder.info("The ticket with id §a" + id + " §rwas closed."));

            ProxiedPlayer ticketOwner = ProxyServer.getInstance().getPlayer(ticket.getPlayerUUID());
            if (ticketOwner != null && ticketOwner.isConnected()) {
                ticketOwner.sendMessage(MessageBuilder.info("Your ticket with id §a" + id + " §rwas closed by " + ticket.getClosedBy()));
            } else {
                // Add to notifications
                Notification notification = new Notification();
                notification.setTicketId(ticket.getID());
                notification.setMessage("Your ticket with id §a" + id + " §rwas closed by " + ticket.getClosedBy());
                notification.setRecipientUUID(ticket.getPlayerUUID());
                BungeeTickets.getInstance().getTicketsCore().getNotificationController().create(notification);
            }

        } catch (TicketException e) {
            sender.sendMessage(MessageBuilder.error("Could not close ticket! Reason:\n" + e.getMessage()));
        } catch (NumberFormatException e) {
            sender.sendMessage(MessageBuilder.error("Not a valid ID"));
        } catch (NotificationException e) {
            ProxyServer.getInstance().getLogger().severe(e.getMessage());
        }
    }

    @Subcommand("reopen")
    @Syntax("<id>")
    @CommandCompletion("@allClosedTickets")
    @Description("Reopen a ticket")
    public static void reopen(CommandSender sender, Integer id) {
        try {
            Ticket ticket = BungeeTickets.getInstance().getTicketsCore().getTicketController().reopenTicket(id, sender.getName());
            sender.sendMessage(MessageBuilder.info("The ticket with id §a" + id + " §rwas reopened."));

            ProxiedPlayer ticketOwner = ProxyServer.getInstance().getPlayer(ticket.getPlayerUUID());
            if (ticketOwner != null && ticketOwner.isConnected()) {
                ticketOwner.sendMessage(MessageBuilder.info("Your ticket with id §a" + id + " §rwas reopened by " + sender.getName()));
            } else {
                // Add to notifications
                Notification notification = new Notification();
                notification.setTicketId(ticket.getID());
                notification.setMessage("Your ticket with id §a" + id + " §rwas reopened by " + sender.getName());
                notification.setRecipientUUID(ticket.getPlayerUUID());
                BungeeTickets.getInstance().getTicketsCore().getNotificationController().create(notification);
            }

        } catch (TicketException e) {
            sender.sendMessage(MessageBuilder.error("Could not reopen ticket! Reason:\n" + e.getMessage()));
        } catch (NumberFormatException e) {
            sender.sendMessage(MessageBuilder.error("Not a valid ID"));
        } catch (NotificationException e) {
            ProxyServer.getInstance().getLogger().severe(e.getMessage());
        }
    }

    @Subcommand("tp|goto")
    @Syntax("<id>")
    @CommandCompletion("@allOpenTickets")
    @Description("Teleport to the location the ticket was created at")
    public static void tpTo(ProxiedPlayer sender, Integer id) {
        try {
            Ticket ticket = BungeeTickets.getInstance().getTicketsCore().getTicketController().getTicketById(id);

            if (ticket.isClosed()) {
                sender.sendMessage(MessageBuilder.error("Cannot tp to a closed ticket."));
                return;
            } else if (ticket.getWorld() == null && ticket.getX() == null) {
                sender.sendMessage(MessageBuilder.error("Ticket does not have a location"));
                return;
            }

            if (!sender.getServer().getInfo().getName().equals(ticket.getServerName())) {
                // If the player is not on the correct server.
                // We have to send the player to the server, then make the server tp the player
                sender.connect(ProxyServer.getInstance().getServerInfo(ticket.getServerName()));
                BungeeTickets.getInstance().getPluginMessager().requestTeleportOnJoin(ticket.getServerName(), sender, ticket.getX(), ticket.getY(), ticket.getZ(), ticket.getWorld());
            } else {
                BungeeTickets.getInstance().getPluginMessager().requestTeleport(sender, ticket.getX(), ticket.getY(), ticket.getZ(), ticket.getWorld());
            }
            sender.sendMessage(MessageBuilder.info("Teleporting to the ticket location..."));

        } catch (TicketException e) {
            sender.sendMessage(MessageBuilder.error(e.getMessage()));
        }
    }

    @Subcommand("list")
    @Syntax("<filter> = p:<player>, s:<open|closed>")
    @CommandCompletion("@openTicketsFilter")
    @Description("List tickets. Can filter by player and open/closed")
    public static void list(ProxiedPlayer sender, @Optional @Single String filter) {
        try {
            List<Ticket> tickets;

            // Handle filtering
            if (filter != null) {
                String[] filterList = filter.split(":");

                if (filterList.length != 2) {
                    throw new TicketException("No filter argument");
                }

                if (filterList[0].equals("p")) {
                    String playerName = filterList[1];
                    if (playerName.length() <= 3) {
                        throw new TicketException("Not a valid player name");
                    }
                    tickets = BungeeTickets.getInstance().getTicketsCore().getTicketController().getTicketsByPlayer(playerName, false);
                } else {
                    // If this filter has not been handled
                    throw new TicketException("Not a valid filter");
                }

            } else {
                // return all
                tickets = BungeeTickets.getInstance().getTicketsCore().getTicketController().getOpenTickets();
            }

            if (tickets.isEmpty()) {
                sender.sendMessage(MessageBuilder.info("No tickets found"));
            } else {
                sender.sendMessage(MessageBuilder.ticketSummary(tickets));
            }
        } catch (TicketException e) {
            sender.sendMessage(MessageBuilder.error(e.getMessage()));
        }
    }

    @HelpCommand
    @Description("Display ticket-admin help.")
    @Syntax("<command>")
    @CommandCompletion("help|close|tp")
    public static void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }
}
