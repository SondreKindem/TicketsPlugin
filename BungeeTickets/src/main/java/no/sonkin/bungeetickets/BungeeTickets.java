package no.sonkin.bungeetickets;

import net.md_5.bungee.api.plugin.Plugin;
import no.sonkin.ticketscore.TestClass;

public class BungeeTickets extends Plugin {
    @Override
    public void onEnable() {
        // You should not put an enable message in your plugin.
        // BungeeCord already does so
        getLogger().info("Yay! It loads!");
        TestClass test = new TestClass();
        getLogger().info(test.test());
        // Wtf
    }
}
