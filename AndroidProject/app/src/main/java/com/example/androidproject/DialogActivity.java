package com.example.androidproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.example.androidproject.navigation_drawer_activity.NavigationActivity;
import com.example.androidproject.navigation_drawer_activity.support.TripWorker;

import java.util.concurrent.TimeUnit;

public class DialogActivity extends AppCompatActivity {

    private static final String CHANNELID = "13";
    private static final int NOTIFICATIONID = 13;
    private TextView tripNameLbl;
    private TextView tripSourceLbl;
    private TextView tripDestiniationLbl;
    private Button startTripBtn;
    private Button cancelTripBtn;
    private Button snoozeBtn;

    private String tripName;
    private String source;
    private String destination;

    private NotificationManager nManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        Intent parent = getIntent();
        tripName = parent.getStringExtra("title");
        source = parent.getStringExtra("source");
        destination = parent.getStringExtra("dest");

        tripNameLbl = findViewById(R.id.dialog_tripName_lbl);
        tripSourceLbl = findViewById(R.id.dialog_source_lbl);
        tripDestiniationLbl = findViewById(R.id.dialog_dest_lbl);
        startTripBtn = findViewById(R.id.dialog_btn_start);
        cancelTripBtn = findViewById(R.id.dialog_btn_cancel);
        snoozeBtn = findViewById(R.id.dialog_btn_snooze);

        tripNameLbl.setText(tripName);
        tripSourceLbl.setText(source);
        tripDestiniationLbl.setText(destination);

        startTripBtn.setOnClickListener((event)->{
            Intent app = new Intent(this, NavigationActivity.class);
            app.putExtra("title",tripName);
            app.putExtra("dest",destination);
            app.putExtra("WakeUp",true);
            app.putExtra("start",true);
            startActivity(app);
            finish();
        });

        cancelTripBtn.setOnClickListener((event)->{
            Intent app = new Intent(this, NavigationActivity.class);
            app.putExtra("title",tripName);
            app.putExtra("dest",destination);
            app.putExtra("WakeUp",true);
            app.putExtra("start",false);
            startActivity(app);
            finish();
        });

        snoozeBtn.setOnClickListener((event)->{
            createNotificationChannel();
            startWorkManager(5);
            finish();
        });

    }


    @SuppressLint("NewApi")
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getApplicationContext().getString(R.string.channel_name);
            String description = getApplicationContext().getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNELID, name, importance);
            channel.setDescription(description);
            nManager = getApplicationContext().getSystemService(NotificationManager.class);
            nManager.createNotificationChannel(channel);
        }

        //PendingIntent pendingIntent1 = PendingIntent.getBroadcast(getApplicationContext(), 0, startBroadCast(), 0);
        Intent start = new Intent(this,NavigationActivity.class);
        start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        start.putExtra("title",tripName);
        start.putExtra("dest",destination);
        start.putExtra("NotifyWakeUp",true);
        start.putExtra("start",true);
        PendingIntent startIntent = PendingIntent.getActivity(this,0,start,0);

        Intent cancel = new Intent(this,NavigationActivity.class);
        cancel.putExtra("title",tripName);
        cancel.putExtra("dest",destination);
        cancel.putExtra("NotifyWakeUp",true);
        cancel.putExtra("start",false);
        PendingIntent cancelIntent = PendingIntent.getActivity(this,1,cancel,0);

        Intent app = new Intent(this,NavigationActivity.class);
        PendingIntent appIntent = PendingIntent.getActivity(this,2,app,0);

        RemoteViews notificationView = new RemoteViews(getApplicationContext().getPackageName(),
                R.layout.notification_layout);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNELID)
                .setSmallIcon(R.drawable.note_small)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(appIntent);
        builder.setStyle(new NotificationCompat.DecoratedCustomViewStyle());

        RemoteViews contentView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification_layout);
        RemoteViews notificationBig = new RemoteViews(getApplicationContext().getPackageName(),
                R.layout.notification_big);
        notificationBig.setOnClickPendingIntent(R.id.notification_cancel_btn,cancelIntent);
        notificationBig.setOnClickPendingIntent(R.id.notification_start_btn, startIntent);
        //builder.setCustomContentView(notificationView);
        builder.setCustomBigContentView(notificationBig);
        builder.setAutoCancel(true);
        //registerNewReciever();
        nManager.notify(NOTIFICATIONID, builder.build());
    }


    private void startWorkManager(long minutes){

        Data.Builder data = new Data.Builder();
        data.putString("title",tripName);
        data.putString("dest",destination);
        data.putString("source",source);

        WorkRequest tripRequest = new OneTimeWorkRequest.Builder(TripWorker.class)
                .setInitialDelay(10, TimeUnit.SECONDS)
                .addTag(tripName)
                .setInputData(data.build())
                .build();

        WorkManager.getInstance(this).enqueue(tripRequest);
        Log.i("TAG", "startWorkManager: ");
    }

}