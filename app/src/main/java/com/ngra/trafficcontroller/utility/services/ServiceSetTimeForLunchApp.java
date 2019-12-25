package com.ngra.trafficcontroller.utility.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.ngra.trafficcontroller.utility.broadcasts.ReceiverJobInBackground;

import java.util.Calendar;

public class ServiceSetTimeForLunchApp extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {//____________________________ Start onStartCommand

//        Toast.makeText(getApplicationContext(),"Start",Toast.LENGTH_SHORT).show();
        Calendar now = Calendar.getInstance();
        Intent intent1 = new Intent(getApplicationContext(), ReceiverJobInBackground.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(getApplicationContext().ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, now.getTimeInMillis(), 120*1000, pendingIntent);
        return Service.START_STICKY;
    }//_____________________________________________________________________________________________ End onStartCommand

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



}
