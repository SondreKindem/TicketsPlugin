package no.sonkin.ticketscore;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import no.sonkin.ticketscore.controllers.NotificationController;
import no.sonkin.ticketscore.controllers.TicketController;
import no.sonkin.ticketscore.models.Notification;
import no.sonkin.ticketscore.models.Ticket;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class TicketsCore {

    private JdbcConnectionSource connection;
    private File dataFolder;
    private Dao<Ticket, String> ticketDao;
    private Dao<Notification, String> notificationDao;
    private TicketController ticketController;
    private NotificationController notificationController;

    public TicketsCore(File dataFolder) throws SQLException, ClassNotFoundException, IOException {
        this.dataFolder = dataFolder;

        connection = getDBConnection();

        ticketDao = DaoManager.createDao(connection, Ticket.class);
        notificationDao = DaoManager.createDao(connection, Notification.class);

        ticketController = new TicketController(ticketDao);
        notificationController = new NotificationController(notificationDao);


        TableUtils.createTableIfNotExists(connection, Ticket.class);
        TableUtils.createTableIfNotExists(connection, Notification.class);

        closeConnection();
    }

    public Dao<Ticket, String> getTicketDao() {
        return ticketDao;
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
    private JdbcConnectionSource getDBConnection() throws SQLException, ClassNotFoundException, IOException {

        try {
            if (connection != null) {
                return connection;
            }

            if (!dataFolder.exists()) {
                dataFolder.mkdir();
            }

            // Get database file
            File database = new File(dataFolder, "database.db");

            if (!database.exists()) {
                try {
                    database.createNewFile();
                } catch (IOException ex) {
                    throw new IOException("Error while writing database.db");
                }
            }

            Class.forName("org.sqlite.JDBC");
            return new JdbcConnectionSource("jdbc:sqlite:" + database);

        } catch (SQLException ex) {
            throw new SQLException("Jdbc threw error while creating connection source");
        } catch (ClassNotFoundException ex) {
            throw new ClassNotFoundException("Could not find the Sqlite JDBC library");
        }
    }
}
