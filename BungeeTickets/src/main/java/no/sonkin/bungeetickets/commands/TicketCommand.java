package no.sonkin.bungeetickets.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import no.sonkin.bungeetickets.BungeeTickets;
import no.sonkin.bungeetickets.MessageBuilder;
import no.sonkin.ticketscore.exceptions.TicketException;
import no.sonkin.ticketscore.models.Ticket;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static no.sonkin.bungeetickets.MessageBuilder.*;

@CommandAlias("ticket")
public class TicketCommand extends BaseCommand {

    @Subcommand("create")
    @Syntax("<description>")
    @Description("Create a new ticket.")
    @CommandCompletion("<description>")
    public static void create(ProxiedPlayer player, String[] args) {
        if (args.length > 0) {
            player.sendMessage(new TextComponent("Created a new ticket!"));
            player.sendMessage(new TextComponent(Arrays.toString(args)));

            // Create a new ticket
            Ticket ticket = new Ticket();
            ticket.setDescription(String.join(" ", args));  // TODO: all additional args shold be joined automatically?
            ticket.setPlayerUUID(player.getUniqueId());
            ticket.setPlayerName(player.getName());
            ticket.setServerName(ProxyServer.getInstance().getPlayer(player.getUniqueId()).getServer().getInfo().getName());

            String randomID = UUID.randomUUID().toString();  // Temporary random id for hashMap

            BungeeTickets.getInstance().waitingTickets.put(randomID, ticket);  // Store the ticket, so we can append location later

            // Send a request to add location to the ticket
            BungeeTickets.getInstance().getPluginMessager().requestLocation(player, randomID);
        } else {
            player.sendMessage(new TextComponent("§cMissing ticket description!"));
        }

    }

    @Subcommand("list")
    @Description("List your open tickets")
    @Syntax("[include-closed( true | false )]")
    @CommandCompletion("false|true")
    public static void list(ProxiedPlayer player, String[] args) {
        try {
            // Parse args
            boolean includeClosed = false;
            if (args.length > 0 && args[0].equals("true")) {
                includeClosed = true;
            }

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
    public static void info(ProxiedPlayer player, String[] args) {
        try {
            Ticket ticket;
            if (args.length > 1) {
                player.sendMessage(MessageBuilder.error("Expected 1 or 0 arguments, got " + args.length + ". Usage: <id>"));
                return;
            } else if (args.length == 1) {
                ticket = BungeeTickets.getInstance().getTicketsCore().getTicketController().getTicketByPlayerAndId(Integer.parseInt(args[0]), player.getUniqueId());
            } else {
                ticket = BungeeTickets.getInstance().getTicketsCore().getTicketController().getLatestTicket(player.getUniqueId());
            }

            if (ticket == null) {
                player.sendMessage(MessageBuilder.error(args.length == 1 ? "Could not find any tickets by you with id " + args[0] : "Could not find any tickets by you!"));
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
    @CommandCompletion("@ticketHelp")
    public static void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }
}
