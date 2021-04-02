package com.example.androidproject.navigation_drawer_activity.support;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class WakeUpReceiver extends BroadcastReceiver {

    private String destination;
    private String title;
    private String source;

    public WakeUpReceiver(String title,String source,String destination){
        this.destination = destination;
        this.title = title;
        this.source = source;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("TAG", "onReceive: >>>"+title+"<<>>"+destination);

    }
}