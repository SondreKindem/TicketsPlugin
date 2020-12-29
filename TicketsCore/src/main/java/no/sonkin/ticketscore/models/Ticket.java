package no.sonkin.ticketscore.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;
import java.sql.Timestamp;
import java.util.UUID;

@DatabaseTable(tableName = "tickets")
public class Ticket {
    @DatabaseField(id = true)
    private Integer ID;
    @DatabaseField(canBeNull = false)
    private String description;
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
    @DatabaseField(canBeNull = false)
    private Timestamp created;
    @DatabaseField(version = true)
    private Timestamp updated;
    @DatabaseField(canBeNull = false, defaultValue = "false")
    private boolean closed;
    @DatabaseField
    private String closedBy;

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

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getY() {
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
}
