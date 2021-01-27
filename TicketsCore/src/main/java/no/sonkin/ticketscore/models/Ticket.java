package no.sonkin.ticketscore.models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.jsoniter.annotation.JsonIgnore;
import com.jsoniter.annotation.JsonUnwrapper;
import com.jsoniter.output.JsonStream;

import java.io.IOException;
import java.util.Date;
import java.sql.Timestamp;
import java.util.UUID;

@DatabaseTable(tableName = "tickets")
public class Ticket {
    @JsonIgnore
    @DatabaseField(generatedId = true)
    private Integer ID;
    @DatabaseField(canBeNull = false)
    private String description;
    @JsonIgnore
    @DatabaseField(canBeNull = false)
    private UUID playerUUID;
    @DatabaseField(canBeNull = false)
    private String playerName;
    @DatabaseField
    private String server;
    @DatabaseField
    private Integer x;
    @DatabaseField
    private Integer z;
    @DatabaseField
    private Integer y;
    @DatabaseField
    private String world;
    @JsonIgnore
    @DatabaseField(canBeNull = false)
    private Timestamp created;
    @JsonIgnore
    @DatabaseField(version = true)
    private Timestamp updated;
    @DatabaseField(canBeNull = false, defaultValue = "false")
    private boolean closed;
    @DatabaseField
    private String closedBy;
    @DatabaseField
    private String discordChannel;
    @DatabaseField
    private String discordUser;

    @JsonIgnore
    @ForeignCollectionField
    ForeignCollection<Comment> comments;

    @JsonUnwrapper
    public void writeStuff(JsonStream stream) throws IOException {
        stream.writeObjectField("ticketId");
        stream.writeVal(ID);
        stream.writeMore();
        stream.writeObjectField("created");
        stream.writeVal(created.toString());
        stream.writeMore();
        stream.writeObjectField("updated");
        stream.writeVal(updated.toString());
        stream.writeMore();
        stream.writeObjectField("playerUUID");
        stream.writeVal(playerUUID.toString());
    }


    public String toJson() {
        return JsonStream.serialize(this);
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonIgnore
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

    public String getServerName() {
        return server;
    }

    public void setServerName(String serverName) {
        this.server = serverName;
    }

    public Integer getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public Integer getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public Integer getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    @JsonIgnore
    public Date getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public boolean isClosed() {
        return closed;
    }

    public void close() {
        this.closed = true;
    }

    public void open() {
        this.closed = false;
    }

    public String getClosedBy() {
        return closedBy;
    }

    public void setClosedBy(String closedBy) {
        this.closedBy = closedBy;
    }

    public String getDiscordChannel() {
        return discordChannel;
    }

    public void setDiscordChannel(String discordChannel) {
        this.discordChannel = discordChannel;
    }

    public ForeignCollection<Comment> getComments() {
        return comments;
    }
}
