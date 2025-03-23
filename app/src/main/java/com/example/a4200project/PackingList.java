package com.example.a4200project;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "packing_lists")
public class PackingList {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "list_name")
    public String name;

    @ColumnInfo(name = "trip_type")
    public String tripType;

    @ColumnInfo(name = "destination")
    public String destination;

    @ColumnInfo(name = "duration")
    public String duration;

    public PackingList(String name, String tripType, String destination, String duration) {
        this.name = name;
        this.tripType = tripType;
        this.destination = destination;
        this.duration = duration;
    }

    // Getters and setters
    public int getUid() { return uid; }
    public void setUid(int uid) { this.uid = uid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTripType() { return tripType; }
    public void setTripType(String tripType) { this.tripType = tripType; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
} 