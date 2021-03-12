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

import com.example.appa.db.EntranceEntity;
import com.example.appa.db.EntranceRepository;
import com.example.appa.db.PlaceEntity;

import java.util.List;

import static android.location.Location.distanceBetween;

// Viewmodel for individual locations
public class PlaceViewModel  {
    private Float distance = null;
    private PlaceEntity placeEntity;
    private EntranceEntity nearestEntrance;
    private EntranceRepository entranceRepository;
    private Context mContext;
    public PlaceViewModel(PlaceEntity placeEntity, Context context) {
        this.placeEntity = placeEntity;
        this.mContext = context;
        this.entranceRepository = new EntranceRepository(mContext.getApplicationContext());
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

    public List<EntranceEntity> getEntrancesFromID() {
        return entranceRepository.getEntrancesFromID(placeEntity.getId());
    }

    public String getDescription() {
        return placeEntity.getDescription();
    }

    private void setDistance(Location location) {
        float[] results = new float[1];
        distanceBetween(location.getLatitude(), location.getLongitude(), placeEntity.getLatitude(), placeEntity.getLongitude(), results);
        this.distance = results[0];
    }

    //what entrance to get the information from
    public void setNearestEntrance(Location location) {
        // ADD LOGIC FOR NEAREST ENTRANCE HERE
        //call distance between the two entrance and then it should return turn the distance between the two location
        //iterate between the entrance, if statement

        //for(i=0; i<=lengthoftheentrancesindatabase; i++){
        //distanceBetween()
        //if(entrance1>entrance2){
        //    nearestEntrance = entrance1; update nearest entrance everytime
        //   }
        //}


        // FOR NOW JUST RETURN THE FIRST ENTRANCE IN THE ENTRANCE LIST
        // getting the first element in the database
        nearestEntrance = entranceRepository.getEntrancesFromID(placeEntity.getId()).get(0);
    }

    public int getPlaceMajor() {
        return placeEntity.getMajor_id();
    }

    public int getNearestEntranceMinor() {
        return nearestEntrance.getMinor_id();
    }

    public Float getNearestEntranceLatitude() {
        return nearestEntrance.getLatitude();
    }

    public Float getNearestEntranceLongitude() {
        return nearestEntrance.getLongitude();
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
