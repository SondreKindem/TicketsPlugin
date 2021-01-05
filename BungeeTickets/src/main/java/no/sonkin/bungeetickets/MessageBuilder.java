package no.sonkin.bungeetickets;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import no.sonkin.ticketscore.models.Comment;
import no.sonkin.ticketscore.models.Notification;
import no.sonkin.ticketscore.models.Ticket;

import java.text.SimpleDateFormat;
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

        String commentsCommand = sentByAdmin ? "/ta comments " + ticket.getID() : "/ticket comments " + ticket.getID();
        TextComponent commentsLink = createClickableText(" §b[§eView comments§b]", "§6View comments", commentsCommand);

        ComponentBuilder ticketBuilder = new ComponentBuilder(header)
                .append("\n§bDisplaying ticket No. §a" + ticket.getID())
                .append("\n§e§l- §bBy: §e" + ticket.getPlayerName())
                .append("\n§e§l- §bOn §a" + ticket.getServerName() + " §bin §a" + ticket.getWorld())
                .append("\n§e§l- §bStatus: " + (ticket.isClosed() ? "§cCLOSED" : "§aOPEN")).append(ticket.isClosed() ? " §bby §e" + ticket.getClosedBy() : "")
                .append("\n§e§l- §bSubject: §a" + ticket.getDescription())
                .append("\n§e§l- §bCreated: §a" + Date.from(ticket.getCreated().toInstant()).toString());

        if(ticket.getComments() == null || ticket.getComments().isEmpty()) {
            ticketBuilder.append("\n§e§l- §bComments: §a0");
        } else {
            ticketBuilder.append("\n§e§l- §bComments: §a" + ticket.getComments().size()).append(commentsLink);
        }

        ticketBuilder.append("\n" + separator)
                .event((ClickEvent) null).event((HoverEvent) null);  // Clear the hover & click

        return ticketBuilder.create();
    }

    public static BaseComponent[] ticketSummary(List<Ticket> tickets, boolean sentByAdmin) {
        ComponentBuilder componentBuilder = new ComponentBuilder(header);
        for (Ticket ticket : tickets) {
            String infoCommand = sentByAdmin ? "/ta info " + ticket.getID() : "/ticket info " + ticket.getID();
            TextComponent detailsLink = createClickableText("§b[§edetails§b]", "§6Show more details", infoCommand);

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
        String infoCommand = sentByAdmin ? "/ta info " + ticket.getID() : "/ticket info " + ticket.getID();
        TextComponent infoLink = createClickableText(" §b[§eview ticket§b]", "§6See ticket details", infoCommand);

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
        String infoCommand = sentByAdmin ? "/ta info " + notification.getTicketId() : "/ticket info " + notification.getTicketId();
        TextComponent infoLink = createClickableText(" §b[§eticket§b]", "§6See ticket details", infoCommand);
        return new ComponentBuilder(prefix).append(notification.getMessage()).append(infoLink).create();
    }

    private static TextComponent createClickableText(String text, String hoverText, String command) {
        TextComponent clickableText = new TextComponent(text);
        clickableText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverText)));
        clickableText.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        return clickableText;
    }
}
