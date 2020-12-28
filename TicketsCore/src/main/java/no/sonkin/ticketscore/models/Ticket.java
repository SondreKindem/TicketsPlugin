package no.sonkin.ticketscore.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Timestamp;
import java.util.UUID;

@DatabaseTable(tableName = "tickets")
public class Ticket {
    @DatabaseField(id = true)
    private int ID;
    @DatabaseField
    private String description;
    @DatabaseField
    private UUID playerUUID;
    @DatabaseField
    private String playerName;
    @DatabaseField
    private String server;
    @DatabaseField
    private int x;
    @DatabaseField
    private int z;
    @DatabaseField
    private int y;
    @DatabaseField
    private String world;
    @DatabaseField
    private Timestamp created;
    @DatabaseField
    private Timestamp updated;

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

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public Timestamp getUpdated() {
        return updated;
    }

    public void setUpdated(Timestamp updated) {
        this.updated = updated;
    }
}
