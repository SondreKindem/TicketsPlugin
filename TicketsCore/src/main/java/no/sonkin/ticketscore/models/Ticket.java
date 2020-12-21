package no.sonkin.ticketscore.models;

import java.sql.Timestamp;
import java.util.UUID;

public class Ticket {
    private int ID;
    private String title;
    private String desc;
    private UUID playerUUID;
    private String playerName;
    private Timestamp created;
    private Timestamp updated;
}
