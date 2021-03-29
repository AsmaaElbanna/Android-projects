package com.example.androidproject.dbroom;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {TripModel.class}, version = 1)
public abstract class  AppDatabase extends RoomDatabase {
    public abstract TripDao tripDao();

}

