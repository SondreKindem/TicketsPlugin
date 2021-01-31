package no.sonkin.ticketscore.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "buffer")
public class BufferItem {
    @DatabaseField(generatedId = true)
    private int ID;

    @DatabaseField(canBeNull = false)
    private String action;

    @DatabaseField(canBeNull = false)
    private int itemID;

    public BufferItem(String action, int itemID) {
        this.action = action;
        this.itemID = itemID;
    }
}
