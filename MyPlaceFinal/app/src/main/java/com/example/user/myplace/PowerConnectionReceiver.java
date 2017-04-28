package com.example.user.myplace;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


public class PowerConnectionReceiver extends BroadcastReceiver  {
    @Override
    public void onReceive(Context context, Intent intent) {

        // check and let the user know if device is connected to charger with a toast
        if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
            Toast.makeText(context, "The device is charging", Toast.LENGTH_SHORT).show();
        } else if (intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
            Toast.makeText(context, "The device is not charging", Toast.LENGTH_SHORT).show();
        }
    }

}
