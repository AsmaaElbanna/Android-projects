package com.example.androidproject.navigation_drawer_activity.support;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RemoteViews;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.androidproject.DialogActivity;
import com.example.androidproject.R;
import com.example.androidproject.SignupActivity;
import com.example.androidproject.navigation_drawer_activity.NavigationActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.internal.operators.flowable.FlowableHide;

public class MyReceiver extends BroadcastReceiver {

    private String destination;
    private String title;
    private String source;
    private int index;
    public static final String CHANNELID = "13";
    public static final int NOTIFICATIONID = 13;

    private NotificationManager nManager;

    public MyReceiver(String title,String source,String destination){
        this.destination = destination;
        this.title = title;
        this.source = source;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("TAG", "onReceive: RECIEVED >>>"+title+"<<<");
        showDialog(context);
        Log.i("TAG", "onReceive: RECIEVED >>>"+title+"<<<");
    }

    private void showDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.dialogTheme);
        builder.setTitle(title)
                .setMessage("your trip from : "+source+"    to: "+destination)
                .setPositiveButton(R.string.btnStart, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i("msg", "fire");
                        Intent app = new Intent(context, NavigationActivity.class);
                        app.putExtra("title",title);
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
                        Log.i("msg", "cancel");
                        Intent app = new Intent(context, NavigationActivity.class);
                        app.putExtra("title",title);
                        app.putExtra("dest",destination);
                        app.putExtra("WakeUp",true);
                        app.putExtra("start",false);
                        app.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(app);
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

        //PendingIntent pendingIntent1 = PendingIntent.getBroadcast(getApplicationContext(), 0, startBroadCast(), 0);
        Intent start = new Intent(context, NavigationActivity.class);
        start.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        start.putExtra("title",title);
        Log.i("TAG", "createNotificationChannel: >>>"+title+"<<<");
        start.putExtra("dest",destination);
        start.putExtra("NotifyWakeUp",true);
        start.putExtra("start",true);
        start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent startIntent = PendingIntent.getActivity(context,0,start,0);

//        WakeUpReceiver wakeUpReceiver = new WakeUpReceiver(title,source,destination);
//        PendingIntent rec = PendingIntent.getBroadcast(context,0,wakeUpReceiver,0);



        Intent cancel = new Intent(context, NavigationActivity.class);
        cancel.putExtra("title",title);
        cancel.putExtra("dest",destination);
        cancel.putExtra("NotifyWakeUp",true);
        cancel.putExtra("start",false);
        start.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent cancelIntent = PendingIntent.getActivity(context,1,cancel,0);

        Intent app = new Intent(context, NavigationActivity.class);
        PendingIntent appIntent = PendingIntent.getActivity(context,2,app,0);

        RemoteViews notificationView = new RemoteViews(context.getPackageName(),
                R.layout.notification_layout);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNELID)
                .setSmallIcon(R.drawable.note_small)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(appIntent);
        builder.setStyle(new NotificationCompat.DecoratedCustomViewStyle());

//        RemoteViews contentView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification_layout);
        RemoteViews notificationBig = new RemoteViews(context.getPackageName(),
                R.layout.notification_big);
        notificationBig.setOnClickPendingIntent(R.id.notification_cancel_btn,cancelIntent);
        notificationBig.setOnClickPendingIntent(R.id.notification_start_btn, startIntent);
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

        WorkRequest tripRequest = new OneTimeWorkRequest.Builder(TripWorker.class)
                .setInitialDelay(1, TimeUnit.MINUTES)
                .addTag(title)
                .setInputData(data.build())
                .build();

        WorkManager.getInstance(context).enqueue(tripRequest);
        Log.i("TAG", "startWorkManager: ");
    }
}
