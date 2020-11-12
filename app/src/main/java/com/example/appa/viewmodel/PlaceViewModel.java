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
    private float distance = 0.0f;
    private PlaceEntity placeEntity;
    public PlaceViewModel(PlaceEntity placeEntity) {
        this.placeEntity = placeEntity;
    }

    public LocationManager manager;

    public int getId() {
        return placeEntity.getId();
    }

    public float getDistance() {
        return distance;
    }

    public String getDistanceString() {
        int distanceToFeet = (int) Math.round(distance * 3.28084);
        return Integer.valueOf(distanceToFeet).toString() + " feet";
    }

    public String getCallString() { return "Call "+ placeEntity.getName(); }
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
    public void setLocationAndDistance(LocationManager manager) {
        // Set a location manager and initial distance.
        // We need to receive an instance of a locationmanager,
        // because we need to initialize it with context from a view
        this.manager = manager;
        Location currentLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        setDistance(currentLocation);
    }
}
