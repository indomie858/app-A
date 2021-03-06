package com.example.appa.db;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class PlaceRepository {
    // This repository serves
    // as an abstraction layer
    // between the ViewModel and the DAO.
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
    public LiveData<List<PlaceEntity>> getPlacesFromString(String searchName) { return placeDao.getPlacesFromString(searchName); }

    public LiveData<List<PlaceEntity>> searchQuery(String searchName, String searchCategory) {
        return placeDao.searchQuery(searchName, searchCategory);
    }
}
