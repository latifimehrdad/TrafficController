package com.ngra.trafficcontroller.backgroundservice.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.ngra.trafficcontroller.R;
import com.ngra.trafficcontroller.utility.NotificationManagerClass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class ReceiverDateTimeChange extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {//_______________________________________ Start onReceive
        final String action = intent.getAction();

        if (action.equals(Intent.ACTION_TIME_CHANGED) ||
                action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {

            if (CheckTimeChange(context)) {
                NotificationManagerClass managerClass =
                        new NotificationManagerClass(
                                context,
                                context.getResources().getString(R.string.DonChangeTime)
                                , false
                                , 2
                        );
            }
        }


    }//_____________________________________________________________________________________________ End onReceive


    public boolean CheckTimeChange(Context context) {//_____________________________________________ Start getStringCurrentDate

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        SharedPreferences prefs = context.getSharedPreferences("trafficcontroller", 0);
        if (prefs != null) {
            String time = prefs.getString("lasttime", "null");
            if (!time.equalsIgnoreCase("null")) {
                try {
                    Date old = simpleDateFormat.parse(time);
                    Date now = new Date();
                    long bet = now.getTime() - old.getTime();
                    if (bet > 10 * 60 * 1000)
                        return true;
                    else
                        return false;

                } catch (ParseException e) {
                    e.printStackTrace();
                    return false;
                }
            } else
                return false;
        }

        return false;
    }//_____________________________________________________________________________________________ End getStringCurrentDate


}
