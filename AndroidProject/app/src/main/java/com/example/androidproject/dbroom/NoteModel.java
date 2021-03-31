package com.example.androidproject.dbroom;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "notes",
        foreignKeys = {
                @ForeignKey(entity = TripModel.class,
                        parentColumns = "id",
                        childColumns = "tripId",
                        onDelete = ForeignKey.CASCADE)
        }, indices = {@Index(value = {"tripId"}, unique = false)})
public class NoteModel {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo
    private int tripId;

    @ColumnInfo
    private String note;

    public NoteModel(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}
