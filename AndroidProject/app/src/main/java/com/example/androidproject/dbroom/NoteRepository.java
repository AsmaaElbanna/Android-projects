package com.example.androidproject.dbroom;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;

public class NoteRepository {

    private final NoteDao noteDao;

   public NoteRepository(Application application) {
        AppDatabase appRoomDatabase = AppDatabase.getDatabase(application);
        noteDao = appRoomDatabase.noteDao();
    }

    public LiveData<List<NoteModel>> getAllNotes() {
        LiveData<List<NoteModel>> notes = noteDao.getAllNotes();
        return notes;
    }

    public LiveData<List<NoteModel>> getAllNotesById(int tripId) {
        LiveData<List<NoteModel>> notes = noteDao.getAllNotesById(tripId);
        return notes;
    }

    public void update (NoteModel noteModel) {
        new NoteRepository.InsertThread(noteDao, noteModel).start();
    }

    public void insert (NoteModel noteModel) {
        new NoteRepository.InsertThread(noteDao, noteModel).start();
    }

    public void delete (NoteModel noteModel) {
        new NoteRepository.DeleteNoteThread(noteDao, noteModel).start();
    }

    public  void deleteAll () {
        new NoteRepository.DeleteAllNotesThread(noteDao).start();
    }

    private class UpdateThread extends Thread {

        NoteDao noteDao;
        NoteModel note;

        public UpdateThread(NoteDao noteDao, NoteModel note) {

            this.noteDao = noteDao;
            this.note = note;
        }

        @Override
        public void run() {
            noteDao.update(note);
        }
    }

    private class InsertThread extends Thread {

        NoteDao noteDao;
        NoteModel note;

        public InsertThread(NoteDao noteDao, NoteModel note) {

            this.noteDao = noteDao;
            this.note = note;
        }

        @Override
        public void run() {
            noteDao.insert(note);
        }
    }
    private static class DeleteAllNotesThread extends Thread {
        NoteDao noteDao;
        public DeleteAllNotesThread(NoteDao noteDao) {

            this.noteDao = noteDao;
        }

        @Override
        public void run() {
            noteDao.deleteAll();
        }
    }

    private static class DeleteNoteThread extends Thread {

        NoteDao noteDao;
        NoteModel note;

        public DeleteNoteThread(NoteDao noteDao, NoteModel note) {

            this.noteDao = noteDao;
            this.note = note;
        }

        @Override
        public void run() {
            noteDao.delete(note);
        }
    }
}
