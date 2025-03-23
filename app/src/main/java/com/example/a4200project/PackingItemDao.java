package com.example.a4200project;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface PackingItemDao {
    @Query("SELECT * FROM packing_items WHERE list_id = :listId")
    List<PackingItem> getAllForList(int listId);

    @Query("SELECT * FROM packing_items WHERE uid = :itemId")
    PackingItem getById(int itemId);

    @Insert
    void insert(PackingItem item);

    @Insert
    void insertAll(PackingItem... items);

    @Update
    void update(PackingItem item);

    @Delete
    void delete(PackingItem item);

    @Query("DELETE FROM packing_items WHERE list_id = :listId")
    void deleteAllForList(int listId);
}
