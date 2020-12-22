package no.sonkin.ticketscore;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TicketsCore {

    private Connection connection;

    public String SQLiteCreateTokensTable = "CREATE TABLE IF NOT EXISTS table_name (" + // make sure to put your table name in here too.
            "`player` varchar(32) NOT NULL," + // This creates the different colums you will save data too. varchar(32) Is a string, int = integer
            "`kills` int(11) NOT NULL," +
            "`total` int(11) NOT NULL," +
            "PRIMARY KEY (`player`)" +  // This is creating 3 colums Player, Kills, Total. Primary key is what you are going to use as your indexer. Here we want to use player so
            ");"; // we can search by player, and get kills and total. If you some how were searching kills it would provide total and player.

    public TicketsCore(Connection connection) {
        this.connection = connection;

        init();
    }

    private void init() {
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(SQLiteCreateTokensTable);
            s.execute("INSERT INTO table_name VALUES ('hei', 5, 5)");
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
