package com.example.androidproject.dbroom;

import android.app.Application;
import androidx.lifecycle.LiveData;

import java.util.List;

public class TripRepository {

    private TripDao tripDao;
    private LiveData<List<TripModel>> allTrips;
    private LiveData<List<TripModel>> upComingTrips;
    private LiveData<List<TripModel>> pastTrips;

   public TripRepository(Application application) {
        AppDatabase appRoomDatabase = AppDatabase.getDatabase(application);
        tripDao = appRoomDatabase.tripDao();
    }

    public LiveData<List<TripModel>> getAllTrips() {
        allTrips = tripDao.getAllTrips();
        return allTrips;
    }

    public LiveData<List<TripModel>> getAllUpcomingTrips() {
        upComingTrips = tripDao.getAllUpComingTrips();
        return upComingTrips;
    }

    public LiveData<List<TripModel>> getAllPastTrips() {
        pastTrips = tripDao.getAllPastTrips();
        return pastTrips;
    }

    public void update (TripModel tripModel) {
        new InsertThread(tripDao, tripModel).start();
    }

    public void insert (TripModel tripModel) {
        new InsertThread(tripDao, tripModel).start();
    }

    public void delete (TripModel tripModel) {
        new DeleteTripThread(tripDao, tripModel).start();
    }
    public  void deleteAll () {
        new DeleteAllTripsThread(tripDao).start();
    }
    private class UpdateThread extends Thread {

        private TripDao tripDao;
        private TripModel trip;

        UpdateThread(TripDao tripDao, TripModel trip) {
            super();
            this.tripDao = tripDao;
            this.trip = trip;
        }
        @Override
        public void run() {
            tripDao.update(trip);
        }
    }

    private class InsertThread extends Thread {

        private TripDao tripDao;
        private TripModel trip;

        InsertThread(TripDao tripDao, TripModel trip) {
            super();
            this.tripDao = tripDao;
            this.trip = trip;
        }

        @Override
        public void run() {
            tripDao.insert(trip);
        }
    }
    private static class DeleteAllTripsThread extends Thread {

        TripDao tripDao;

        public DeleteAllTripsThread(TripDao tripDao) {

            this.tripDao = tripDao;
        }

        @Override
        public void run() {
            tripDao.deleteAll();
        }
    }

    private static class DeleteTripThread extends Thread {

        TripDao tripDao;
        TripModel trip;

        public DeleteTripThread(TripDao tripDao, TripModel trip) {

            this.tripDao = tripDao;
            this.trip = trip;
        }

        @Override
        public void run() {
            tripDao.delete(trip);
        }
    }
}

