package no.sonkin.ticketscore.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.jsoniter.annotation.JsonIgnore;
import com.jsoniter.annotation.JsonUnwrapper;
import com.jsoniter.output.JsonStream;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.UUID;

@DatabaseTable(tableName = "comments")
public class Comment {
    @JsonIgnore
    @DatabaseField(generatedId = true)
    private Integer ID;

    @DatabaseField(canBeNull = false)
    private String message;

    @JsonIgnore
    @DatabaseField
    private UUID playerUUID;

    @DatabaseField(canBeNull = false)
    private String playerName;

    @JsonIgnore
    @DatabaseField(version = true)
    private Timestamp created;

    @JsonIgnore
    @DatabaseField(foreign = true, canBeNull = false)
    private Ticket ticket;

    @JsonUnwrapper
    public void writeStuff(JsonStream stream) throws IOException {
        stream.writeObjectField("commentID");
        stream.writeVal(ID);
        stream.writeMore();
        stream.writeObjectField("created");
        stream.writeVal(created.toString());
        stream.writeMore();
        stream.writeObjectField("playerUUID");
        stream.writeVal(playerUUID.toString());
    }

    // Helper field for sending tickets to discord
    private String discordChannel;

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

    public void setDiscordChannel(String discordChannel) {
        this.discordChannel = discordChannel;
    }

    public String getDiscordChannel() {
        return this.discordChannel;
    }
}
