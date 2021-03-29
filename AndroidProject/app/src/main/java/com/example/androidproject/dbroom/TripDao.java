package com.example.androidproject.dbroom;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TripDao {

    @Query("SELECT * FROM TripModel")
    List<TripModel> getAll();

    @Query("SELECT * FROM TripModel WHERE tripId = (:userIdsTrip)")
    List<TripModel> loadAllByIdsTrip(int userIdsTrip);

    @Query("SELECT * FROM TripModel WHERE user_id = (:userIdsUser)")
    List<TripModel> loadAllByIdsUser(String userIdsUser);
/*
    @Query("SELECT * FROM TripModel WHERE first_name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    TripModel findByName(String first, String last);
*/
    @Insert
    void insertAll(TripModel... trips);   //... refer to args method 0 or more

    @Delete
    void delete(TripModel trip);
}
