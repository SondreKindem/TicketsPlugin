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
import no.sonkin.ticketscore.exceptions.TicketException;
import no.sonkin.ticketscore.models.Comment;
import no.sonkin.ticketscore.models.Notification;
import no.sonkin.ticketscore.models.Ticket;

import java.util.List;
import java.util.UUID;

@CommandAlias("ticket")
public class TicketCommand extends BaseCommand {

    @Subcommand("create")
    @Syntax("<description>")
    @Description("Create a new ticket.")
    @CommandCompletion("<description> @nothing")
    public static void create(ProxiedPlayer player, String description) {
        player.sendMessage(MessageBuilder.info("Attempting to create a new ticket"));

        // Create a new ticket
        Ticket ticket = new Ticket();
        ticket.setDescription(description);
        ticket.setPlayerUUID(player.getUniqueId());
        ticket.setPlayerName(player.getName());
        ticket.setServerName(ProxyServer.getInstance().getPlayer(player.getUniqueId()).getServer().getInfo().getName());

        String randomID = UUID.randomUUID().toString();  // Temporary random id for hashMap

        BungeeTickets.getInstance().waitingTickets.put(randomID, ticket);  // Store the ticket, so we can append location later

        // Send a request to add location to the ticket
        BungeeTickets.getInstance().getPluginMessager().requestLocation(player, randomID);
    }

    @Subcommand("close")
    @Syntax("<id>")
    @CommandCompletion("@openTicketsForPlayer")
    @Description("Close a ticket")
    public static void close(ProxiedPlayer sender, @Values("@openTicketsForPlayer") Integer id) {
        try {
            Ticket ticket = BungeeTickets.getInstance().getTicketsCore().getTicketController().closeTicket(id, sender.getName());
            sender.sendMessage(MessageBuilder.info("Your ticket with id §a" + id + " §rwas closed."));

            Notification notification = new Notification();
            notification.setTicketId(ticket.getID());
            notification.setMessage("The ticket with id §a" + id + " §rwas closed by " + ticket.getClosedBy());
            notification.setRecipientUUID(ticket.getPlayerUUID());

            // Notify admins
            BungeeTickets.getInstance().notifyAdmins(notification);

        } catch (TicketException e) {
            sender.sendMessage(MessageBuilder.error("Could not close ticket! Reason:\n" + e.getMessage()));
        } catch (NumberFormatException e) {
            sender.sendMessage(MessageBuilder.error("Not a valid ID"));
        }
    }

    @Subcommand("list")
    @Description("List your open tickets")
    @Syntax("[include-closed( true | false )]")
    @CommandCompletion("false|true")
    public static void list(ProxiedPlayer player, @Default("false") boolean includeClosed) {
        // TODO: paginate this. list can be too long otherwise
        try {
            // Get and display list
            List<Ticket> tickets = BungeeTickets.getInstance().getTicketsCore().getTicketController().getTicketsByPlayer(player.getUniqueId(), includeClosed);

            if (tickets.isEmpty()) {
                player.sendMessage(MessageBuilder.info("You have no tickets :)"));
            } else {
                player.sendMessage(MessageBuilder.ticketList(tickets, false));
            }

        } catch (TicketException e) {
            e.printStackTrace();
        }
    }

    @Subcommand("info")
    @Description("List details for one of your tickets")
    @Syntax("[id] - defaults to latest ticket")
    @CommandCompletion("@allTicketsForPlayer")
    public static void info(ProxiedPlayer player, @Optional Integer id) {
        try {
            Ticket ticket;
            if (id != null) {
                ticket = BungeeTickets.getInstance().getTicketsCore().getTicketController().getTicketByPlayerAndId(id, player.getUniqueId());
            } else {  // No argument
                ticket = BungeeTickets.getInstance().getTicketsCore().getTicketController().getLatestTicket(player.getUniqueId());
            }

            if (ticket == null) {
                player.sendMessage(MessageBuilder.error(id != null ? "Could not find any tickets by you with id " + id : "Could not find any tickets by you!"));
            } else {
                player.sendMessage(MessageBuilder.ticket(ticket, false));
            }
        } catch (TicketException e) {
            player.sendMessage(MessageBuilder.error(e.getMessage()));
        }
    }

    @Subcommand("comment add")
    @Description("List details for one of your tickets")
    @Syntax("[id] - defaults to latest ticket")
    @CommandCompletion("@openTicketsForPlayer <message>")
    public static void addComment(ProxiedPlayer player, @Values("@allTicketsForPlayer") Integer id, String message) {
        try {
            Comment comment = new Comment();
            comment.setMessage(message);
            comment.setPlayerName(player.getName());
            comment.setPlayerUUID(player.getUniqueId());
            Ticket ticket = BungeeTickets.getInstance().getTicketsCore().getTicketController().addComment(comment, id);

            HandleSockets.addComment(comment, ticket);

            player.sendMessage(MessageBuilder.info("Comment added"));

            // Set up a notification
            Notification notification = new Notification();
            notification.setRecipientUUID(ticket.getPlayerUUID());
            notification.setMessage("§a" + ticket.getID() + "§r: §e" + comment.getPlayerName() + " §rcommented §a" + comment.getMessage());
            notification.setTicketId(ticket.getID());

            // Notify admins
            BungeeTickets.getInstance().notifyAdmins(notification);

        } catch (TicketException e) {
            player.sendMessage(MessageBuilder.error(e.getMessage()));
        }
    }

    @Subcommand("comments")
    @CommandCompletion("@allTicketsForPlayer")
    public static void comments(ProxiedPlayer player, @Values("@allTicketsForPlayer") Integer id) {
        try {
            Ticket ticket = BungeeTickets.getInstance().getTicketsCore().getTicketController().getTicketById(id);
            if(ticket != null) {
                player.sendMessage(MessageBuilder.comments(ticket, false));
            } else {
                player.sendMessage(MessageBuilder.error("Could not find a ticket with id §a" + id));
            }
        } catch (TicketException e) {
            e.printStackTrace();
        }
    }

    @HelpCommand
    @Description("Display ticket help.")
    @Syntax("<command>")
    @CommandCompletion("help|create|list|info")
    public static void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }
}
