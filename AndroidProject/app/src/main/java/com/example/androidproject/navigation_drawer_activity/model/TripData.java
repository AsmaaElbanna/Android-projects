package com.example.androidproject.navigation_drawer_activity.model;

import java.util.List;

public class TripData {
    public String tripName;
    public String tripStartPoint;
    public String tripEndPoint;
    public String date;
    public String time;
    public List<String> notes;
    public TripStatus tripStatus;

    public TripData(){}

    public TripData(String tripName, String tripStartPoint, String tripEndPoint, String date, String time, TripStatus tripStatus) {
        this.tripName = tripName;
        this.tripStartPoint = tripStartPoint;
        this.tripEndPoint = tripEndPoint;
        this.date = date;
        this.time = time;
        this.tripStatus = tripStatus;
    }

    public enum TripStatus{
        oneWay,
        circular
    }
}
