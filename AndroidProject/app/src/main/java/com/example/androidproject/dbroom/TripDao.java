package com.example.androidproject.dbroom;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TripDao {

    @Query("SELECT * FROM trips")
    LiveData<List<TripModel>> getAllTrips();
/*
    @Query("SELECT * FROM trips where status == 0  order by timestamp asc")
    LiveData<List<TripModel>> getAllUpComingTrips();
*/

    @Query("SELECT * FROM trips where status = 0  and email = :useremail order by timestamp asc ")
    LiveData<List<TripModel>> getAllUpComingTrips(String useremail);

    @Query("SELECT * FROM trips where status != 0 order by timestamp asc")
    LiveData<List<TripModel>> getAllPastTrips();

    @Update
    void update(TripModel tripModel);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insert(TripModel... tripModels);

    @Delete
    void delete(TripModel tripModel);

    @Query("DELETE FROM trips")
    void deleteAll();
}
