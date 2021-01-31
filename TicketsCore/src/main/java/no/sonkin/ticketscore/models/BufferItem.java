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

    public BufferItem(){}

    public BufferItem(String action, int itemID) {
        this.action = action;
        this.itemID = itemID;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }
}
