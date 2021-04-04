package com.example.androidproject.navigation_drawer_activity;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.androidproject.AddTripActivity;
import com.example.androidproject.R;
import com.example.androidproject.dbroom.AppDatabase;
import com.example.androidproject.dbroom.NoteModel;
import com.example.androidproject.dbroom.NoteViewModel;
import com.example.androidproject.dbroom.TripDao;
import com.example.androidproject.dbroom.TripModel;
import com.example.androidproject.dbroom.TripViewModel;
import com.example.androidproject.navigation_drawer_activity.model.TripData;
import com.example.androidproject.navigation_drawer_activity.support.TripWorker;
import com.example.androidproject.navigation_drawer_activity.ui.map.FloatWidgetService;
import com.example.androidproject.navigation_drawer_activity.ui.upcoming.UpcomingFragment;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.internal.operators.flowable.FlowableHide;

public class NavigationActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private final String TAG = "tag";
    private UpcomingFragment upcomingFragment;
    boolean readyToSync;
    AppDatabase appDatabase;
    TripDao tripDao;
    private TripViewModel tripViewModel;
    private List<TripModel> tripModels;
    boolean isHomeFragment;

    public static boolean firstTime = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        tripViewModel = new ViewModelProvider(this).get(TripViewModel.class);

        Intent parent = getIntent();
        firstTime = parent.getBooleanExtra("firstTime",false);
        Log.i(TAG, "onCreate: "+parent.getStringExtra("title")+" : "+ parent.getStringExtra("dest"));
        boolean notifyWake = parent.getBooleanExtra("NotifyWakeUp",false);
        if(parent.getBooleanExtra("WakeUp",false)){
            int tripId = parent.getIntExtra("tripID",-1);
            String destination = parent.getStringExtra("dest");
            boolean start = parent.getBooleanExtra("start",false);
            Log.i(TAG, "onCreate: <<<>>>>"+tripId);
            //move trip to history.
            moveToHistory(tripId);
            if(start){
                displayMap(destination,tripId);
            }
            //finish();
        }else if(notifyWake){
            int tripId = parent.getIntExtra("tripID",-1);
            String destination = parent.getStringExtra("dest");
            boolean start = parent.getBooleanExtra("start",false);
            Log.i(TAG, "onCreate: <<<"+tripId+"//"+destination);
            cancelWorkRequest(new Integer(tripId).toString());
            //move trip to history.
            moveToHistory(tripId);
            if(start){
                displayMap(destination,tripId);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getApplicationContext().getSystemService(NotificationManager.class).cancel(13);
            }
            notifyWake = false;

        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        isHomeFragment=true;
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_upcoming, R.id.nav_history, R.id.nav_map)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);



        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                switch (destination.getId()) {
                    case R.id.nav_signout:
                        WorkManager.getInstance(NavigationActivity.this).cancelAllWork();
                        FirebaseAuth.getInstance().signOut();
                        finish();
                        Toast.makeText(NavigationActivity.this, "signed out", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        });


        Log.i(TAG, "onCreate: " + UpcomingFragment.id);


        appDatabase = AppDatabase.getDatabase(getBaseContext());
        tripDao = appDatabase.tripDao();

        tripViewModel = new ViewModelProvider(this).get(TripViewModel.class);
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

        WorkManager.getInstance(this).enqueue(tripRequest);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void clickedOption(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_sync:

                tripViewModel.getAllTrips().observe(this,tripModels -> {
                    Toast.makeText(this, "add to firebase", Toast.LENGTH_SHORT).show();
                    syncDataWithFirebaseDatabase(tripModels);
                });

                drawer.closeDrawers();
                break;

            case R.id.nav_signout:
//                FirebaseAuth.getInstance().signOut();
                WorkManager.getInstance(this).cancelAllWork();
                AuthUI.getInstance()
                        .signOut(NavigationActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(NavigationActivity.this, "Logout successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
//                Toast.makeText(NavigationActivity.this, "sign out", Toast.LENGTH_SHORT).show();
//                Log.i(TAG, "clickedOption: sjhfbadjkjlwigfuiahsgh");
//                finish();
                drawer.closeDrawers();
                break;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (readyToSync = true) {

        }
    }
    void syncDataWithFirebaseDatabase(final List<TripModel> tripList) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference();
        reference.child("trips").removeValue();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        for (int indx = 0; indx < tripList.size(); ++indx) {
            TripModel tripModel = tripList.get(indx);
            reference.child("trips").child(uid).push().setValue(tripModel).addOnCompleteListener(task -> {
                Toast.makeText(this, "done", Toast.LENGTH_SHORT).show();
            });
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    List<TripModel> fetchDataWithFirebaseDatabase() {
        List<TripModel> tripList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("trips").get().addOnCompleteListener(task -> {
            DataSnapshot result = task.getResult();
            Iterable<DataSnapshot> children = result.getChildren();
            children.forEach(dataSnapshot -> {
                TripModel value = dataSnapshot.getValue(TripModel.class);
                tripList.add(value);
            });
        });

        return tripList;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DrawerLayout drawerLayout =(DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            if(isHomeFragment){
                finishAffinity();
            }else{
                isHomeFragment=true;
            }
        }
    }

    private void displayMap(String destination,int id) {
        try {
            Uri uri = Uri.parse("https://www.google.co.in/maps/dir//" + destination);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            NoteViewModel noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
            noteViewModel.getAllNotesById(id).observe(this, noteModels -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                    Intent widgetIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(widgetIntent, 106);
                } else {
                    Intent startIntent = new Intent(this, FloatWidgetService.class);

                    startIntent.putExtra("notes", (ArrayList<NoteModel>) noteModels);
                    startService(startIntent);
                }

            });
        } catch (ActivityNotFoundException e) {
            Uri uri = Uri.parse("http://play.google.com/store/apps/details?id=com.google.android.apps.maps");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void moveToHistory(int tripId){
        Log.i(TAG, "moveToHistory: IAM HERE 1 !!! >> " + tripId);
        LiveData<List<TripModel>> tripById = tripViewModel.getTripById(tripId);
        Log.i(TAG, "moveToHistory: IAM HERE 2 !!!");

        tripById.observe(this, new Observer<List<TripModel>>() {
        
            @Override
            public void onChanged(@Nullable final List<TripModel> tripModels) {
                Log.i(TAG, "onChanged: ID????"+tripById);
                Log.i("TAG", "onCreate: DialogMessageActivity 4");
                TripModel tripModel = tripModels.get(0);
                if (tripModel.getTripRepeatingType().equals("No_Repeat")){
                    tripModel.setStatus(1);
                    tripViewModel.update(tripModel);
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
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: DESTROYED !!");
    }

    void cancelWorkRequest(String name){
        WorkManager.getInstance(this).cancelAllWorkByTag(name);
    }
}

