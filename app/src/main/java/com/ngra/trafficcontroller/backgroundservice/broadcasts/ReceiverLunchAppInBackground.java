package com.ngra.trafficcontroller.backgroundservice.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.ngra.trafficcontroller.backgroundservice.services.ServiceSetTimeForLunchApp;

public class ReceiverLunchAppInBackground extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {//_______________________________________ Start onReceive

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, ServiceSetTimeForLunchApp.class));
        } else {
            context.startService(new Intent(context, ServiceSetTimeForLunchApp.class));
        }

    }//_____________________________________________________________________________________________ End onReceive


}
