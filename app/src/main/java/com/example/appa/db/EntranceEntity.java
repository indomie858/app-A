package com.example.appa.db;


import com.example.appa.model.Entrance;

public class EntranceEntity implements Entrance {
    private int id;
    private Float latitude = 0.0f;
    private Float longitude = 0.0f;

    //matches database fields
    public EntranceEntity(Integer id,
                          Float latitude,
                          Float longitude
    ) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    //getters and setters for data fields
    public int setId(){
        return this.id = id;
    }

    @Override
    public void setId(int id) {

    }

    public int getId() {
        return id;
    }

    public Float getLatitude() {
        return latitude;
    }

    public Float getLongitude() {
        return longitude;
    }
}

