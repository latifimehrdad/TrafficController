package com.ngra.trafficcontroller.utility.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.ngra.trafficcontroller.R;
import com.ngra.trafficcontroller.utility.NotificationManagerClass;
import com.ngra.trafficcontroller.views.application.TrafficController;

import static com.ngra.trafficcontroller.views.application.TrafficController.ObservablesGpsAndNetworkChange;

public class ReceiverNetworkChange extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {//_______________________________________ Start onReceive
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!TrafficController.getApplication(context).isInternetConnected()){
                    NotificationManagerClass managerClass =
                            new NotificationManagerClass(
                                    context,
                                    context.getResources().getString(R.string.DisconnectNet)
                                    ,false
                                    ,false
                            );
                }
            }
        },10 * 1000);

        ObservablesGpsAndNetworkChange.onNext("changeNetwork");
    }//_____________________________________________________________________________________________ End onReceive
}