package no.sonkin.bungeetickets.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import no.sonkin.bungeetickets.BungeeTickets;
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
                player.sendMessage(info("You have no tickets :)"));
            } else {
                player.sendMessage(new TextComponent("\n§6==Here are your tickets!=="));
                player.sendMessage(new TextComponent("§9---------------------------"));

                for (Ticket ticket : tickets) {
                    player.sendMessage(new TextComponent(ticket.getID() + ": " + ticket.getDescription()));
                    player.sendMessage(new TextComponent("by: §6" + ticket.getPlayerName() + " §ron §6" + ticket.getServerName()));
                    player.sendMessage(new TextComponent("§9---------------------------"));
                }
            }

        } catch (TicketException e) {
            e.printStackTrace();
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
