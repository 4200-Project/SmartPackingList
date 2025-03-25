package com.example.a4200project;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(
    entities = {PackingList.class, PackingItem.class},
    version = 2,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PackingListDao packingListDao();
    public abstract PackingItemDao packingItemDao();
}