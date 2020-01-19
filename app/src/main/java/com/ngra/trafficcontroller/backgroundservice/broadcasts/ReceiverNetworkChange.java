package com.ngra.trafficcontroller.backgroundservice.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;

import com.ngra.trafficcontroller.R;
import com.ngra.trafficcontroller.utility.NotificationManagerClass;
import com.ngra.trafficcontroller.views.application.TrafficController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.ngra.trafficcontroller.views.application.TrafficController.ObservablesGpsAndNetworkChange;

public class ReceiverNetworkChange extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {//_______________________________________ Start onReceive

        ObservablesGpsAndNetworkChange.onNext("changeNetwork");

        if(!CheckTimeChange(context))
            return;

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
                                    ,0
                            );
                }
            }
        },10 * 1000);


    }//_____________________________________________________________________________________________ End onReceive


    public boolean CheckTimeChange(Context context) {//_____________________________________________ Start getStringCurrentDate

        SharedPreferences prefs = context.getSharedPreferences("trafficcontroller", 0);
        if (prefs != null) {
            boolean isNetwork = prefs.getBoolean("isnetwork", false);
            if (isNetwork) {
                if (!TrafficController.getApplication(context).isInternetConnected()){
                    SharedPreferences.Editor perf =
                            context.getSharedPreferences("trafficcontroller", 0).edit();
                    perf.putBoolean("isnetwork", false);
                    perf.apply();
                    return true;
                } else return false;

            } else {
                if (TrafficController.getApplication(context).isInternetConnected()){
                    SharedPreferences.Editor perf =
                            context.getSharedPreferences("trafficcontroller", 0).edit();
                    perf.putBoolean("isnetwork", true);
                    perf.apply();
                    return false;
                } else return false;
            }
        } else {
            SharedPreferences.Editor perf =
                    context.getSharedPreferences("trafficcontroller", 0).edit();
            if (!TrafficController.getApplication(context).isInternetConnected()){
                perf.putBoolean("isnetwork", false);
                perf.apply();
                return true;
            } else {
                perf.putBoolean("isnetwork", true);
                perf.apply();
                return false;
            }

        }
    }//_____________________________________________________________________________________________ End getStringCurrentDate



}