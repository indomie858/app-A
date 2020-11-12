package com.example.appa.db;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.appa.model.Place;

import java.lang.reflect.Array;

import static android.location.Location.distanceBetween;

@Entity(tableName = "place_table")
public class PlaceEntity implements Place {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String description = "";
    private Float latitude = 0.0f;
    private Float longitude = 0.0f;
    private String categories = "";
    private Integer major_id;
    private Integer minor_id;

    // Fields should match database fields.
    public PlaceEntity(Integer id,
                       String name,
                       String description,
                       Float latitude,
                       Float longitude,
                       String categories,
                       Integer major_id,
                       Integer minor_id
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.categories =  categories;
        this.major_id = major_id;
        this.minor_id = minor_id;
    }


    // Getters for data fields.
    @Override
    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Float getLatitude() {
        return latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public String getCategories() { return categories; }

    public Integer getMajor_id() { return major_id; }

    public Integer getMinor_id() { return minor_id; }
}
