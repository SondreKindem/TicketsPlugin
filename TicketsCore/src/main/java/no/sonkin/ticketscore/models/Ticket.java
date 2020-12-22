package no.sonkin.ticketscore.models;

import java.sql.Timestamp;
import java.util.UUID;

public class Ticket {
    private int ID;
    private String title;
    private String description;
    private UUID playerUUID;
    private String playerName;
    private String serverName;
    private Timestamp created;
    private Timestamp updated;


}
