package com.example.androidproject.navigation_drawer_activity.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TripData implements Serializable {
    public String tripName;
    public String tripStartPoint;
    public String tripEndPoint;
    public String date;
    public String time;
    public String tripStatus;
    public String repeat;
    public ArrayList<String> notes;

    public TripData() {
        notes = new ArrayList<>();
    }
    public TripData(String tripName, String tripStartPoint, String tripEndPoint, String date, String time,
                    String tripStatus, String repeat) {
        this.tripName = tripName;
        this.tripStartPoint = tripStartPoint;
        this.tripEndPoint = tripEndPoint;
        this.date = date;
        this.time = time;
        this.tripStatus = tripStatus;
        this.repeat = repeat;
    }
}
