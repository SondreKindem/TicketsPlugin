package no.sonkin.ticketscore;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import no.sonkin.ticketscore.managers.TicketManager;
import no.sonkin.ticketscore.models.Ticket;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class TicketsCore {

    private JdbcConnectionSource connection;
    private File dataFolder;
    private Dao<Ticket, String> ticketDao;
    private TicketManager ticketManager;

    public TicketsCore(File dataFolder) throws SQLException, ClassNotFoundException, IOException {
        this.dataFolder = dataFolder;

        connection = getDBConnection();

        ticketDao = DaoManager.createDao(connection, Ticket.class);

        ticketManager = new TicketManager(ticketDao);

        TableUtils.createTableIfNotExists(connection, Ticket.class);

        closeConnection();
    }

    public Dao<Ticket, String> getTicketDao() {
        return ticketDao;
    }

    public TicketManager getTicketManager() {
        return ticketManager;
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
