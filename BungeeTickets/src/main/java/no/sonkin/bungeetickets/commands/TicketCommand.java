package no.sonkin.bungeetickets.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import no.sonkin.bungeetickets.BungeeTickets;
import no.sonkin.bungeetickets.MessageBuilder;
import no.sonkin.ticketscore.exceptions.TicketException;
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
                player.sendMessage(MessageBuilder.ticketSummary(tickets));
            }

        } catch (TicketException e) {
            e.printStackTrace();
        }
    }

    @Subcommand("info")
    @Description("List details for one of your tickets")
    @Syntax("[id] - defaults to latest ticket")
    @CommandCompletion("<id>")
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
                player.sendMessage(MessageBuilder.ticket(ticket));
            }
        } catch (TicketException e) {
            player.sendMessage(MessageBuilder.error(e.getMessage()));
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
