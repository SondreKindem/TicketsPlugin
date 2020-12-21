package no.sonkin.bungeetickets;

import net.md_5.bungee.api.CommandSender;

import java.util.List;

public interface SubCommand {

    void execute(CommandSender sender, String[] args);

    List<String> onTabComplete(CommandSender commandSender, String[] args);
}
