package com.example.androidproject.navigation_drawer_activity.ui.upcoming;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.androidproject.R;
import com.example.androidproject.navigation_drawer_activity.model.TripData;
import com.example.androidproject.navigation_drawer_activity.support.MyAdapter;

import java.util.ArrayList;

public class UpcomingFragment extends Fragment {

    private UpcomingViewModel mViewModel;

    private final String TAG = "tag";

    private ArrayList<TripData> upcomingTrips;
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;

    public static UpcomingFragment newInstance() {
        return new UpcomingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.upcoming_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(UpcomingViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.upcoming_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        upcomingTrips = new ArrayList<>();
        upcomingTrips.add(new TripData("first trip",
                "Alexandria","Cairo",
                "13/06/2021","5:45 PM",TripData.TripStatus.oneWay));
        upcomingTrips.add(new TripData("return trip",
                "Cairo","Alexandria",
                "20/06/2021","9:45 PM",TripData.TripStatus.oneWay));

        myAdapter = new MyAdapter(this.getContext(),upcomingTrips);
        recyclerView.setAdapter(myAdapter);
    }

}