package com.ngra.trafficcontroller.utility.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ngra.trafficcontroller.R;
import com.ngra.trafficcontroller.utility.NotificationManagerClass;


public class ReceiverDateTimeChange extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {//_______________________________________ Start onReceive
        final String action = intent.getAction();

        if (action.equals(Intent.ACTION_TIME_CHANGED) ||
                action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {
            NotificationManagerClass managerClass =
                    new NotificationManagerClass(
                            context,
                            context.getResources().getString(R.string.DonChangeTime)
                            , false
                            , 2
                    );
        }


    }//_____________________________________________________________________________________________ End onReceive
}
