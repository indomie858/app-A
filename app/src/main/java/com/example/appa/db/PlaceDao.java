package com.example.appa.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.example.appa.model.Place;

import java.util.List;

@Dao
public interface PlaceDao {
    // Get all places
    @Query("SELECT * FROM place_table")
    LiveData<List<PlaceEntity>> getAllPlaces();

    // Specific place from an ID
    @Query("SELECT * FROM place_table WHERE id=:id")
    LiveData<PlaceEntity> getPlaceFromID(Integer id);

    // Search for places,
    // that start with a given string.
    // Useful for returning results
    // as a user fills in a field.
    @Query("SELECT * FROM place_table WHERE name LIKE '%' || :searchName || '%'")
    LiveData<List<PlaceEntity>> getPlacesFromString(String searchName);
}
