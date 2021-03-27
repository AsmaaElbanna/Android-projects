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
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.androidproject.DialogActivity;
import com.example.androidproject.R;
import com.example.androidproject.SignupActivity;

import com.example.androidproject.navigation_drawer_activity.NavigationActivity;

public class UploadWorker extends Worker {
    String dist = "Alex";
    NotificationManager nManager;
    MyReceiver receiver;

    public UploadWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {

        // Do the work here--in this case, upload the images.
        //uploadImages();
        Log.i("moutaz", "doWork: MY WORK");
        createNotificationChannel();
        Intent intent = new Intent(getApplicationContext(), DialogActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        getApplicationContext().startActivity(intent);

        // Indicate whether the work finished successfully with the Result
        return Result.success();
    }

    @SuppressLint("NewApi")
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getApplicationContext().getString(R.string.channel_name);
            String description = getApplicationContext().getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("13", name, importance);
            channel.setDescription(description);
            nManager = getApplicationContext().getSystemService(NotificationManager.class);
            nManager.createNotificationChannel(channel);
        }
        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
        intent.putExtra("tag", dist);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(getApplicationContext(), 0, startBroadCast(), 0);

        RemoteViews notificationView = new RemoteViews(getApplicationContext().getPackageName(),
                R.layout.notification_layout);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "13")
                .setSmallIcon(R.drawable.note_small)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);
        builder.setStyle(new NotificationCompat.DecoratedCustomViewStyle());

        RemoteViews contentView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification_layout);
        RemoteViews notificationBig = new RemoteViews(getApplicationContext().getPackageName(),
                R.layout.notification_big);
        notificationBig.setOnClickPendingIntent(R.id.notification_start_btn, pendingIntent1);
//        notificationBig.setOnClickPendingIntent(R.id.notification_cancel_btn,tryCancel );
        builder.setCustomContentView(notificationView);
        builder.setCustomBigContentView(notificationBig);
        builder.setAutoCancel(true);

        //builder.setCustomContentView(contentView);
        //builder.setContent(contentView);
        registerNewReciever();
        nManager.notify(13, builder.build());
    }

    @Override
    public void onStopped() {
        super.onStopped();
        getApplicationContext().unregisterReceiver(receiver);
    }

    private Intent startBroadCast() {
        Intent intent = new Intent();
        intent.setAction("ImageBroadCast");
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        //intent.putExtra(MainActivity.LINK,FILENAME);
        return intent;
    }

    private void registerNewReciever() {
        IntentFilter filter = new IntentFilter("ImageBroadCast");
        receiver = new MyReceiver(dist);
        getApplicationContext().registerReceiver(receiver, filter);
    }
}
