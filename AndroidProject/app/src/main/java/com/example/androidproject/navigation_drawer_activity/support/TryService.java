package com.example.androidproject.navigation_drawer_activity.support;

import android.app.Application;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.androidproject.dbroom.NoteModel;
import com.example.androidproject.dbroom.TripModel;
import com.example.androidproject.dbroom.TripRepository;
import com.example.androidproject.dbroom.TripViewModel;
import com.example.androidproject.navigation_drawer_activity.ui.map.FloatWidgetService;

import java.security.acl.Owner;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TryService extends JobIntentService {

    private static final int JOB_ID = 1313;
    private TripViewModel tripViewModel;
    private TripRepository tripRepository;
    static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, TryService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.i("TAG", "onHandleWork: IN SERVICE !!>>>><<<<");
        int tripId = intent.getIntExtra("tripID",-1);
        String destination = intent.getStringExtra("dest");
        boolean start = intent.getBooleanExtra("start",false);
        Log.i("TAG", "onCreate: <<<>>>>"+tripId);
        //move trip to history.
        tripRepository = new TripRepository(getApplication());
        changeTripStatus(tripId);
        Log.i("TAG", "onHandleWork: >>>>>"+tripRepository.getTripById(tripId).getValue());
        if(start){
            displayMap(destination);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getApplicationContext().getSystemService(NotificationManager.class).cancel(13);
        }
        cancelWorkRequest(new Integer(tripId).toString());
    }

    private void displayMap(String destination) {
        try {
            Uri uri = Uri.parse("https://www.google.co.in/maps/dir//" + destination);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
            mapIntent.setPackage("com.google.android.apps.maps");
            mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mapIntent);
        } catch (ActivityNotFoundException e) {
            Uri uri = Uri.parse("http://play.google.com/store/apps/details?id=com.google.android.apps.maps");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
            mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mapIntent);
        }
    }

    void cancelWorkRequest(String name){
        WorkManager.getInstance(this).cancelAllWorkByTag(name);
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
                .setInitialDelay(10, TimeUnit.SECONDS)
                .addTag(new Integer(id).toString())
                .setInputData(data.build())
                .build();

        WorkManager.getInstance(this).enqueue(tripRequest);
    }

    private void changeTripStatus(int id){

    }
}