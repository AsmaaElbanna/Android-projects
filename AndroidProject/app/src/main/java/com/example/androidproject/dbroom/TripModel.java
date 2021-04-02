package com.example.androidproject.dbroom;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "trips")
public class TripModel implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo
    private String name;

    @ColumnInfo
    private String email;

    @ColumnInfo
    private String userId;

    @ColumnInfo(name = "start_point")
    private String startPoint;

    @ColumnInfo(name = "end_point")
    private String endPoint;

    @ColumnInfo
    private String date;

    @ColumnInfo
    private String time;

    @ColumnInfo
    private long timestamp;

    @ColumnInfo           //upcoming or history
    private int status;

    @ColumnInfo
    private String type;    //single or round

    @ColumnInfo(name = "trip_repeating_type")
    private String tripRepeatingType;            //not repeating , daily , weekly , monthly ,.....

    @ColumnInfo(name = "start_point_latitude")
    private double startPointLatitude;

    @ColumnInfo(name = "start_point_longitude")
    private double startPointLongitude;

    @ColumnInfo(name = "end_point_latitude")
    private double endPointLatitude;

    @ColumnInfo(name = "end_point_longitude")
    private double endPointLongitude;

    public TripModel() {

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTripRepeatingType() {
        return tripRepeatingType;
    }

    public void setTripRepeatingType(String tripRepeatingType) {
        this.tripRepeatingType = tripRepeatingType;
    }

    public double getStartPointLatitude() {
        return startPointLatitude;
    }

    public void setStartPointLatitude(double startPointLatitude) {
        this.startPointLatitude = startPointLatitude;
    }

    public double getStartPointLongitude() {
        return startPointLongitude;
    }

    public void setStartPointLongitude(double startPointLongitude) {
        this.startPointLongitude = startPointLongitude;
    }

    public double getEndPointLatitude() {
        return endPointLatitude;
    }

    public void setEndPointLatitude(double endPointLatitude) {
        this.endPointLatitude = endPointLatitude;
    }

    public double getEndPointLongitude() {
        return endPointLongitude;
    }

    public void setEndPointLongitude(double endPointLongitude) {
        this.endPointLongitude = endPointLongitude;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
