package no.sonkin.bungeetickets;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import no.sonkin.ticketscore.models.Comment;
import no.sonkin.ticketscore.models.Notification;
import no.sonkin.ticketscore.models.Ticket;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
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

    public static BaseComponent[] ticket(Ticket ticket, boolean sentByAdmin) {

        TextComponent commentsLink = new TextComponent(" §b[§eView comments§b]");
        commentsLink.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§6View comments")));
        if (sentByAdmin) {
            commentsLink.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ticket comments " + ticket.getID()));
        } else {
            commentsLink.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ta comments " + ticket.getID()));
        }


        return new ComponentBuilder(header)
                .append("\n§bDisplaying ticket No. §a" + ticket.getID())
                .append("\n§e§l- §bBy: §e" + ticket.getPlayerName())
                .append("\n§e§l- §bOn §a" + ticket.getServerName() + " §bin §a" + ticket.getWorld())
                .append("\n§e§l- §bStatus: " + (ticket.isClosed() ? "§cCLOSED" : "§aOPEN")).append(ticket.isClosed() ? " §bby §e" + ticket.getClosedBy() : "")
                .append("\n§e§l- §bSubject: §a" + ticket.getDescription())
                .append("\n§e§l- §bCreated: §a" + Date.from(ticket.getCreated().toInstant()).toString())
                .append("\n§e§l- §bComments: §a" + ticket.getComments().size()).append(ticket.getComments().isEmpty() ? new TextComponent("") : commentsLink)
                .append("\n" + separator)
                .event((ClickEvent) null).event((HoverEvent) null)  // Clear the hover & click
                .create();
    }

    public static BaseComponent[] ticketSummary(List<Ticket> tickets, boolean sentByAdmin) {
        ComponentBuilder componentBuilder = new ComponentBuilder(header);
        for (Ticket ticket : tickets) {
            TextComponent detailsLink = new TextComponent("§b[§edetails§b]");
            detailsLink.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§6Show more details")));
            if (sentByAdmin) {
                detailsLink.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ta info " + ticket.getID()));
            } else {
                detailsLink.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ticket info " + ticket.getID()));
            }

            componentBuilder
                    .append("\n§bTicket No. §a" + ticket.getID()).append(" §b(" + (ticket.isClosed() ? "§cclosed" : "§aopen") + "§b)").append("            ").append(detailsLink)
                    .append("\n§bSubject: §a" + ticket.getDescription())
                    .event((ClickEvent) null).event((HoverEvent) null)  // Clear the hover & click
                    .append("\n§bBy §a" + ticket.getPlayerName() + " §bon §a" + ticket.getServerName())
                    .append("\n" + separator);
        }
        return componentBuilder.create();
    }

    public static BaseComponent[] comments(Ticket ticket, boolean sentByAdmin) {
        TextComponent infoLink = new TextComponent(" §b[§eview ticket§b]");
        infoLink.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§6See ticket details")));
        if (sentByAdmin) {
            infoLink.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ta info " + ticket.getID()));
        } else {
            infoLink.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ticket info " + ticket.getID()));
        }

        ComponentBuilder componentBuilder = new ComponentBuilder(header)
                .append("\n§bComments for ticket §a" + ticket.getID()).append(infoLink)
                .append("\n§e§l- §bSubject: §a" + ticket.getDescription())
                .event((ClickEvent) null).event((HoverEvent) null);  // Clear the hover & click;

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MMM-hh:mm");
        for (Comment comment : ticket.getComments()) {
            componentBuilder
                    .append("\n§b[" + dateFormat.format(comment.getCreated()) + "] ")
                    .append(comment.getPlayerName() + ": §a" + comment.getMessage());
        }
        componentBuilder.append("\n" + separator);
        return componentBuilder.create();
    }

    public static BaseComponent[] notification(Notification notification, boolean sentByAdmin) {
        TextComponent infoLink = new TextComponent(" §b[§eticket§b]");
        infoLink.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§6See ticket details")));
        if (sentByAdmin) {
            infoLink.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ta info " + notification.getTicketId()));
        } else {
            infoLink.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ticket info " + notification.getTicketId()));
        }
        return new ComponentBuilder(prefix).append(notification.getMessage()).append(infoLink).create();
    }
}
