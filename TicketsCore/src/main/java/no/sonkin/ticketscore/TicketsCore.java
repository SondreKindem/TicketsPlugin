package no.sonkin.ticketscore;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import no.sonkin.ticketscore.controllers.NotificationController;
import no.sonkin.ticketscore.controllers.TicketController;
import no.sonkin.ticketscore.models.Comment;
import no.sonkin.ticketscore.models.Notification;
import no.sonkin.ticketscore.models.Ticket;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class TicketsCore {

    private JdbcConnectionSource connection;
    private File dataFolder;
    private Dao<Ticket, Integer> ticketDao;
    private Dao<Notification, Integer> notificationDao;
    private TicketController ticketController;
    private NotificationController notificationController;

    public TicketsCore(File dataFolder, String dbType) throws SQLException, ClassNotFoundException, IOException {
        this.dataFolder = dataFolder;

        connection = getDBConnection(dbType);

        ticketDao = DaoManager.createDao(connection, Ticket.class);
        notificationDao = DaoManager.createDao(connection, Notification.class);

        ticketController = new TicketController(ticketDao);
        notificationController = new NotificationController(notificationDao);


        TableUtils.createTableIfNotExists(connection, Ticket.class);
        TableUtils.createTableIfNotExists(connection, Notification.class);
        TableUtils.createTableIfNotExists(connection, Comment.class);

        closeConnection();
    }

    public TicketController getTicketController() {
        return ticketController;
    }

    public NotificationController getNotificationController() {
        return notificationController;
    }

    public void closeConnection() throws IOException {
        connection.close();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private JdbcConnectionSource getDBConnection(String dbType) throws SQLException, ClassNotFoundException {
        // TODO: add persistent in-memory option https://stackoverflow.com/questions/26562084/how-to-backup-in-memory-database-or-restore-memory-database-from-file

        try {
            if (connection != null) {
                return connection;
            }

            if (!dataFolder.exists()) {
                dataFolder.mkdir();
            }

            if (dbType != null && !dbType.equalsIgnoreCase("h2")) {
                if (dbType.equalsIgnoreCase("sqlite")) {
                    // Make sure the driver for the database exists
                    Class.forName("org.sqlite.JDBC");
                    // Get database file
                    File database = new File(dataFolder, "database.db");
                    return new JdbcConnectionSource("jdbc:sqlite:" + database);
                }
            }

            // Default database is h2
            Class.forName("org.h2.Driver");
            File database = new File(dataFolder, "database");
            return new JdbcConnectionSource("jdbc:h2:" + database.getAbsolutePath());

        } catch (SQLException ex) {
            throw new SQLException("Jdbc threw error while creating connection source");
        } catch (ClassNotFoundException ex) {
            throw new ClassNotFoundException("Could not find the Sqlite JDBC library");
        }
    }
}
