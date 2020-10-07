package com.example.appa.db;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.appa.model.Place;

import java.util.List;

public class PlaceRepository {
    private PlaceDao placeDao;
    private LiveData<List<PlaceEntity>> allPlaces;
    private LiveData<PlaceEntity> singlePlace;
    public PlaceRepository(Application application) {
        PlaceDatabase database = PlaceDatabase.getInstance(application);
        placeDao = database.placeListDao();
        allPlaces = placeDao.getAllPlaces();
    }

    public LiveData<List<PlaceEntity>> getAllPlaces() {
        return allPlaces;
    }
    public LiveData<PlaceEntity> getPlaceFromID(int id) { return placeDao.getPlaceFromID(id);}

}
