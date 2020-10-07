package com.example.appa.db;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import com.example.appa.model.Place;

@Entity(tableName = "place_table")
public class PlaceEntity implements Place {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String description = "";
    private Float latitude = 0.0f;
    private Float longitude = 0.0f;

    // Ignore is to make Room ignore this constructor.
    // We can get rid of it when we no longer need it
    // to display our placeholders in the ViewModel.
    @Ignore
    public PlaceEntity(String name) {
        this.name = name;
    }


    public PlaceEntity(Integer id, String name, String description, Float latitude, Float longitude) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String getName() {
        return name;
    }


    // This ID setter is used by Room
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


}
