package com.example.androidproject.navigation_drawer_activity.support;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class WakeUpService extends Service {
    public WakeUpService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}