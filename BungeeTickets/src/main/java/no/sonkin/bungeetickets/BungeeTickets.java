package no.sonkin.bungeetickets;

import co.aikar.commands.BungeeCommandManager;
import co.aikar.commands.MessageKeys;
import co.aikar.commands.MessageType;
import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import no.sonkin.bungeetickets.commands.TicketCommand;
import no.sonkin.bungeetickets.commands.TicketAdminCommand;
import no.sonkin.bungeetickets.listeners.EventListener;
import no.sonkin.ticketscore.SocketsClientHelper;
import no.sonkin.ticketscore.TicketsCore;
import no.sonkin.ticketscore.exceptions.TicketException;
import no.sonkin.ticketscore.models.Notification;
import no.sonkin.ticketscore.models.Ticket;

import java.io.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class BungeeTickets extends Plugin {

    private static BungeeTickets instance;

    private Configuration config;
    private PluginMessager pluginMessager;
    private TicketsCore ticketsCore;
    private SocketsClientHelper socketsClientHelper;

    public HashMap<String, Ticket> waitingTickets = new HashMap<>();
    public boolean socketsEnabled = false;

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
        getProxy().getPluginManager().registerListener(this, new EventListener());

        // SET UP SOCKETS

        if(config.getBoolean("discord-integration")) {
            setupSockets();
        } else {
            socketsEnabled = false;
        }

        try {

            // SET UP TICKETS CORE

            ticketsCore = new TicketsCore(getDataFolder(), config.getString("database"));

            int frequency = config.contains("notify-frequency") ? config.getInt("notify-frequency") : 10;
            if (frequency > 0) {
                ProxyServer.getInstance().getScheduler().schedule(this, () -> {
                    try {
                        List<Ticket> openTickets = BungeeTickets.getInstance().getTicketsCore().getTicketController().getOpenTickets();
                        if (openTickets != null && !openTickets.isEmpty()) {
                            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                                if (player.hasPermission("tickets.admin")) {
                                    player.sendMessage(MessageBuilder.info("There are §a" + openTickets.size() + " §ropen tickets"));
                                }
                            }
                        }
                    } catch (TicketException e) {
                        ProxyServer.getInstance().getLogger().log(Level.SEVERE, e.getMessage(), e);
                    }
                }, frequency, frequency, TimeUnit.MINUTES);
            }

        } catch (IOException | ClassNotFoundException | SQLException ex) {
            getLogger().log(Level.SEVERE, ex.getMessage(), ex);
            getLogger().severe("Disabling plugin!");
            // There is no actual way of disabling the plugin, so we just disable listeners & commands
            // TODO: Disable running tasks, like discord buffer
            getProxy().getPluginManager().unregisterListeners(this);
            getProxy().getPluginManager().unregisterCommands(this);
            closeDbConnection();
        }
    }

    public void setupCommandManager() {
        BungeeCommandManager manager = new BungeeCommandManager(this);
        //noinspection deprecation
        manager.enableUnstableAPI("help");

        // REGISTER COMMAND COMPLETIONS
        manager.getCommandCompletions().registerCompletion("allOpenTickets", c -> {
            try {
                return ticketsCore.getTicketController().getOpenTickets().stream().map(t -> String.valueOf(t.getID())).collect(Collectors.toList());
            } catch (TicketException e) {
                return ImmutableList.of("");
            }
        });

        manager.getCommandCompletions().registerCompletion("allClosedTickets", c -> {
            try {
                return ticketsCore.getTicketController().getClosedTickets().stream().map(t -> String.valueOf(t.getID())).collect(Collectors.toList());
            } catch (TicketException e) {
                return ImmutableList.of("");
            }
        });

        manager.getCommandCompletions().registerCompletion("allTickets", c -> {
            try {
                return ticketsCore.getTicketController().getAllTickets().stream().map(t -> String.valueOf(t.getID())).collect(Collectors.toList());
            } catch (TicketException e) {
                return ImmutableList.of("");
            }
        });

        manager.getCommandCompletions().registerCompletion("allTicketsForPlayer", c -> {
            try {
                return ticketsCore.getTicketController().getTicketsByPlayer(c.getPlayer().getUniqueId())
                        .stream().map(t -> String.valueOf(t.getID())).collect(Collectors.toList());
            } catch (TicketException e) {
                return ImmutableList.of("");
            }
        });

        manager.getCommandCompletions().registerCompletion("openTicketsForPlayer", c -> {
            try {
                return ticketsCore.getTicketController().getTicketsByPlayer(c.getPlayer().getUniqueId(), false)
                        .stream().map(t -> String.valueOf(t.getID())).collect(Collectors.toList());
            } catch (TicketException e) {
                return ImmutableList.of("");
            }
        });

        manager.getCommandCompletions().registerCompletion("filtering", c -> {
            // Handle filtering of open tickets. I.e. limit by player name = p:<player>
            try {
                if (c.getInput().toLowerCase().startsWith("p:")) {
                    return ticketsCore.getTicketController().getPlayersWithOpenTickets().stream().map(ticket -> "p:" + ticket.getPlayerName()).collect(Collectors.toList());
                } else if (c.getInput().toLowerCase().startsWith("s:")) {
                    return ImmutableList.of("s:open", "s:closed", "s:all");
                }
                return ImmutableList.of("p:", "s:");
            } catch (TicketException e) {
                getLogger().severe(e.getMessage());
                return ImmutableList.of("");
            }
        });

        // REGISTER COMMANDS
        manager.registerCommand(new TicketCommand().setExceptionHandler((command, registeredCommand, sender, args, t) -> {
            sender.sendMessage(MessageType.ERROR, MessageKeys.ERROR_GENERIC_LOGGED);
            return true; // mark as handled, default message will not be send to sender
        }));
        manager.registerCommand(new TicketAdminCommand().setExceptionHandler((command, registeredCommand, sender, args, t) -> {
            sender.sendMessage(MessageType.ERROR, MessageKeys.ERROR_GENERIC_LOGGED);
            return true;
        }));
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


    public void setupSockets() {
        if(!config.contains("token")) {
            getLogger().severe("Could not enable sockets, could not find token in plugin config");
            return;
        } else if(!config.contains("guild")) {
            getLogger().severe("Could not enable sockets, could not find guild in plugin config");
            return;
        }

        String token = config.getString("token");
        String guild = config.getString("guild");

        System.out.println(config.getString("guild"));
        System.out.println(config.getLong("guild"));

        if(token == null) {
            if(config.getLong("token") > 0) {
                token = String.valueOf(config.getLong("token"));
            } else {
                getLogger().severe("Could not enable sockets, malformed token. Check the plugin config!");
                return;
            }
        }

        if(guild == null) {
            if(config.getLong("guild") > 0) {
                guild = String.valueOf(config.getLong("guild"));
            } else {
                getLogger().severe("Could not enable sockets, malformed guild id. Check the plugin config!");
                return;
            }
        }

        // socketsClient = new SocketsClient(token, guild);
        socketsClientHelper = new SocketsClientHelper(token, guild);
        socketsEnabled = true;
    }

    public void notifyAdmins(Notification notification) {
        // Notify admins
        for (ProxiedPlayer onlinePlayer : ProxyServer.getInstance().getPlayers()) {
            if (onlinePlayer.hasPermission("tickets.admin")) {
                onlinePlayer.sendMessage(MessageBuilder.notification(notification, true));
            }
        }
    }

    @Override
    public void onDisable() {
        closeDbConnection();
        super.onDisable();
        // TODO: Disable running tasks, like discord buffer
    }

    private void closeDbConnection() {
        if(ticketsCore != null) {
            try {
                ticketsCore.closeConnection();
            } catch (IOException e) {
                getLogger().severe("Error while trying to close db connection: " + e.getMessage());
            }
        }
    }

    public static BungeeTickets getInstance() {
        return instance;
    }

    public PluginMessager getPluginMessager() {
        return pluginMessager;
    }

    public SocketsClientHelper getSocketsClientHelper() {
        return socketsClientHelper;
    }

    public Configuration getConfig() {
        return config;
    }

    public TicketsCore getTicketsCore() {
        return ticketsCore;
    }
}
