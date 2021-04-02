package com.example.androidproject.navigation_drawer_activity;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.androidproject.AddTripActivity;
import com.example.androidproject.R;
import com.example.androidproject.dbroom.AppDatabase;
import com.example.androidproject.dbroom.TripDao;
import com.example.androidproject.dbroom.TripModel;
import com.example.androidproject.dbroom.TripViewModel;
import com.example.androidproject.navigation_drawer_activity.model.TripData;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.work.WorkManager;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        Intent parent = getIntent();
        Log.i(TAG, "onCreate: "+parent.getStringExtra("title")+" : "+ parent.getStringExtra("dest"));
        if(parent.getBooleanExtra("WakeUp",false)){
            int tripId = parent.getIntExtra("tripID",-1);
            String destination = parent.getStringExtra("dest");
            boolean start = parent.getBooleanExtra("start",false);
            Log.i(TAG, "onCreate: <<<"+tripId);
            //move trip to history.
            if(start){
                displayMap(destination);
            }
            finish();
        }else if(parent.getBooleanExtra("NotifyWakeUp",false)){
            int tripId = parent.getIntExtra("tripID",-1);
            String destination = parent.getStringExtra("dest");
            boolean start = parent.getBooleanExtra("start",false);
            Log.i(TAG, "onCreate: <<<"+tripId);
            //move trip to history.
            if(start){
                displayMap(destination);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getApplicationContext().getSystemService(NotificationManager.class).cancel(13);
            }
            finish();
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
                        FirebaseAuth.getInstance().signOut();
                        finish();
                        Toast.makeText(NavigationActivity.this, "sign out", Toast.LENGTH_SHORT).show();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                TripData result = (TripData) data.getSerializableExtra("result");
                Toast.makeText(this, "done", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onActivityResult: done" + result.tripName);
//                upcomingFragment.addTrip(result);

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

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

    private void displayMap(String destination) {
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

    void cancelWorkRequest(String name){
        WorkManager.getInstance(this).cancelAllWorkByTag(name);
    }
}

