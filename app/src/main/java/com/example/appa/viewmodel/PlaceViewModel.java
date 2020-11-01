package com.example.appa.viewmodel;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.example.appa.db.PlaceEntity;

import static android.location.Location.distanceBetween;

public class PlaceViewModel {
    private float distance = 0.0f;
    private PlaceEntity placeEntity;

    public PlaceViewModel(PlaceEntity placeEntity) {
        this.placeEntity = placeEntity;
    }

    private LocationManager manager;

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            float[] results = new float[1];
            distanceBetween(location.getLatitude(), location.getLongitude(), placeEntity.getLatitude(), placeEntity.getLongitude(), results);
            setDistance(results[0]);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
        @Override
        public void onProviderEnabled(String provider) {

        }
        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public int getId() {
        return placeEntity.getId();
    }

    public float getDistance() {
        return distance;
    }

    public String getDistanceString() {
        return Float.valueOf(distance).toString();
    }

    public String getName() {
        return placeEntity.getName();
    }

    public String getDescription() {
        return placeEntity.getDescription();
    }
    private void setDistance(float distance) {
        this.distance = distance;
    }

    public void setLocationManager(LocationManager manager) {
        this.manager = manager;
        this.manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, mLocationListener);
    }

}
