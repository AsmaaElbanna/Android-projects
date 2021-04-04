package com.example.androidproject.navigation_drawer_activity.ui.upcoming;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

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
import com.example.androidproject.navigation_drawer_activity.support.TripWorker;
import com.example.androidproject.navigation_drawer_activity.ui.map.FloatWidgetService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

public class UpcomingFragment extends Fragment implements DataTransfer , OnRecyclerViewListener{

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

    @RequiresApi(api = Build.VERSION_CODES.N)
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

            if (isFirstStartAfterLogin()) {
                SharedPreferences.Editor editor = getActivity().getSharedPreferences("start", MODE_PRIVATE).edit();
                editor.putBoolean("start", false);
                editor.commit();
                if(upcomingTrips.size()==0) {

                    NavigationActivity activity = new NavigationActivity();
                    activity.fetchDataWithFirebaseDatabase();
                    activity.onFetchData = new NavigationActivity.OnFetchData() {
                        @Override
                        public void onFetch(TripModel tripModel) {
                            Log.e(TAG + "sssasa", "onFetch: " + tripModel.getName());
                            //inset data in room
                        }

                    };
                }
            }

            if(NavigationActivity.firstTime){
                if(upcomingTrips.size() == 0){
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        Log.i(TAG, "onViewCreated: IAM HERE");
//                        fetchDataWithFirebaseDatabase();
//                    }
                }else{
                    for(TripModel trip:upcomingTrips){
                        long delay = getNewDelay(trip.getDate(),trip.getTime());
                        Log.i(TAG, "onViewCreated: DELAYYY>>"+delay);
                        if(delay>0) {
                            startWorkManager(delay, trip.getId(), trip.getName(),
                                    trip.getStartPoint(), trip.getEndPoint());
                        }else{
                            moveToHistory(trip.getId());
                        }
                    }
                }
            }
        });
        Log.i(TAG, "onViewCreated: " + getId() + "/" + R.id.nav_upcoming);
        tvDestination = view.findViewById(R.id.trip_row_endLbl);

        btnAdd = view.findViewById(R.id.upcoming_addBtn);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkOverlayDisplayPermission()) {
                    Intent i = new Intent(UpcomingFragment.this.getContext(), AddTripActivity.class);
                    startActivity(i);
                } else {
                    // If permission is not given,
                    // it shows the AlertDialog box and
                    // redirects to the Settings
                    requestOverlayDisplayPermission();
                }
            }
        });
    }

    private long getNewDelay(String dateString,String timeString){
        long difference = -1;
        Log.i(TAG, "getNewDelay: "+dateString+"//"+timeString);
        DateFormat format = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.ENGLISH);
        Date date = null;
        try {
            date = format.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            String[] times = timeString.split(":");
            int hours = Integer.parseInt(times[0]);
            int minutes = Integer.parseInt(times[1]);
            calendar.set(Calendar.HOUR_OF_DAY,hours);
            calendar.set(Calendar.MINUTE,minutes);

            Date currentDate = Calendar.getInstance().getTime();
            Date selectedDate = calendar.getTime();
            difference = (selectedDate.getTime() - currentDate.getTime())/(1000);

//            Log.i(TAG, "onViewCreated: date"+calendar.getTime() + "//time>>"+hours+":"+minutes);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return difference;
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
        WorkManager.getInstance(getContext()).cancelAllWorkByTag(new Integer(id).toString());
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

        moveToHistory(id);

    }


//    @RequiresApi(api = Build.VERSION_CODES.N)
//    private List<TripModel> fetchDataWithFirebaseDatabase() {
//        List<TripModel> tripList = new ArrayList<>();
////        Log.i(TAG, "fetchDataWithFirebaseDatabase: " + myRef.get().getResult().getValue());
//        FirebaseDatabase.getInstance().getReference().child("trips").child(FirebaseAuth.getInstance()
//                .getCurrentUser().getUid()).get().addOnCompleteListener(task -> {
//            DataSnapshot result = task.getResult();
//            Iterable<DataSnapshot> children = result.getChildren();
//            children.forEach(dataSnapshot -> {
//                TripModel value = dataSnapshot.getValue(TripModel.class);
//                upcomingTrips.add(value);
//                myAdapter.setTrips(upcomingTrips);
//                myAdapter.notifyDataSetChanged();
//
//                for(TripModel trip:upcomingTrips){
//                    long delay = getNewDelay(trip.getDate(),trip.getTime());
//                    Log.i(TAG, "fetchDataWithFirebaseDatabase: DELAY>>> "+delay);
//                    if(delay>0) {
//                        startWorkManager(delay, trip.getId(), trip.getName(),
//                                trip.getStartPoint(), trip.getEndPoint());
//                    }else{
//                        moveToHistory(trip.getId());
//                    }
//                }
//            });
//        });
//        return tripList;
//    }

    @Override
    public void saveNotes(int position) {
        Intent intent = new Intent(this.getContext(), Addnote.class);
        intent.putExtra(POSITION, position);
        intent.putExtra("tripId", upcomingTrips.get(position).getId());
        Log.i(TAG, "saveNotes: " + upcomingTrips.size());
        startActivity(intent);
    }

    private void moveToHistory(int tripId){
        Log.i(TAG, "moveToHistory: IAM HERE 1 !!! >> " + tripId);
        LiveData<List<TripModel>> tripById = mViewModel.getTripById(tripId);
        Log.i(TAG, "moveToHistory: IAM HERE 2 !!!");

        tripById.observe(getActivity(), new Observer<List<TripModel>>() {

            @Override
            public void onChanged(@Nullable final List<TripModel> tripModels) {
                Log.i(TAG, "onChanged: ID????"+tripById);
                Log.i("TAG", "onCreate: DialogMessageActivity 4");
                TripModel tripModel = tripModels.get(0);

                if (tripModel.getTripRepeatingType().equals("No_Repeat")){
                    tripModel.setStatus(1);
                    mViewModel.update(tripModel);
                }else if(tripModel.getTripRepeatingType().equals("Daily")){
                    startWorkManager(60*60*24,tripId,tripModel.getName(),
                            tripModel.getStartPoint(),tripModel.getEndPoint());
                }else if(tripModel.getTripRepeatingType().equals("Weekly")){
                    startWorkManager(60*60*24*7,tripId,tripModel.getName(),
                            tripModel.getStartPoint(),tripModel.getEndPoint());
                }else if(tripModel.getTripRepeatingType().equals("Monthly")){
                    startWorkManager(60*60*24*30,tripId,tripModel.getName(),
                            tripModel.getStartPoint(),tripModel.getEndPoint());
                }


            }
        });
    }

    private void startWorkManager(long delay , int id,String tripName,
                                  String source,String destination){

        Data.Builder data = new Data.Builder();
        data.putString("title",tripName);
        data.putString("dest",destination);
        data.putString("source",source);
        data.putInt("tripID",id);
        Log.i("TAG", "startWorkManager: >>"+id);

        WorkRequest tripRequest = new OneTimeWorkRequest.Builder(TripWorker.class)
                .setInitialDelay(delay, TimeUnit.SECONDS)
                .addTag(new Integer(id).toString())
                .setInputData(data.build())
                .build();

        WorkManager.getInstance(getContext()).enqueue(tripRequest);
    }

    public boolean isFirstStartAfterLogin(){
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("start",MODE_PRIVATE);
        boolean result=sharedPreferences.getBoolean("start",true);
        return result;
    }

    @Override
    public void onDeleteItem(int position) {
        mViewModel.delete(upcomingTrips.get(position));
    }

    private void requestOverlayDisplayPermission() {
        // An AlertDialog is created
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // This dialog can be closed, just by taping
        // anywhere outside the dialog-box
        builder.setCancelable(true);

        // The title of the Dialog-box is set
        builder.setTitle("Screen Overlay Permission Needed");

        // The message of the Dialog-box is set
        builder.setMessage("Enable 'Display over other apps' from System Settings.");

        // The event of the Positive-Button is set
        builder.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The app will redirect to the 'Display over other apps' in Settings.
                // This is an Implicit Intent. This is needed when any Action is needed
                // to perform, here it is
                // redirecting to an other app(Settings).
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getContext().getPackageName()));

                // This method will start the intent. It takes two parameter, one is the Intent and the other is
                // an requestCode Integer. Here it is -1.
                startActivityForResult(intent, Activity.RESULT_OK);
            }
        });
        AlertDialog dialog = builder.create();
        // The Dialog will
        // show in the screen
        dialog.show();
    }

    private boolean checkOverlayDisplayPermission() {
        // Android Version is lesser than Marshmallow or
        // the API is lesser than 23
        // doesn't need 'Display over other apps' permission enabling.
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            // If 'Display over other apps' is not enabled
            // it will return false or else true
            if (!Settings.canDrawOverlays(getContext())) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

}
