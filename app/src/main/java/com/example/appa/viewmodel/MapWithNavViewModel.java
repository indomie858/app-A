package com.example.appa.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.appa.db.PlaceEntity;
import com.example.appa.db.PlaceRepository;

public class MapWithNavViewModel extends AndroidViewModel {
    private PlaceRepository repository;
    private LiveData<PlaceEntity> currentPlace;

    public LiveData<PlaceEntity> getPlaceFromID(Integer id) {
        return repository.getPlaceFromID(id);
    }

    public MapWithNavViewModel(@NonNull Application application) {
        super(application);
        repository = new PlaceRepository(application);
        currentPlace = repository.getPlaceFromID(0);
    }
}
