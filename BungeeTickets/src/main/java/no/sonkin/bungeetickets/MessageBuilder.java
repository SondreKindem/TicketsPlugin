package no.sonkin.bungeetickets;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import no.sonkin.ticketscore.models.Ticket;

import java.util.Date;

public class MessageBuilder {
    public static String prefix = "§r[§6Tickets§r] ";
    public static String separator = "§b--------------------------------";

    public static TextComponent info(String message) {
        return new TextComponent(prefix + message);
    }

    public static BaseComponent[] error(String message) {
        return new ComponentBuilder(prefix).append(message).color(ChatColor.RED).create();
    }

    public static BaseComponent[] ticket(Ticket ticket) {
        return new ComponentBuilder("§b------------- " + prefix + "§b-------------")
                .append("\n§bDisplaying ticket No. §a" + ticket.getID())
                .append("\n§c§l- §bBy: §a" + ticket.getPlayerName())
                .append("\n§c§l- §bon §a" + ticket.getServerName() + " §bin §a" + ticket.getWorld())
                .append("\n§c§l- §bSubject: §a" + ticket.getDescription())
                .append("\n§c§l- §bCreated: §a" + Date.from(ticket.getCreated().toInstant()).toString())
                .append("\n" + separator)
                .create();
    }

    public static BaseComponent[] ticketSummary(Ticket ticket) {
        return new ComponentBuilder().create();
    }
}
