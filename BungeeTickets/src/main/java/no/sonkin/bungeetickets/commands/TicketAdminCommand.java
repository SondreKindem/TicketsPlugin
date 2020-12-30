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

@CommandAlias("ticketadmin|ta")
@CommandPermission("tickets.admin")
public class TicketAdminCommand extends BaseCommand {

    @Subcommand("close")
    @Syntax("<id>")
    @CommandCompletion("<id>")
    @Description("Close a ticket")
    public static void close(CommandSender sender, String[] args) {
        try {
            if (args.length > 0) {
                int id = Integer.parseInt(args[0]);

                Ticket ticket = BungeeTickets.getInstance().getTicketsCore().getTicketController().closeTicket(id, sender.getName());
                sender.sendMessage(MessageBuilder.info("The ticket with id §a" + id + " §rwas closed."));

                ProxiedPlayer ticketOwner = ProxyServer.getInstance().getPlayer(ticket.getPlayerUUID());
                if (ticketOwner.isConnected()) {
                    ticketOwner.sendMessage(MessageBuilder.info("Your ticket with id §a" + id + " §rwas closed by " + ticket.getClosedBy()));
                } else {
                    // Add to notifications
                    // TODO: implement some sort of notification system
                }
            }
        } catch (TicketException e) {
            sender.sendMessage(new TextComponent("§cCould not close ticket! Reason:\n" + e.getMessage()));
        } catch (NumberFormatException e) {
            sender.sendMessage(new TextComponent("§cNot a valid ID"));
        }
    }

    @HelpCommand
    @Description("Display ticket-admin help.")
    @Syntax("<command>")
    @CommandCompletion("@ticketAdminHelp")
    public static void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }
}
