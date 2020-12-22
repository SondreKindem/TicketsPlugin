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

    @Override
    public void onEnable() {
        // You should not put an enable message in your plugin.
        // BungeeCord already does so
        getLogger().info("Yay! It loads!");
        TestClass test = new TestClass();
        getLogger().info(test.test());

        loadConfig();

        getProxy().getPluginManager().registerCommand(this, new TicketCommand());
        getProxy().getPluginManager().registerCommand(this, new TicketAdminCommand());

        try {
            TicketsCore ticketsCore = new TicketsCore(getDataFolder());
        } catch (IOException | ClassNotFoundException | SQLException ex) {
            getLogger().log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    /**
     * Parse the config.yml file. Create it if it does not exist
     */
    @SuppressWarnings({"ResultOfMethodCallIgnored", "UnstableApiUsage"})
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


}
