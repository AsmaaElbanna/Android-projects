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

        createNotificationChannel();

//        Intent service = new Intent(getApplicationContext(),MyService.class);
//        service.putExtra("title",title);
//        service.putExtra("source",source);
//        service.putExtra("dest",destination);
//        MyService.enqueueWork(getApplicationContext(),service);

//        registerNewReciever(title,source,destination);
        Log.i("TAG", "doWork: "+destination+"/"+title);
        //createNotificationChannel();
//        startBroadCast(title);
        Intent trip = new Intent(getApplicationContext(), DialogActivity.class);
        trip.putExtra("source",source);
        trip.putExtra("dest",destination);
        trip.putExtra("title",title);
        trip.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        getApplicationContext().startActivity(trip);
        // Indicate whether the work finished successfully with the Result
        return Result.success();
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
        Intent start = new Intent(getApplicationContext(), NavigationActivity.class);
        start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        start.putExtra("title",title);
        start.putExtra("dest",destination);
        start.putExtra("NotifyWakeUp",true);
        start.putExtra("start",true);
        PendingIntent startIntent = PendingIntent.getActivity(getApplicationContext(),0,start,0);

        Intent cancel = new Intent(getApplicationContext(), NavigationActivity.class);
        cancel.putExtra("title",title);
        cancel.putExtra("dest",destination);
        cancel.putExtra("NotifyWakeUp",true);
        cancel.putExtra("start",false);
        PendingIntent cancelIntent = PendingIntent.getActivity(getApplicationContext(),1,cancel,0);

        Intent app = new Intent(getApplicationContext(), NavigationActivity.class);
        PendingIntent appIntent = PendingIntent.getActivity(getApplicationContext(),2,app,0);

        RemoteViews notificationView = new RemoteViews(getApplicationContext().getPackageName(),
                R.layout.notification_layout);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNELID)
                .setSmallIcon(R.drawable.note_small)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(appIntent);
        builder.setStyle(new NotificationCompat.DecoratedCustomViewStyle());

//        RemoteViews contentView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification_layout);
        RemoteViews notificationBig = new RemoteViews(getApplicationContext().getPackageName(),
                R.layout.notification_big);
        notificationBig.setOnClickPendingIntent(R.id.notification_cancel_btn,cancelIntent);
        notificationBig.setOnClickPendingIntent(R.id.notification_start_btn, startIntent);
//        builder.setCustomContentView(notificationView);
        builder.setCustomBigContentView(notificationBig);
        builder.setAutoCancel(true);
        //registerNewReciever();
        nManager.notify(NOTIFICATIONID, builder.build());
    }

    @Override
    public void onStopped() {
        super.onStopped();
        getApplicationContext().unregisterReceiver(receiver);
    }

    private Intent startBroadCast(String title) {
        Intent intent = new Intent();
        intent.setAction("hello");
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        return intent;
    }

//    private void registerNewReciever(String title,String source,String dist) {
//        IntentFilter filter = new IntentFilter("hello");
//        receiver = new MyReceiver(title,source,dist);
//        getApplicationContext().registerReceiver(receiver, filter);
//    }
}
