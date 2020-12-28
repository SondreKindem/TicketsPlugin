package no.sonkin.bungeetickets.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import no.sonkin.bungeetickets.BungeeTickets;
import no.sonkin.ticketscore.exceptions.TicketException;

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
                BungeeTickets.getInstance().getTicketsCore().getTicketController().closeTicket(id);
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
