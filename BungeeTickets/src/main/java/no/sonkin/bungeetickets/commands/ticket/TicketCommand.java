package no.sonkin.bungeetickets.commands.ticket;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import no.sonkin.bungeetickets.BungeeTickets;
import no.sonkin.bungeetickets.SubCommand;
import no.sonkin.bungeetickets.commands.ticket.subcommands.TicketCreateCommand;
import no.sonkin.bungeetickets.commands.ticket.subcommands.TicketHelpCommand;

import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;

public class TicketCommand extends Command implements TabExecutor {

    private HashMap<String, SubCommand> subcommands = new HashMap<>();

    public TicketCommand() {
        super("ticket");

        // Register subcommands
        subcommands.put("help", new TicketHelpCommand());
        subcommands.put("create", new TicketCreateCommand());
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
            BungeeTickets.getInstance().getPluginMessager().sendCustomData((ProxiedPlayer) commandSender, "Some text", 12);
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] strings) {
        ProxyServer.getInstance().getLogger().info("SIZE: " + strings.length);
        ProxyServer.getInstance().getLogger().info(Arrays.toString(strings));

        if(strings.length == 1) {
            return subcommands.keySet();
        } else if(strings.length >= 2) {
            if(subcommands.get(strings[0]) != null && subcommands.get(strings[0]).onTabComplete(commandSender, strings) != null)
                return subcommands.get(strings[0]).onTabComplete(commandSender, strings);
        }
        return new ArrayList<>();
    }
}
