package no.sonkin.bungeetickets.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import no.sonkin.bungeetickets.BungeeTickets;
import no.sonkin.ticketscore.models.Ticket;
import org.sqlite.util.StringUtils;

import java.util.Arrays;
import java.util.UUID;

@CommandAlias("ticket")
public class TicketCommand extends BaseCommand {

    @Subcommand("create")
    @Syntax("<description>")
    @Description("Create a new ticket.")
    @CommandCompletion("<description>")
    public static void create(ProxiedPlayer player, String[] args) {
        if(args.length > 0) {
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

    @HelpCommand
    @Description("Display ticket help.")
    @Syntax("<command>")
    @CommandCompletion("@ticketHelp")
    public static void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }
}
