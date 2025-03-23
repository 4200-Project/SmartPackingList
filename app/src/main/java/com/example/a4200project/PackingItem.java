package com.example.a4200project;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity(
    tableName = "packing_items",
    foreignKeys = @ForeignKey(
        entity = PackingList.class,
        parentColumns = "uid",
        childColumns = "list_id",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index("list_id")}
)
public class PackingItem {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "list_id")
    public int listId;

    @ColumnInfo(name = "item_name")
    public String name;

    @ColumnInfo(name = "is_checked")
    public boolean checked;

    @ColumnInfo(name = "trip_type")
    private String tripType;

    @ColumnInfo(name = "destination")
    private String destination;

    @ColumnInfo(name = "duration")
    private String duration;

    @Ignore
    public PackingItem(int listId, String name, boolean checked) {
        this.listId = listId;
        this.name = name;
        this.checked = checked;
        this.tripType = "";
        this.destination = "";
        this.duration = "";
    }

    public PackingItem(int listId, String name, boolean checked, String tripType, String destination, String duration) {
        this.listId = listId;
        this.name = name;
        this.checked = checked;
        this.tripType = tripType;
        this.destination = destination;
        this.duration = duration;
    }

    public int getUid() { return uid; }
    public void setUid(int uid) { this.uid = uid; }

    public int getListId() { return listId; }
    public void setListId(int listId) { this.listId = listId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isChecked() { return checked; }
    public void setChecked(boolean checked) { this.checked = checked; }

    public String getTripType() { return tripType; }
    public void setTripType(String tripType) { this.tripType = tripType; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
}