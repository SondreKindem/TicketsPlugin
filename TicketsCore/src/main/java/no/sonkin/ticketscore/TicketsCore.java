package no.sonkin.ticketscore;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class TicketsCore {

    private Connection connection;
    private File dataFolder;

    public String SQLiteCreateTokensTable = "CREATE TABLE IF NOT EXISTS table_name (" + // make sure to put your table name in here too.
            "`player` varchar(32) NOT NULL," + // This creates the different colums you will save data too. varchar(32) Is a string, int = integer
            "`kills` int(11) NOT NULL," +
            "`total` int(11) NOT NULL," +
            "PRIMARY KEY (`player`)" +  // This is creating 3 colums Player, Kills, Total. Primary key is what you are going to use as your indexer. Here we want to use player so
            ");"; // we can search by player, and get kills and total. If you some how were searching kills it would provide total and player.

    public TicketsCore(File dataFolder) throws SQLException, ClassNotFoundException, IOException {
        this.dataFolder = dataFolder;

        connection = setupDB();

        init();
    }

    private void init() throws SQLException {
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(SQLiteCreateTokensTable);
            s.execute("INSERT INTO table_name VALUES ('hei', 5, 5)");
            s.close();
        } catch (SQLException e) {
            throw new SQLException("Could not insert into table");
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private Connection setupDB() throws SQLException, ClassNotFoundException, IOException {

        if (dataFolder.exists()) {
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
            if (connection != null && !connection.isClosed()) {
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection("jdbc:sqlite:" + database);

        } catch (SQLException ex) {
            throw new SQLException("SQLite exception on initialize");
        } catch (ClassNotFoundException ex) {
            throw new ClassNotFoundException("Could not find the Sqlite JDBC library");
        }
    }
}
