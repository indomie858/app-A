package com.example.appa.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;

import com.example.appa.db.PlaceEntity;
import com.example.appa.db.PlaceRepository;

import java.util.List;

public class NavigationListViewModel extends AndroidViewModel {
    private PlaceRepository repository;
    private LiveData<List<PlaceEntity>> allPlaces;
    public NavigationListViewModel(Application application, SavedStateHandle savedStateHandle) {
        super(application);
        repository = new PlaceRepository(application);
        allPlaces = repository.getAllPlaces();
    }

    public LiveData<List<PlaceEntity>> getAllPlaces() {
        return allPlaces;
    }
}
