package com.example.appa.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.appa.db.PlaceEntity;

import static android.location.Location.distanceBetween;

// Viewmodel for individual locations
public class PlaceViewModel  {
    private Float distance = null;
    private PlaceEntity placeEntity;
    public PlaceViewModel(PlaceEntity placeEntity) {
        this.placeEntity = placeEntity;
    }

    public int getId() {
        return placeEntity.getId();
    }

    public Float getDistance() {
        return distance;
    }

    public String getDistanceFeet() {
        int distanceToFeet = (int) Math.round(distance * 3.28084);
        return Integer.valueOf(distanceToFeet).toString();
    }

    public String getPhoneNumber() { return placeEntity.getPhone_number(); }

    public String getName() {
        return placeEntity.getName();
    }

    public String getDescription() {
        return placeEntity.getDescription();
    }

    private void setDistance(Location location) {
        float[] results = new float[1];
        distanceBetween(location.getLatitude(), location.getLongitude(), placeEntity.getLatitude(), placeEntity.getLongitude(), results);
        this.distance = results[0];
    }

    @SuppressLint("MissingPermission")
    public void setLocationAndDistance(Location location) {
        // Set initial distance with a given location.
        if (location != null) {
            setDistance(location);
        } else {
            distance = null;
        }
    }
}
