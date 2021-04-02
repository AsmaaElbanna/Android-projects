package com.example.androidproject.navigation_drawer_activity.support;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.androidproject.DialogActivity;
import com.example.androidproject.R;
import com.example.androidproject.navigation_drawer_activity.NavigationActivity;

public class TripWorker extends Worker {
    NotificationManager nManager;
    MyReceiver receiver;

    public static final String CHANNELID = "13";
    public static final int NOTIFICATIONID = 13;

    private String title;
    private String destination;
    private String source;
    private int tripId;

    public TripWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {

        Log.i("moutaz", "doWork: MY WORK");
        Data data = getInputData();
        destination = data.getString("dest");
        title = data.getString("title");
        source = data.getString("source");
        tripId = data.getInt("tripID",-1);
        Log.i("TAG", "doWork: 11111"+title+destination+source+tripId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            nManager = getApplicationContext().getSystemService(NotificationManager.class);
            nManager.cancel(13);
        }

        Log.i("TAG", "doWork: "+title+destination+source+tripId);
        Intent service = new Intent(getApplicationContext(),MyService.class);
        service.putExtra("title",title);
        service.putExtra("source",source);
        service.putExtra("dest",destination);
        service.putExtra("tripID",tripId);
        MyService.enqueueWork(getApplicationContext(),service);
        return Result.success();
    }
}
