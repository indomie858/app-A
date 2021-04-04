package com.example.appa.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.appa.db.EntranceEntity;
import com.example.appa.db.EntranceRepository;
import com.example.appa.db.PlaceEntity;
import com.example.appa.ui.MainActivity;

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
    @SuppressLint("MissingPermission")
    public void setNearestEntrance(Location location) {
        float[] distanceBetweenResults;
        float minimumDistance ;
        int nearestEntranceIndex;
        int entranceListAmount;

        //This check is necessary to prevent crashing due to null exception when clicking a category
        if (location == null) {
            LocationManager locationManager = (LocationManager)  mContext.getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true));
            location = locationManager.getLastKnownLocation(bestProvider);
        }

        // Gets the list of available entrances
        List<EntranceEntity> totalEntrancesAvailable = entranceRepository.getEntrancesFromID(placeEntity.getId());

        // Gets the size of the list
        entranceListAmount = totalEntrancesAvailable.size();

        // Sets the size of the float array
        distanceBetweenResults = new float[entranceListAmount];

        // Calculates the first location
        distanceBetween(location.getLatitude(),location.getLongitude(),
                entranceRepository.getEntrancesFromID(placeEntity.getId()).get(0).getLatitude(),
                entranceRepository.getEntrancesFromID(placeEntity.getId()).get(0).getLongitude(),
                distanceBetweenResults);

        //Sets the first minimum distance
        minimumDistance = distanceBetweenResults[0];
        nearestEntranceIndex = 0;

        // Checks the nearest entrance
        for (int i = 1; i < entranceListAmount; i++){

           distanceBetween(location.getLatitude(),location.getLongitude(),
                   entranceRepository.getEntrancesFromID(placeEntity.getId()).get(i).getLatitude(),
                   entranceRepository.getEntrancesFromID(placeEntity.getId()).get(i).getLongitude(),
                   distanceBetweenResults);

            // Updates minimum distance and keeps track of index location
            if (distanceBetweenResults[0]< minimumDistance){
                minimumDistance = distanceBetweenResults[0];
                nearestEntranceIndex = i;

            }
        }

        nearestEntrance = entranceRepository.getEntrancesFromID(placeEntity.getId()).get(nearestEntranceIndex);



        // Chooses which entrance is the closest

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
