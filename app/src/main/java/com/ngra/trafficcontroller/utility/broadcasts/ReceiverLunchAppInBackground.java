package com.ngra.trafficcontroller.utility.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.ngra.trafficcontroller.utility.services.ServiceSetTimeForLunchApp;

public class ReceiverLunchAppInBackground extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, ServiceSetTimeForLunchApp.class));

    }


}
