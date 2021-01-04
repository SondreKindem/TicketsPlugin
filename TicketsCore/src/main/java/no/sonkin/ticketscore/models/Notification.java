package no.sonkin.ticketscore.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Timestamp;
import java.util.UUID;

@DatabaseTable(tableName = "notifications")
public class Notification {
    @DatabaseField(generatedId = true)
    private Integer ID;
    @DatabaseField(canBeNull = false)
    private UUID recipientUUID;
    @DatabaseField(canBeNull = false)
    private String message;
    @DatabaseField
    private Integer ticketId;
    @DatabaseField(version = true)
    private Timestamp updated;

    public UUID getRecipientUUID() {
        return recipientUUID;
    }

    public void setRecipientUUID(UUID recipientUUID) {
        this.recipientUUID = recipientUUID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getTicketId() {
        return ticketId;
    }

    public void setTicketId(Integer ticketId) {
        this.ticketId = ticketId;
    }
}
