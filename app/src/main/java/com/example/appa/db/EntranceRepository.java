package com.example.appa.db;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;

public class EntranceRepository {
    private EntranceDao entranceDao;

    public EntranceRepository(Context context) {
        PlaceDatabase database = PlaceDatabase.getInstance(context);
        entranceDao = database.entranceDao();

    }

    public List<EntranceEntity> getEntrancesFromID(int ID) {
        return entranceDao.getEntrancesFromID(ID);
    }

}
