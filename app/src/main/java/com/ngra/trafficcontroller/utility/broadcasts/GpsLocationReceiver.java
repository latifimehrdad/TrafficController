package com.ngra.trafficcontroller.utility.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import static com.ngra.trafficcontroller.views.application.TrafficController.ObservablesGpsAndNetworkChange;

public class GpsLocationReceiver extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {//_______________________________________ Start onReceive
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED"))
            ObservablesGpsAndNetworkChange.onNext("changeGPS");
    }//_____________________________________________________________________________________________ End onReceive



}
