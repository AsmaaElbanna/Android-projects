package com.example.androidproject.navigation_drawer_activity.ui.upcoming;

import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidproject.AddTripActivity;
import com.example.androidproject.Addnote;
import com.example.androidproject.R;
import com.example.androidproject.navigation_drawer_activity.NavigationActivity;
import com.example.androidproject.navigation_drawer_activity.model.TripData;
import com.example.androidproject.navigation_drawer_activity.support.DataTransfer;
import com.example.androidproject.navigation_drawer_activity.support.MyAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class UpcomingFragment extends Fragment implements DataTransfer {

    private UpcomingViewModel mViewModel;
    private final String TAG = "tag";
    private ArrayList<TripData> upcomingTrips;
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    public static int id;
    TextView tvDestination;
    FloatingActionButton btnAdd;
    private final int REQUEST_CODE = 2;
    public static final String POSITION = "position";
    public static final String BUNDLE_NAME = "Data";

    public static UpcomingFragment newInstance() {
        return new UpcomingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        id = getId();
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
                "Alex", "Cairo",
                "13/06/2021", "5:45 PM", "oneWay", "noRepeat"));
        upcomingTrips.add(new TripData("return trip",
                "Cairo", "Alex",
                "20/06/2021", "9:45 PM", "oneWay", "noRepeat"));

        Log.i(TAG, "onViewCreated: " + getId() + "/" + R.id.nav_upcoming);

        myAdapter = new MyAdapter(this.getContext(), upcomingTrips, this, MyAdapter.Status.UPCOMING);
        recyclerView.setAdapter(myAdapter);
        tvDestination = view.findViewById(R.id.trip_row_endLbl);

        btnAdd = view.findViewById(R.id.upcoming_addBtn);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int LAUNCH_SECOND_ACTIVITY = 1;
                Intent i = new Intent(UpcomingFragment.this.getContext(), AddTripActivity.class);
                startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);
            }
        });


    }

    private void DisplayMap(String destination) {
        try {
            Uri uri = Uri.parse("https://www.google.co.in/maps/dir//" + destination);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Uri uri = Uri.parse("http://play.google.com/store/apps/details?id=com.google.android.apps.maps");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }


    @Override
    public void startMap(String dest) {
        DisplayMap(dest);
    }

    public void addTrip(TripData data) {
        upcomingTrips.add(data);
        myAdapter.notifyItemInserted(myAdapter.getItemCount());
    }


    @Override
    public void saveNotes(int position) {
        Intent intent = new Intent(this.getContext(), Addnote.class);
        intent.putExtra(POSITION,position);
        intent.putExtra("NOTES",upcomingTrips.get(position).notes);
        startActivityForResult(intent,REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                TripData result = (TripData) data.getSerializableExtra("result");
                Toast.makeText(this.getContext(), "done", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onActivityResult: done" + result.tripName);
                upcomingTrips.add(result);
                myAdapter.notifyItemInserted(upcomingTrips.size());
//                upcomingFragment.addTrip(result);

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
}