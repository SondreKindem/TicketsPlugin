package no.sonkin.ticketscore;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import no.sonkin.ticketscore.models.Ticket;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public class TicketsCore {

    private JdbcConnectionSource connection;
    private File dataFolder;
    private Dao<Ticket, String> ticketDao;

    public String SQLiteCreateTokensTable = "CREATE TABLE IF NOT EXISTS table_name (" + // make sure to put your table name in here too.
            "`player` varchar(32) NOT NULL," + // This creates the different colums you will save data too. varchar(32) Is a string, int = integer
            "`kills` int(11) NOT NULL," +
            "`total` int(11) NOT NULL," +
            "PRIMARY KEY (`player`)" +  // This is creating 3 colums Player, Kills, Total. Primary key is what you are going to use as your indexer. Here we want to use player so
            ");"; // we can search by player, and get kills and total. If you some how were searching kills it would provide total and player.

    public TicketsCore(File dataFolder) throws SQLException, ClassNotFoundException, IOException {
        this.dataFolder = dataFolder;

        connection = getDBConnection();

        ticketDao = DaoManager.createDao(connection, Ticket.class);

        TableUtils.createTableIfNotExists(connection, Ticket.class);

        Ticket ticket = new Ticket();
        ticket.setPlayerName("Sonk1n");
        ticket.setDescription("Heisann");
        ticket.setPlayerUUID(UUID.randomUUID());
        ticket.setCreated(new Timestamp(System.currentTimeMillis()));
        //ticket.setUpdated(new Date(System.currentTimeMillis()));

        ticketDao.create(ticket);

        List<Ticket> tickets = ticketDao.queryForAll();

        // init();
    }

    private void init() throws SQLException {

    }

    public Dao<Ticket, String> getTicketDao() {
        return ticketDao;
    }

    public void closeConnection() throws IOException {
        connection.close();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private JdbcConnectionSource getDBConnection() throws SQLException, ClassNotFoundException, IOException {

        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        // Get database file
        File database = new File(dataFolder, "database.db");

        if (!database.exists()) {
            try {
                database.createNewFile();
            } catch (IOException ex) {
                throw new IOException("Error while writing databse.db");
            }
        }

        try {
            if (connection != null) {
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            return new JdbcConnectionSource("jdbc:sqlite:" + database);

        } catch (SQLException ex) {
            throw new SQLException("SQLite exception on initialize");
        } catch (ClassNotFoundException ex) {
            throw new ClassNotFoundException("Could not find the Sqlite JDBC library");
        }
    }
}
