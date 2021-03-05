package com.example.appa.db;


import androidx.room.Entity;

import com.example.appa.model.Entrance;

@Entity(tableName = "entrance_locations")
public class EntranceEntity implements Entrance {
    private int place_id;
    private Float latitude = 0.0f;
    private Float longitude = 0.0f;

    //matches database fields
    public EntranceEntity(Integer place_id,
                          Float latitude,
                          Float longitude
    ) {
        this.place_id = place_id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    //getters and setters for data fields
    @Override
    public void setId(int place_id) {
        this.place_id = place_id;
    }

    public int getId() {
        return place_id;
    }

    public Float getLatitude() {
        return latitude;
    }

    public Float getLongitude() {
        return longitude;
    }
}

