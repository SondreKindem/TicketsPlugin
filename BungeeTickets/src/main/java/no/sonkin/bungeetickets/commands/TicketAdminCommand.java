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
    @CommandCompletion("@allOpenTickets")
    @Description("Close a ticket")
    public static void close(CommandSender sender, Integer id) {
        try {
            Ticket ticket = BungeeTickets.getInstance().getTicketsCore().getTicketController().closeTicket(id, sender.getName());
            sender.sendMessage(MessageBuilder.info("The ticket with id §a" + id + " §rwas closed."));

            ProxiedPlayer ticketOwner = ProxyServer.getInstance().getPlayer(ticket.getPlayerUUID());
            if (ticketOwner.isConnected()) {
                ticketOwner.sendMessage(MessageBuilder.info("Your ticket with id §a" + id + " §rwas closed by " + ticket.getClosedBy()));
            } else {
                // Add to notifications
                // TODO: implement some sort of notification system
            }

        } catch (TicketException e) {
            sender.sendMessage(new TextComponent("§cCould not close ticket! Reason:\n" + e.getMessage()));
        } catch (NumberFormatException e) {
            sender.sendMessage(new TextComponent("§cNot a valid ID"));
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

    @HelpCommand
    @Description("Display ticket-admin help.")
    @Syntax("<command>")
    @CommandCompletion("help|close|tp")
    public static void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }
}
