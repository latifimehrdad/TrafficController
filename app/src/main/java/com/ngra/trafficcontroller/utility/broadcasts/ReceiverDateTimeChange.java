package com.ngra.trafficcontroller.utility.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ngra.trafficcontroller.utility.NotificationManagerClass;


public class ReceiverDateTimeChange extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        if (action.equals(Intent.ACTION_TIME_CHANGED) ||
                action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {
            NotificationManagerClass managerClass =
                    new NotificationManagerClass(
                            context,
                            "کار زشتی میکنی ساعت و تغییر میدی"
                            , false
                            , 2
                    );
        }


    }
}
