package no.sonkin.ticketscore.controllers;

import com.j256.ormlite.dao.Dao;
import no.sonkin.ticketscore.exceptions.NotificationException;
import no.sonkin.ticketscore.models.Notification;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class NotificationController {
    private Dao<Notification, Integer> notificationDao;

    public NotificationController(Dao<Notification, Integer> notificationDao) {
        this.notificationDao = notificationDao;
    }

    public void create(Notification notification) throws NotificationException {
        try {
            notificationDao.create(notification);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new NotificationException("Could not create notification: ");
        }
    }

    public List<Notification> extractNotifications(UUID playerUUID) throws NotificationException {
        try {
            List<Notification> notifications = notificationDao.queryForEq("recipientUUID", playerUUID);
            if (notifications != null && !notifications.isEmpty()) {
                notificationDao.delete(notifications);
            }
            return notifications;
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new NotificationException("Could not get notifications: ");
        }
    }
}
