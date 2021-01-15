package no.sonkin.ticketscore.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Timestamp;
import java.util.UUID;

@DatabaseTable(tableName = "comments")
public class Comment {
    @DatabaseField(generatedId = true)
    private Integer ID;
    @DatabaseField(canBeNull = false)
    private String message;
    @DatabaseField
    private UUID playerUUID;
    @DatabaseField(canBeNull = false)
    private String playerName;
    @DatabaseField(version = true)
    private Timestamp created;
    @DatabaseField(foreign = true, canBeNull = false)
    private Ticket ticket;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }
}
