package com.example.androidproject.navigation_drawer_activity.support;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RemoteViews;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.androidproject.R;
import com.example.androidproject.SignupActivity;
import com.example.androidproject.navigation_drawer_activity.NavigationActivity;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.TimeUnit;

public class MyReceiver extends BroadcastReceiver {

    private String destination;
    private String title;
    private String source;
    private int index;
    public static final String CHANNELID = "13";
    public static final int NOTIFICATIONID = 13;

    private int tripId;

    private NotificationManager nManager;

    public MyReceiver(int ID,String title,String source,String destination){
        this.destination = destination;
        this.title = title;
        this.source = source;
        this.tripId = ID;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("TAG", "onReceive: RECIEVED >>>"+title+"<<<");
        showDialog(context);
        Log.i("TAG", "onReceive: RECIEVED >>>"+title+"<<<");
    }

    private void showDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.dialogTheme);
        builder.setTitle(title)
                .setMessage("your trip from : "+source+"    to: "+destination)
                .setPositiveButton(R.string.btnStart, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i("msg", "fire");
                        Intent app = new Intent(context, NavigationActivity.class);
                        app.putExtra("tripID",tripId);
                        app.putExtra("dest",destination);
                        app.putExtra("WakeUp",true);
                        app.putExtra("start",true);
                        app.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(app);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        Intent service = new Intent(context,TryService.class);
                        service.putExtra("dest",destination);
                        service.putExtra("tripID",tripId);
                        service.putExtra("start",false);
                        TryService.enqueueWork(context,service);
                        dialog.dismiss();
                    }
                })
                .setNeutralButton(R.string.Snooze, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("TAG", "onClick: SNOOZE");
                        createNotificationChannel(context);
                        startWorkManager(context,5);
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // Do something for Android Pie and above versions
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else {
            // do something for phones running an SDK before Android Pie
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }

        alertDialog.show();
    }


    @SuppressLint("NewApi")
    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNELID, name, importance);
            channel.setDescription(description);
            nManager = context.getSystemService(NotificationManager.class);
            nManager.createNotificationChannel(channel);
        }

        //-----------------------
        Intent start = new Intent(context, NavigationActivity.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(start);
        stackBuilder.editIntentAt(0).putExtra("tripID",tripId)
                .putExtra("dest",destination)
                .putExtra("NotifyWakeUp",true)
                .putExtra("start",true);

        PendingIntent startPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);



        Intent cancel = new Intent(context, NavigationActivity.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder cancelstackBuilder = TaskStackBuilder.create(context);
        cancelstackBuilder.addNextIntentWithParentStack(cancel);
        cancelstackBuilder.editIntentAt(0).putExtra("tripID",tripId)
                .putExtra("dest",destination)
                .putExtra("NotifyWakeUp",true)
                .putExtra("start",false);
        // Get the PendingIntent containing the entire back stack
        PendingIntent cancelPendingIntent =
                cancelstackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);

        //---------------------------------------------------
        SharedPreferences spManager = context.getSharedPreferences("navigate", Context.MODE_PRIVATE);
        SharedPreferences.Editor editManager = spManager.edit();
        editManager.putInt("tripID",tripId);
        editManager.putString("dest",destination);
        editManager.commit();

        Intent startIntent = new Intent(context,TryReceiver.class);
        PendingIntent startPending = PendingIntent.getBroadcast(context,5,startIntent,0);

        Intent cancelIntent = new Intent(context,CancelReceiver.class);
        PendingIntent cancelPending = PendingIntent.getBroadcast(context,13,cancelIntent,0);

        //---------------------------------------------------


        Intent app = new Intent(context, NavigationActivity.class);
        PendingIntent appIntent = PendingIntent.getActivity(context,2,app,0);

        RemoteViews notificationView = new RemoteViews(context.getPackageName(),
                R.layout.notification_layout);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNELID)
                .setContentTitle(title)
                .setContentText("trip from: "+source+"  to: "+destination)
                .setSmallIcon(R.drawable.note_small)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(appIntent);
        builder.setStyle(new NotificationCompat.DecoratedCustomViewStyle());

//        RemoteViews contentView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification_layout);
        RemoteViews notificationBig = new RemoteViews(context.getPackageName(),
                R.layout.notification_big);
        notificationBig.setTextViewText(R.id.notification_title_lbl,title);
        Log.i("TAG", "createNotificationChannel: "+title);
        notificationBig.setTextViewText(R.id.notification_body_lbl,"your trip from: "+source+"\t\t to:"+destination);
        notificationBig.setOnClickPendingIntent(R.id.notification_cancel_btn,cancelPending);
        notificationBig.setOnClickPendingIntent(R.id.notification_start_btn, startPendingIntent);
//        builder.setCustomContentView(notificationView);
        builder.setCustomBigContentView(notificationBig);
        builder.setAutoCancel(true);
        //registerNewReciever();
        nManager.notify(NOTIFICATIONID, builder.build());
    }

    private void startWorkManager(Context context,long minutes){

        Data.Builder data = new Data.Builder();
        data.putString("title",title);
        data.putString("dest",destination);
        data.putString("source",source);
        data.putInt("tripID",tripId);
        Log.i("TAG", "startWorkManager: "+title+destination+source+tripId);

        WorkRequest tripRequest = new OneTimeWorkRequest.Builder(TripWorker.class)
                .setInitialDelay(1, TimeUnit.MINUTES)
                .addTag(new Integer(tripId).toString())
                .setInputData(data.build())
                .build();

        WorkManager.getInstance(context).enqueue(tripRequest);
    }

}
