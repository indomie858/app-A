package com.example.appa;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "destination.db";  //file name
    public static final String TABLE_NAME = "destination_table"; //table name

    //Table Columns
    public static final String ID = "ID";
    public static final String CATEGORY = "CATEGORY";
    public static final String NAME = "NAME";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String LONGITUDE = "LONGITUDE";
    public static final String LATITUDE = "LATITUDE";

    //Whenever this is called, it creates the database
    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Executes the query
        db.execSQL(
                "create table " + TABLE_NAME + " (" +
                        "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "NAME TEXT, " +
                        "DESCRIPTION TEXT, " +
                        "LONGITUDE FLOAT," +
                        "LATITUDE FLOAT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
