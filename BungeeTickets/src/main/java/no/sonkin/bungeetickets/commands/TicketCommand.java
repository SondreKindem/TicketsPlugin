package no.sonkin.bungeetickets.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import no.sonkin.bungeetickets.BungeeTickets;

import java.util.Arrays;

@CommandAlias("ticket")
public class TicketCommand extends BaseCommand {

    @Subcommand("create")
    @Syntax("<message>")
    @Description("Create a new ticket.")
    @CommandCompletion("<description>")
    public static void create(ProxiedPlayer player, String[] args) {
        if(args.length > 0) {
            player.sendMessage(new TextComponent("Created a new ticket!"));
            player.sendMessage(new TextComponent(Arrays.toString(args)));

            // TODO: get player location here
            BungeeTickets.getInstance().getPluginMessager().sendCustomData(player, "Some text", 12);
        } else {
            player.sendMessage(new TextComponent("§cMissing ticket description!"));
        }

    }

    @HelpCommand
    @Description("Display ticket help.")
    public static void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }
}
