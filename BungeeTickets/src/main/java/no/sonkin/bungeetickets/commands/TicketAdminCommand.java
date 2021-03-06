package no.sonkin.bungeetickets.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import no.sonkin.bungeetickets.BungeeTickets;
import no.sonkin.bungeetickets.HandleSockets;
import no.sonkin.bungeetickets.MessageBuilder;
import no.sonkin.ticketscore.exceptions.NotificationException;
import no.sonkin.ticketscore.exceptions.TicketException;
import no.sonkin.ticketscore.models.Comment;
import no.sonkin.ticketscore.models.Notification;
import no.sonkin.ticketscore.models.Ticket;

import java.util.HashMap;
import java.util.List;

@CommandAlias("ticketadmin|ta")
@CommandPermission("tickets.admin")
public class TicketAdminCommand extends BaseCommand {
    // TODO: delete comments & tickets - useful for offensive stuff
    // TODO: ability to purge old tickets, maybe task for autopurging on startup

    @Subcommand("close")
    @Syntax("<id>")
    @CommandCompletion("@allOpenTickets")
    @Description("Close a ticket")
    // TODO: can close even if already closed
    public static void close(CommandSender sender, Integer id) {
        try {
            Ticket ticket = BungeeTickets.getInstance().getTicketsCore().getTicketController().closeTicket(id, sender.getName());
            ProxiedPlayer ticketOwner = ProxyServer.getInstance().getPlayer(ticket.getPlayerUUID());

            if(BungeeTickets.getInstance().socketsEnabled) {
                HandleSockets.closeTicket(ticket);
            }

            Notification notification = new Notification();
            notification.setTicketId(ticket.getID());
            notification.setMessage("Your ticket with id §a" + id + " §rwas closed by " + ticket.getClosedBy());
            notification.setRecipientUUID(ticket.getPlayerUUID());

            if (ticketOwner != null && ticketOwner.isConnected()) {
                ticketOwner.sendMessage(MessageBuilder.notification(notification, false));
            } else {
                // Add to notifications
                BungeeTickets.getInstance().getTicketsCore().getNotificationController().create(notification);
            }

            // Notify other admins
            notification.setMessage("Ticket §a" + id + " §rwas closed by " + ticket.getClosedBy());
            BungeeTickets.getInstance().notifyAdmins(notification);

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
            ProxiedPlayer ticketOwner = ProxyServer.getInstance().getPlayer(ticket.getPlayerUUID());

            if(BungeeTickets.getInstance().socketsEnabled) {
                HandleSockets.reopenTicket(ticket);
            }

            Notification notification = new Notification();
            notification.setTicketId(ticket.getID());
            notification.setMessage("Your ticket with id §a" + id + " §rwas reopened by " + sender.getName());
            notification.setRecipientUUID(ticket.getPlayerUUID());

            if (ticketOwner != null && ticketOwner.isConnected()) {
                ticketOwner.sendMessage(MessageBuilder.notification(notification, false));
            } else {
                // Add to notifications
                BungeeTickets.getInstance().getTicketsCore().getNotificationController().create(notification);
            }

            // Notify other admins
            notification.setMessage("Ticket §a" + id + " §rwas reopened by " + sender.getName());
            BungeeTickets.getInstance().notifyAdmins(notification);

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
    @CommandCompletion("@filtering")
    @Description("List tickets. Can filter by player and open/closed. If no filter, show all open tickets")
    // TODO: paginate
    public static void list(ProxiedPlayer sender, @Optional String[] filters) {
        try {
            List<Ticket> tickets;
            boolean allStatus = false;

            // Handle filtering. Kinda convoluted, probably improve
            if (filters != null && filters.length > 0) {
                HashMap<String, Object> queryFilter = new HashMap<>();
                for (String filter : filters) {
                    String[] filterSplit = filter.split(":");

                    if (filterSplit.length != 2) {
                        throw new TicketException("No filter argument: " + filter);
                    }

                    if (filterSplit[0].equals("p")) {
                        String playerName = filterSplit[1];
                        if (playerName.length() <= 3) {
                            throw new TicketException("Not a valid player name: " + playerName);
                        }
                        queryFilter.put("playerName", playerName);
                    } else if (filterSplit[0].equals("s")) {
                        String status = filterSplit[1];
                        // If all, don't add any filter
                        switch (status) {
                            case "open":
                                queryFilter.put("closed", false);
                                break;
                            case "closed":
                                queryFilter.put("closed", true);
                                break;
                            case "all":
                                allStatus = true;
                                break;
                            default:
                                throw new TicketException("Not a valid status: " + status);
                        }
                    } else {
                        // If this filter has not been handled
                        throw new TicketException("Not a valid filter");
                    }
                }

                if(queryFilter.isEmpty() && allStatus) {  // all status with no other filters
                    tickets = BungeeTickets.getInstance().getTicketsCore().getTicketController().getAllTickets();
                } else {
                    tickets = BungeeTickets.getInstance().getTicketsCore().getTicketController().getFilteredTickets(queryFilter);
                }
            } else {
                // return all
                tickets = BungeeTickets.getInstance().getTicketsCore().getTicketController().getOpenTickets();
            }

            ProxyServer.getInstance().getLogger().severe(tickets.toString());

            if (tickets.isEmpty()) {
                sender.sendMessage(MessageBuilder.info("No tickets found"));
            } else {
                sender.sendMessage(MessageBuilder.ticketList(tickets, true));
            }
        } catch (
                TicketException e) {
            sender.sendMessage(MessageBuilder.error(e.getMessage()));
        }

    }

    @Subcommand("info")
    @CommandCompletion("@allTickets")
    @Syntax("<id>")
    @Description("Show ticket info")
    public static void info(ProxiedPlayer sender, Integer id) {
        try {
            Ticket ticket = BungeeTickets.getInstance().getTicketsCore().getTicketController().getTicketById(id);
            if (ticket == null) {
                sender.sendMessage(MessageBuilder.error("Could not find a ticket with id" + id));
            } else {
                sender.sendMessage(MessageBuilder.ticket(ticket, true));
            }
        } catch (TicketException e) {
            sender.sendMessage(MessageBuilder.error(e.getMessage()));
        }
    }

    @Subcommand("comment")
    @Description("List details for one of your tickets")
    @Syntax("[id] - defaults to latest ticket")
    @CommandCompletion("@allOpenTickets <message>")
    public static void addComment(ProxiedPlayer player, @Values("@allOpenTickets") Integer id, String message) {
        try {
            Comment comment = new Comment();
            comment.setMessage(message);
            comment.setPlayerName(player.getName());
            comment.setPlayerUUID(player.getUniqueId());
            Ticket ticket = BungeeTickets.getInstance().getTicketsCore().getTicketController().addComment(comment, id);

            if(BungeeTickets.getInstance().socketsEnabled) {
                // send to discord
                HandleSockets.addComment(comment, ticket);
            }

            player.sendMessage(MessageBuilder.info("Comment added"));

            // Notify the owner of the ticket that an admin commented
            ProxiedPlayer ticketOwner = ProxyServer.getInstance().getPlayer(ticket.getPlayerUUID());

            Notification notification = new Notification();
            notification.setRecipientUUID(ticket.getPlayerUUID());
            notification.setMessage("§a" + ticket.getID() + "§r: §e" + comment.getPlayerName() + " §rcommented §a" + comment.getMessage());
            notification.setTicketId(ticket.getID());

            if (ticketOwner != null && ticketOwner.isConnected()) {
                ticketOwner.sendMessage(MessageBuilder.notification(notification, false));
            } else {
                // Ticket owner is offline
                BungeeTickets.getInstance().getTicketsCore().getNotificationController().create(notification);
            }

            BungeeTickets.getInstance().notifyAdmins(notification);

        } catch (TicketException e) {
            player.sendMessage(MessageBuilder.error(e.getMessage()));
        } catch (NotificationException e) {
            ProxyServer.getInstance().getLogger().severe(e.getMessage());
        }
    }

    @Subcommand("comments")
    @CommandCompletion("@allTickets")
    public static void comments(ProxiedPlayer player, @Values("@allTickets") Integer id) {
        try {
            Ticket ticket = BungeeTickets.getInstance().getTicketsCore().getTicketController().getTicketById(id);
            if (ticket != null) {
                player.sendMessage(MessageBuilder.comments(ticket, true));
            } else {
                player.sendMessage(MessageBuilder.error("Could not find a ticket with id §a" + id));
            }
        } catch (TicketException e) {
            e.printStackTrace();
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
