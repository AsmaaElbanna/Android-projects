package com.example.androidproject.dbroom;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
@Dao
public interface NoteDao {

    @Query("SELECT * FROM notes")
    LiveData<List<NoteModel>> getAllNotes();

    @Query("SELECT * FROM notes WHERE tripId IN (:id)")
    LiveData<List<NoteModel>> getAllNotesById(int... id);

    @Update
    void update(NoteModel noteModel);

    @Insert
    void insert(NoteModel... noteModels);

    @Delete
    void delete(NoteModel noteModel);

    @Query("DELETE FROM notes")
    void deleteAll();



}
