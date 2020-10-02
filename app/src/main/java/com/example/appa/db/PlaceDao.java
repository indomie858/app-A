package com.example.appa.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PlaceDao {
    @Query("SELECT * FROM place_table")
    LiveData<List<PlaceEntity>> getAllPlaces();
}
