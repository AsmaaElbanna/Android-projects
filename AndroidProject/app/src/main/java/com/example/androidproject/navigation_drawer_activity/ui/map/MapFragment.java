package com.example.androidproject.navigation_drawer_activity.ui.map;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.androidproject.R;
import com.example.androidproject.dbroom.AppDatabase;
import com.example.androidproject.dbroom.TripModel;
import com.example.androidproject.dbroom.TripViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MapFragment extends Fragment {

    SupportMapFragment mapFragment;
    Marker markerPerth;
    private LatLng location;
    private static final int COLOR_BLACK_ARGB = Color.RED;
    private static final int POLYLINE_STROKE_WIDTH_PX = 12;
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);
   // NewTrip newTrip;
    private AppDatabase database;
    TripViewModel tripViewModel;

    //private LatLng[] latLngs;
    private GoogleMap mMap;
    PolylineOptions polylineOptions ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = AppDatabase.getDatabase(getContext());
        polylineOptions =new PolylineOptions();
        // ......doing
         // tripHistoryList = database.tripDao().getAllP

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.map_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        tripViewModel = new ViewModelProvider(getActivity()).get(TripViewModel.class);
       // tripHistoryList= tripViewModel.getAllPastTrips();
        tripViewModel.getAllPastTrips().observe(getActivity(), new Observer<List<TripModel>>() {
            @Override
            public void onChanged(@Nullable final List<TripModel> tripModels) {
                // Update the cached copy of the words in the adapter.
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        mMap = googleMap;

                        for(int i =0 ; i <tripModels.size();i++) {
                            //trips = new ArrayList<>();

                            Random rnd = new Random();
                            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                            Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                                    .clickable(true)
                                    .add(
                                            new LatLng(tripModels.get(i).getStartPointLatitude(), tripModels.get(i).getStartPointLongitude()),
                                            new LatLng(tripModels.get(i).getEndPointLatitude(), tripModels.get(i).getEndPointLongitude())

                                    ).color(color));
                            polyline1.setTag("A");
                        }
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(30.033333,31.233334),4
                        ));
                    }
                });
            }
        });


        // location = new LatLng(31, 29);


    }

    private void stylePolyline(Polyline polyline) {
        String type = "";
        // Get the data object stored with the polyline.
        if (polyline.getTag() != null) {
            type = polyline.getTag().toString();
        }


        polyline.setEndCap(new RoundCap());
        polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
        polyline.setColor(COLOR_BLACK_ARGB);
        polyline.setJointType(JointType.ROUND);
    }

}