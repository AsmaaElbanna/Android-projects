package com.example.androidproject.dbroom;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class TripViewModel extends AndroidViewModel {

    private final TripRepository tripRepository;

    public TripViewModel(Application application) {
        super(application);
        tripRepository = new TripRepository(application);
    }

    public LiveData<List<TripModel>> getAllTrips() {
        return tripRepository.getAllTrips();
    }

    public LiveData<List<TripModel>> getAllUpcomingTrips(String email) {
        return tripRepository.getAllUpcomingTrips(email);
    }

    public LiveData<List<TripModel>> getAllPastTrips() {
        return tripRepository.getAllPastTrips();
    }

    public void update(TripModel trip) {
        tripRepository.update(trip);
    }

    public void insert(TripModel trip) {
        tripRepository.insert(trip);
    }

    public void delete(TripModel trip) {
        tripRepository.delete(trip);
    }

    public void deleteAll() {
        tripRepository.deleteAll();
    }
}
