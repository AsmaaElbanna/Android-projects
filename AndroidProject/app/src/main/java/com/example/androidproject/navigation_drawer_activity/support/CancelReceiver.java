package com.example.androidproject.navigation_drawer_activity.support;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class CancelReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences spManager = context.getSharedPreferences("navigate", Context.MODE_PRIVATE);
        Log.i("TAG", "onReceive: TryReceiver Just Received <<<<");
        int tripId = spManager.getInt("tripID",-1);
        String destination = spManager.getString("dest","N/A");

        Intent service = new Intent(context,TryService.class);
        service.putExtra("dest",destination);
        service.putExtra("tripID",tripId);
        service.putExtra("start",false);
        TryService.enqueueWork(context,service);
    }
}