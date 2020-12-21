package no.sonkin.bungeetickets.commands.ticketadmin;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;

public class TicketAdminCommand extends Command implements TabExecutor {
    public TicketAdminCommand() {
        super("ticketadmin", "tickets.admin", "ta");
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] strings) {
        return new ArrayList<>();
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {

    }
}
