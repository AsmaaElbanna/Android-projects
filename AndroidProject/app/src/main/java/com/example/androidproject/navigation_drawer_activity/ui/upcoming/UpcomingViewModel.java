package com.example.androidproject.navigation_drawer_activity.ui.upcoming;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.androidproject.dbroom.TripModel;
import com.example.androidproject.dbroom.TripRepository;

import java.util.List;

public class UpcomingViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    TripRepository tripRepository ;

    public LiveData<List<TripModel>> getUpcomingTrips(){
        return tripRepository.getAllUpcomingTrips();

    }
}