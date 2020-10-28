package com.example.appa.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


// Database table using the PlaceEntity
@Database(entities = PlaceEntity.class, version = 1, exportSchema = false)
public abstract class PlaceDatabase extends RoomDatabase {
    // Singleton: one instance of this class can exist
    private static PlaceDatabase instance;

    public abstract PlaceDao placeListDao();

    public static synchronized PlaceDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), PlaceDatabase.class, "place_database")
                    .fallbackToDestructiveMigration()
                    .createFromAsset("locations.db")
                    .build();
        }
        return instance;
    }
}
