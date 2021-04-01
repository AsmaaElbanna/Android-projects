package com.example.androidproject.navigation_drawer_activity.ui.upcoming;

import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidproject.AddTripActivity;
import com.example.androidproject.Addnote;
import com.example.androidproject.NoteAdapter;
import com.example.androidproject.R;
import com.example.androidproject.dbroom.AppDatabase;
import com.example.androidproject.dbroom.NoteModel;
import com.example.androidproject.dbroom.NoteViewModel;
import com.example.androidproject.dbroom.TripModel;
import com.example.androidproject.dbroom.TripRepository;
import com.example.androidproject.dbroom.TripViewModel;
import com.example.androidproject.navigation_drawer_activity.NavigationActivity;
import com.example.androidproject.navigation_drawer_activity.model.TripData;
import com.example.androidproject.navigation_drawer_activity.support.DataTransfer;
import com.example.androidproject.navigation_drawer_activity.support.MyAdapter;
import com.example.androidproject.navigation_drawer_activity.support.OnRecyclerViewListener;
import com.example.androidproject.navigation_drawer_activity.ui.map.FloatWidgetService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class UpcomingFragment extends Fragment implements DataTransfer , OnRecyclerViewListener {

    private TripViewModel mViewModel;
    private final String TAG = "tag";
    private List<TripModel> upcomingTrips;
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
        upcomingTrips = new ArrayList<>();
        return inflater.inflate(R.layout.upcoming_fragment, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //room


        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.upcoming_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);

        myAdapter = new MyAdapter(this.getContext(), null, this, MyAdapter.Status.UPCOMING ,this);
        recyclerView.setAdapter(myAdapter);
        //room
        mViewModel = new ViewModelProvider(getActivity()).get(TripViewModel.class);
        String email= FirebaseAuth.getInstance().getCurrentUser().getEmail();
        mViewModel.getAllUpcomingTrips(email).observe(getViewLifecycleOwner(), tripModels -> {
            myAdapter.setTrips(tripModels);

            // 31-3
            upcomingTrips = tripModels;
        });
        Log.i(TAG, "onViewCreated: " + getId() + "/" + R.id.nav_upcoming);
        tvDestination = view.findViewById(R.id.trip_row_endLbl);

        btnAdd = view.findViewById(R.id.upcoming_addBtn);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int LAUNCH_SECOND_ACTIVITY = 1;
                Intent i = new Intent(UpcomingFragment.this.getContext(), AddTripActivity.class);
                // startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);
                startActivity(i);
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
    public void startMap(String dest,int id) {
        DisplayMap(dest);
        NoteModel noteModel = new NoteModel();
        NoteViewModel noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
//          noteRepository = new NoteRepository(this);

// room

        noteViewModel.getAllNotesById(id).observe(this, noteModels -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getActivity().getPackageName()));
                startActivityForResult(intent, 106);
            } else {
                Intent startIntent = new Intent(getContext(), FloatWidgetService.class);

                startIntent.putExtra("notes", (ArrayList<NoteModel>) noteModels);
                getActivity().startService(startIntent);
            }

        });

    }



    //    public void addTrip(TripData data) {
//        upcomingTrips.add(data);
//        myAdapter.notifyItemInserted(myAdapter.getItemCount());
//    }
    @Override
    public void saveNotes(int position) {
        Intent intent = new Intent(this.getContext(), Addnote.class);
        intent.putExtra(POSITION, position);
        intent.putExtra("tripId", upcomingTrips.get(position).getId());
        Log.i(TAG, "saveNotes: " + upcomingTrips.size());
        startActivity(intent);
    }

    @Override
    public void onDeleteItem(int position) {
        mViewModel.delete(upcomingTrips.get(position));
    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1) {
//            if (resultCode == Activity.RESULT_OK) {
//                TripData result = (TripData) data.getSerializableExtra("result");
//                Toast.makeText(this.getContext(), "done", Toast.LENGTH_SHORT).show();
//                Log.i(TAG, "onActivityResult: done" + result.tripName);
//                upcomingTrips.add(result);
//                myAdapter.notifyItemInserted(upcomingTrips.size());
////                upcomingFragment.addTrip(result);
//            }
//            if (resultCode == Activity.RESULT_CANCELED) {
//                //Write your code if there's no result
//            }
//        }
//    }

}
