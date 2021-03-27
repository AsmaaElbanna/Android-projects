package com.example.androidproject.navigation_drawer_activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.androidproject.AddTripActivity;
import com.example.androidproject.R;
import com.example.androidproject.navigation_drawer_activity.model.TripData;
import com.example.androidproject.navigation_drawer_activity.ui.upcoming.UpcomingFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class NavigationActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private final String TAG = "tag";
    private UpcomingFragment upcomingFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        FloatingActionButton fab = findViewById(R.id.upcoming_addBtn);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int LAUNCH_SECOND_ACTIVITY = 1;
//                Intent i = new Intent(NavigationActivity.this, AddTripActivity.class);
//                startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);
//            }
//        });
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_upcoming,R.id.nav_history,R.id.nav_map)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

//        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
//            @Override
//            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
//                switch (destination.getId()){
//                    case R.id.nav_upcoming:
//                        fab.show();
//                        break;
//                    default:
//                        fab.hide();
//                        break;
//                }
//            }
//        });

        Log.i(TAG, "onCreate: " + UpcomingFragment.id);
//        upcomingFragment = (UpcomingFragment) manager.findFragmentByTag(UpcomingFragment.tag);
//        if (upcomingFragment != null){
//            Log.i(TAG, "onCreate: Successful");
//        } else
//            Log.i(TAG, "onCreate: failed");

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.navigation, menu);
//        return true;
//    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void clickedOption(MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_sync:
                // Sync code
                drawer.closeDrawers();
                break;

            case R.id.nav_signout:
                //sign out code
                drawer.closeDrawers();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                TripData result= (TripData) data.getSerializableExtra("result");
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

    }
}