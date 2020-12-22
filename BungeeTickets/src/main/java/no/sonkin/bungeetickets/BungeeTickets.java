package no.sonkin.bungeetickets;

import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import no.sonkin.bungeetickets.commands.ticket.TicketCommand;
import no.sonkin.bungeetickets.commands.ticketadmin.TicketAdminCommand;
import no.sonkin.ticketscore.TestClass;
import no.sonkin.ticketscore.TicketsCore;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

public class BungeeTickets extends Plugin {

    private Configuration config;
    private Connection connection;

    @Override
    public void onEnable() {
        // You should not put an enable message in your plugin.
        // BungeeCord already does so
        getLogger().info("Yay! It loads!");
        TestClass test = new TestClass();
        getLogger().info(test.test());

        loadConfig();
        setupDB();

        getProxy().getPluginManager().registerCommand(this, new TicketCommand());
        getProxy().getPluginManager().registerCommand(this, new TicketAdminCommand());

        TicketsCore ticketsCore = new TicketsCore(connection);
    }

    /**
     * Parse the config.yml file. Create it if it does not exist
     */
    private void loadConfig() {
        try {
            getLogger().info("Trying to load config");

            if (!getDataFolder().exists()) {
                getDataFolder().mkdir();
            }

            File config = new File(getDataFolder().getPath(), "config.yml");

            if (!config.exists()) {
                try {
                    config.createNewFile();
                    try (InputStream is = getResourceAsStream("config.yml");
                         OutputStream os = new FileOutputStream(config)) {
                        ByteStreams.copy(is, os);
                    }
                } catch (IOException e) {
                    getLogger().severe("Could not create the config!");
                    throw new RuntimeException("Could not create config!", e);
                }
            }

            this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(config);

        } catch (IOException e) {
            getLogger().severe("Error while trying to load the config!");
            throw new RuntimeException("Could not load the config!", e);
        }
    }

    private Connection setupDB() {
        getLogger().info("Trying to load database");

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        // Get database file
        File database = new File(getDataFolder(), "database.db");

        if(!database.exists()) {
            try {
                database.createNewFile();
            } catch (IOException ex) {
                getLogger().log(Level.SEVERE, "Error while writing databse.db");
            }
        }

        try {
            if(connection!=null&&!connection.isClosed()){
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + database);
            return connection;
        } catch (SQLException ex) {
            getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
        return null;
    }
}
