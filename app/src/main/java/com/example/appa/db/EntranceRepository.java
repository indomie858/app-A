package com.example.appa.db;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class EntranceRepository {
    private EntranceDao entranceDao;
    //public List<LiveData> getPlacesFromID(int id) {
    //}


    public EntranceRepository(Application application) {
        PlaceDatabase database = PlaceDatabase.getInstance(application);
        entranceDao = database.entranceDao();

    }

    public LiveData<List<EntranceEntity>> getEntrancesFromID(int ID) {
        return entranceDao.getEntrancesFromID(ID);
    }

}
