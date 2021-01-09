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
    public static String prefix = "§7[§6Tickets§7] §r";
    // TODO: use a better color for separators
    public static String separator = "§b----------------------------------";
    public static String header = "§b------------- " + prefix + "§b-------------";

    public static TextComponent adminPrefix = button(prefix, "§6View all open tickets", "/ta list");

    public static TextComponent userPrefix = button(prefix, "§6View your open tickets", "/ticket list");

    public static ComponentBuilder adminHeader = new ComponentBuilder("§b------------- ")
            .append(adminPrefix)
            .append("§b-------------", ComponentBuilder.FormatRetention.NONE);

    public static ComponentBuilder userHeader = new ComponentBuilder("§b------------- ")
            .append(userPrefix)
            .append("§b-------------", ComponentBuilder.FormatRetention.NONE);

    public static TextComponent info(String message) {
        return new TextComponent(prefix + message);
    }

    public static BaseComponent[] error(String message) {
        return new ComponentBuilder(prefix).append(message).color(ChatColor.RED).create();
    }

    public static BaseComponent[] ticket(Ticket ticket, boolean sentByAdmin) {
        String commentsCommand = sentByAdmin ? "/ta comments " + ticket.getID() : "/ticket comments " + ticket.getID();
        TextComponent commentsLink = button("    §7[§eView comments§7]", "§6View comments", commentsCommand);

        ComponentBuilder ticketBuilder = new ComponentBuilder(sentByAdmin ? adminHeader : userHeader)
                .append("\n§bDisplaying ticket No. §a" + ticket.getID())
                .append("\n§e§l- §bBy: §e" + ticket.getPlayerName())
                .append("\n§e§l- §bOn §a" + ticket.getServerName() + " §bin §a" + ticket.getWorld())
                .append("\n§e§l- §bStatus: " + (ticket.isClosed() ? "§cCLOSED" : "§aOPEN")).append(ticket.isClosed() ? " §bby §e" + ticket.getClosedBy() : "")
                .append("\n§e§l- §bSubject: §a" + ticket.getDescription())
                .append("\n§e§l- §bCreated: §a" + Date.from(ticket.getCreated().toInstant()).toString());

        // Comments
        if (ticket.getComments() == null || ticket.getComments().isEmpty()) {
            ticketBuilder.append("\n§e§l- §bComments: §a0");
        } else {
            ticketBuilder.append("\n§e§l- §bComments: §a" + ticket.getComments().size()).append(commentsLink);
        }

        ticketBuilder.append("\n" + separator, ComponentBuilder.FormatRetention.NONE);

        // Add bottom menu buttons
        if (sentByAdmin) {
            ticketBuilder.append("\n");
            if (ticket.isClosed()) {
                ticketBuilder.append(button(" §7[§areopen§7] ", "Reopen ticket", "/ta reopen " + ticket.getID()));
            } else {
                ticketBuilder.append(button(" §7[§cclose§7] ", "Close ticket", "/ta close " + ticket.getID()))
                        .append(" §7[§ecomment§7] ")
                        .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ta comment add " + ticket.getID() + " "))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Add a comment")));
            }
            ticketBuilder.append("\n" + separator, ComponentBuilder.FormatRetention.NONE);
        } else {
            ticketBuilder.append("\n");
            if (!ticket.isClosed()) {
                ticketBuilder.append(button(" §7[§cclose§7] ", "Close ticket", "/ticket close " + ticket.getID()))
                        .append(" §7[§ecomment§7] ")
                        .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ticket comment add " + ticket.getID() + " "))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Add a comment")));

                ticketBuilder.append("\n" + separator, ComponentBuilder.FormatRetention.NONE);
            }
        }

        return ticketBuilder.create();
    }

    public static BaseComponent[] ticketList(List<Ticket> tickets, boolean sentByAdmin) {
        ComponentBuilder componentBuilder = new ComponentBuilder(sentByAdmin ? adminHeader : userHeader);
        for (Ticket ticket : tickets) {
            String infoCommand = (sentByAdmin ? "/ta info " : "/ticket info ") + ticket.getID();
            TextComponent detailsLink = button("§b[§edetails§b]", "§6Show more details", infoCommand);

            componentBuilder
                    .append("\n§bTicket No. §a" + ticket.getID()).append(" §b(" + (ticket.isClosed() ? "§cclosed" : "§aopen") + "§b)").append("            ").append(detailsLink)
                    .append("\n§bSubject: §a" + ticket.getDescription(), ComponentBuilder.FormatRetention.NONE)
                    .append("\n§bBy §a" + ticket.getPlayerName() + " §bon §a" + ticket.getServerName())
                    .append("\n" + separator);
        }
        return componentBuilder.create();
    }

    public static BaseComponent[] comments(Ticket ticket, boolean sentByAdmin) {
        String infoCommand = sentByAdmin ? "/ta info " + ticket.getID() : "/ticket info " + ticket.getID();
        TextComponent infoLink = button(" §b[§eview ticket§b]", "§6See ticket details", infoCommand);

        ComponentBuilder componentBuilder = new ComponentBuilder(sentByAdmin ? adminHeader : userHeader)
                .append("\n§bComments for ticket §a" + ticket.getID()).append(infoLink)
                .append("\n§e§l- §bSubject: §a" + ticket.getDescription(), ComponentBuilder.FormatRetention.NONE);

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
        TextComponent infoLink = button(" §7[§eticket§7]", "§6See ticket details", infoCommand);
        return new ComponentBuilder(sentByAdmin ? adminPrefix : userPrefix)
                .append(notification.getMessage(), ComponentBuilder.FormatRetention.NONE)
                .append(infoLink).create();
    }

    private static TextComponent button(String text, String hoverText, String command) {
        TextComponent clickableText = new TextComponent(text);
        clickableText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverText)));
        clickableText.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        return clickableText;
    }
}
