package com.ngra.trafficcontroller.utility.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ngra.trafficcontroller.views.application.TrafficController;

import static com.ngra.trafficcontroller.views.application.TrafficController.ObservablesGpsAndNetworkChange;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {//_______________________________________ Start onReceive
        ObservablesGpsAndNetworkChange.onNext("changeNetwork");
    }//_____________________________________________________________________________________________ End onReceive
}