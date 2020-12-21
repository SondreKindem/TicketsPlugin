package no.sonkin.bungeetickets.commands.ticket.subcommands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import no.sonkin.bungeetickets.SubCommand;

import java.util.List;

public class TicketHelpCommand implements SubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(new TextComponent("Hey, this is the help section"));
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String[] args) {
        return null;
    }
}
