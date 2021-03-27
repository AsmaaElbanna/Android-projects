package com.example.androidproject.navigation_drawer_activity.support;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.androidproject.SignupActivity;

public class MyReceiver extends BroadcastReceiver {

    private String fileName;

    public MyReceiver(String fileName){
        this.fileName = fileName;
    }
    
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent signUpTry = new Intent(context,SignupActivity.class);
        signUpTry.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        signUpTry.putExtra("tag",fileName);
        context.startActivity(signUpTry);
    }
}
