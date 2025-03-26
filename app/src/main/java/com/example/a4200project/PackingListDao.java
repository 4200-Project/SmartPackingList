package com.example.a4200project;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface PackingListDao {
    @Query("SELECT * FROM packing_lists")
    List<PackingList> getAll();

    @Query("SELECT * FROM packing_lists WHERE uid = :listId")
    PackingList getById(int listId);

    @Insert
    long insert(PackingList list);

    @Update
    void update(PackingList list);

    @Delete
    void delete(PackingList list);
} 