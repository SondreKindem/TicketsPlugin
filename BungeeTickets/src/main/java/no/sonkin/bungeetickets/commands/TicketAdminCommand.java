package no.sonkin.bungeetickets.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import net.md_5.bungee.api.CommandSender;

@CommandAlias("ticketadmin|ta")
@CommandPermission("tickets.admin")
public class TicketAdminCommand extends BaseCommand {

    @Subcommand("close")
    @Syntax("<id> <reason>")
    @CommandCompletion("<id> <reason>")
    @Description("Close a ticket")
    public static void close(CommandSender sender, String[] args) {

    }

    @HelpCommand
    @Description("Display ticket-admin help.")
    @Syntax("<command>")
    @CommandCompletion("@ticketAdminHelp")
    public static void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }
}
