package no.sonkin.bungeetickets;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import no.sonkin.ticketscore.models.Ticket;

import java.util.Date;
import java.util.List;

public class MessageBuilder {
    public static String prefix = "§r[§6Tickets§r] ";
    public static String separator = "§b----------------------------------";
    public static String header = "§b------------- " + prefix + "§b-------------";

    public static TextComponent info(String message) {
        return new TextComponent(prefix + message);
    }

    public static BaseComponent[] error(String message) {
        return new ComponentBuilder(prefix).append(message).color(ChatColor.RED).create();
    }

    public static BaseComponent[] ticket(Ticket ticket) {
        return new ComponentBuilder(header)
                .append("\n§bDisplaying ticket No. §a" + ticket.getID())
                .append("\n§c§l- §bBy: §a" + ticket.getPlayerName())
                .append("\n§c§l- §bOn §a" + ticket.getServerName() + " §bin §a" + ticket.getWorld())
                .append("\n§c§l- §bSubject: §a" + ticket.getDescription())
                .append("\n§c§l- §bCreated: §a" + Date.from(ticket.getCreated().toInstant()).toString())
                .append("\n" + separator)
                .create();
    }

    public static BaseComponent[] ticketSummary(List<Ticket> tickets) {
        ComponentBuilder componentBuilder = new ComponentBuilder(header);
        for (Ticket ticket : tickets) {
            TextComponent detailsLink = new TextComponent("§b[§edetails§b]");
            detailsLink.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ticket info " + ticket.getID()));
            detailsLink.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§6Show more details")));

            componentBuilder
                    .append("\n§bTicket No. §a" + ticket.getID() + "                       ").append(detailsLink)
                    .append("\n§bSubject: §a" + ticket.getDescription())
                    .append("\n§bBy §a" + ticket.getPlayerName() + " §bon §a" + ticket.getServerName())
                    .append("\n" + separator);
        }
        return componentBuilder.create();
    }
}
