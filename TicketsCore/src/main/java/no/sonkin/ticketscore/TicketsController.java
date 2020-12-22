package no.sonkin.ticketscore;

import no.sonkin.ticketscore.models.Ticket;

import java.util.UUID;

public class TicketsController {

    private TicketsController instance;

    public TicketsController() {
        instance = this;
    }

    public boolean createTicket(UUID playerUUID, String playerName, String description) {
        return false;
    }
}
