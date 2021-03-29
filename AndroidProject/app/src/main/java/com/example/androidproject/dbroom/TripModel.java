package com.example.androidproject.dbroom;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TripModel {

    @PrimaryKey(autoGenerate = true)
    public int tripId;

    @ColumnInfo(name = "trip_name")
    public String tripName;

    @ColumnInfo(name = "start_point")
    public String startPoint;

    @ColumnInfo(name = "end_point")
    public String endPoint;

    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "time")
    public String time;

    @ColumnInfo(name = "user_id")
    public String userId;

}
