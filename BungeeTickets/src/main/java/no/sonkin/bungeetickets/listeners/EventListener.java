package no.sonkin.bungeetickets.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import no.sonkin.bungeetickets.BungeeTickets;
import no.sonkin.bungeetickets.MessageBuilder;
import no.sonkin.ticketscore.exceptions.NotificationException;
import no.sonkin.ticketscore.models.Notification;

import java.util.List;

public class EventListener implements Listener {

    @EventHandler
    public void onJoin(PostLoginEvent event) {
        try {
            List<Notification> notifications = BungeeTickets.getInstance().getTicketsCore().getNotificationController().extractNotifications(event.getPlayer().getUniqueId());
            if (notifications != null && !notifications.isEmpty()) {
                for (Notification notification : notifications) {
                    event.getPlayer().sendMessage(MessageBuilder.notification(notification));
                }
            }
        } catch (NotificationException e) {
            ProxyServer.getInstance().getLogger().severe(e.getMessage());
        }
    }
}
