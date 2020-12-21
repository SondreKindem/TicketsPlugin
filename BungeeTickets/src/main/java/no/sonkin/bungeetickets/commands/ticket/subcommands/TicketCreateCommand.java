package no.sonkin.bungeetickets.commands.ticket.subcommands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import no.sonkin.bungeetickets.SubCommand;

import java.util.ArrayList;
import java.util.Collections;

public class TicketCreateCommand implements SubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length != 2) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Invalid amount of arguments, expected 2. \nUsage: /ticket create <description>"));
            return;
        }

        sender.sendMessage(new TextComponent("Created a new ticket!"));
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] args) {
        if(args.length == 2) {
            return new ArrayList<>(Collections.singletonList("<description>"));
        }
        return new ArrayList<>();
    }
}
