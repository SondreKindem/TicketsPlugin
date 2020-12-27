package no.sonkin.bungeetickets;

import co.aikar.commands.BungeeCommandManager;
import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import no.sonkin.bungeetickets.commands.TicketCommand;
import no.sonkin.bungeetickets.commands.TicketAdminCommand;
import no.sonkin.ticketscore.TicketsCore;

import java.io.*;
import java.sql.SQLException;
import java.util.logging.Level;

public class BungeeTickets extends Plugin {

    private static BungeeTickets instance;

    private Configuration config;
    private PluginMessager pluginMessager;
    private TicketsCore ticketsCore;

    @Override
    public void onEnable() {
        // Make the plugin instance available to other classes
        instance = this;

        // SETUP ACF

        setupCommandManager();

        // GET CONFIG

        loadConfig();

        // REGISTER PLUGIN MESSAGING CHANNEL

        getProxy().registerChannel("BungeeCord");

        // REGISTER LISTENERS

        pluginMessager = new PluginMessager();
        getProxy().getPluginManager().registerListener(this, pluginMessager);

        try {
            ticketsCore = new TicketsCore(getDataFolder());
        } catch (IOException | ClassNotFoundException | SQLException ex) {
            getLogger().log(Level.SEVERE, ex.getMessage(), ex);
            getLogger().severe("Disabling plugin!");
            // There is no actual way of disabling the plugin, so we just disable listeners & commands
            getProxy().getPluginManager().unregisterListeners(this);
            getProxy().getPluginManager().unregisterCommands(this);
        }
    }

    public void setupCommandManager() {
        BungeeCommandManager manager = new BungeeCommandManager(this);
        manager.enableUnstableAPI("help");

        // REGISTER COMMAND COMPLETIONS
        manager.getCommandCompletions().registerCompletion("ticketHelp", c -> ImmutableList.of("help", "create"));
        manager.getCommandCompletions().registerCompletion("ticketAdminHelp", c -> ImmutableList.of("help", "close"));

        // REGISTER COMMANDS
        manager.registerCommand(new TicketCommand());
        manager.registerCommand(new TicketAdminCommand());
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

            File configFile = new File(getDataFolder().getPath(), "config.yml");

            // If the config file does not exist we copy the provided config.yml into the plugin directory
            if (!configFile.exists()) {
                try {
                    configFile.createNewFile();
                    try (InputStream is = getResourceAsStream("config.yml");
                         OutputStream os = new FileOutputStream(configFile)) {
                            ByteStreams.copy(is, os);
                    }
                } catch (IOException e) {
                    getLogger().severe("Could not create the config!");
                    throw new RuntimeException("Could not create config!", e);
                }
            }

            this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);

        } catch (IOException e) {
            getLogger().severe("Error while trying to load the config!");
            throw new RuntimeException("Could not load the config!", e);
        }
    }

    public static BungeeTickets getInstance() {
        return instance;
    }

    public PluginMessager getPluginMessager() {
        return pluginMessager;
    }

    public Configuration getConfig() {
        return config;
    }

    public TicketsCore getTicketsCore() {
        return ticketsCore;
    }
}
