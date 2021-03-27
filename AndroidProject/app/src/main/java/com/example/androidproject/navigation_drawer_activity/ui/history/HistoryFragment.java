package com.example.androidproject.navigation_drawer_activity.ui.history;

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
import com.example.androidproject.navigation_drawer_activity.support.DataTransfer;
import com.example.androidproject.navigation_drawer_activity.support.MyAdapter;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {

    private HistoryViewModel mViewModel;

    private ArrayList<TripData> historyTrips;
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.history_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.history_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        historyTrips = new ArrayList<>();
        historyTrips.add(new TripData("first trip",
                "Alex", "Cairo",
                "13/06/2021", "5:45 PM", "no_Repeat", "oneWay"));
        historyTrips.add(new TripData("return trip",
                "Cairo", "Alex",
                "20/06/2021", "9:45 PM", "no_Repeat", "oneWay"));

        myAdapter = new MyAdapter(this.getContext(), historyTrips,null, MyAdapter.Status.HISTORY);
        recyclerView.setAdapter(myAdapter);
    }
}