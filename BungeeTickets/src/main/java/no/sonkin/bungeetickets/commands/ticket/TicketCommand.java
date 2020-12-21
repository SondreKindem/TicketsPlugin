package no.sonkin.bungeetickets.commands.ticket;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import no.sonkin.bungeetickets.SubCommand;
import no.sonkin.bungeetickets.commands.ticket.subcommands.TicketHelpCommand;

import java.util.HashMap;

public class TicketCommand extends Command implements TabExecutor {

    private HashMap<String, SubCommand> subcommands = new HashMap<>();

    public TicketCommand() {
        super("ticket");

        // Register subcommands
        subcommands.put("help", new TicketHelpCommand());
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (strings.length >= 1) {
            SubCommand sc = subcommands.get(strings[0]);

            // No subcommand found
            if (sc == null) {
                return;
            }
            sc.execute(commandSender, strings);
        } else {
            commandSender.sendMessage(new TextComponent("Hey, no args provided"));
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] strings) {
        return subcommands.keySet();
    }
}
