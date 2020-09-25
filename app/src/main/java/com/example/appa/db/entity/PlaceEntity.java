package com.example.appa.db.entity;

import com.example.appa.model.Place;


// This is where we should implement getters
// and setters for our database items
public class PlaceEntity implements Place {
    private String name = "Jacaranda Hall";

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
