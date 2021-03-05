package com.example.appa.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.example.appa.model.Entrance;

import java.util.List;

@Dao
public interface EntranceDao {
    @Query("SELECT*FROM entrance_locations")
    LiveData<List<EntranceEntity>> getAllEntrance();

    @Query("SELECT * FROM entrance_locations WHERE place_id = :placeID")
    LiveData<List<EntranceEntity>> getEntrancesFromID(int placeID);

}
