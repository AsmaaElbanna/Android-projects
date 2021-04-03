package com.example.androidproject.navigation_drawer_activity.support;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;


public class MyService extends JobIntentService {

    private BroadcastReceiver receiver;
    static final String TAG = "APPLICATION";
    private static final int JOB_ID = 1000;

    static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, MyService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        String title = intent.getStringExtra("title");
        Log.i(TAG, "onHandleWork: >>>"+title+"<<<");
        String source = intent.getStringExtra("source");
        String destination = intent.getStringExtra("dest");
        int tripId = intent.getIntExtra("tripID",-1);
        Log.i(TAG, "onHandleWork: ID>>>"+tripId);
        registerNewReciever(tripId,title,source,destination);
        startBroadCast(new Integer(tripId).toString());
    }


    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        Log.i(TAG, "onDestroy: IT IS DESTROYED!!");
    }

    private void startBroadCast(String title){
        Intent intent = new Intent();
        intent.setAction(title);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intent);
    }

    private void registerNewReciever(int tripId,String title,String source, String dest){
        IntentFilter filter = new IntentFilter(new Integer(tripId).toString());
        receiver = new MyReceiver(tripId,title,source,dest);
        registerReceiver(receiver,filter);
    }

}