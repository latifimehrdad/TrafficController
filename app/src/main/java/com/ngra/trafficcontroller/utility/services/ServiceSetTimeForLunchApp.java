package com.ngra.trafficcontroller.utility.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;

import com.ngra.trafficcontroller.R;
import com.ngra.trafficcontroller.database.DataBaseLog;
import com.ngra.trafficcontroller.utility.NotificationManagerClass;
import com.ngra.trafficcontroller.utility.broadcasts.ReceiverJobInBackground;
import com.ngra.trafficcontroller.views.application.TrafficController;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;

public class ServiceSetTimeForLunchApp extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {//____________________________ Start onStartCommand

        NotificationManagerClass managerClass =
                new NotificationManagerClass(
                        getApplicationContext(),
                        getApplicationContext().getResources().getString(R.string.ServiceRun)
                        , true
                        , 3
                );
        Integer id = getApplicationContext().getResources().getInteger(R.integer.NotificationRun);
        android.app.Notification alarmNotify = managerClass.getNotification();
        startForeground(id,alarmNotify);

        SaveLog("onStart Service : " + getStringCurrentDate());
        Calendar now = Calendar.getInstance();
        Intent intent1 = new Intent(getApplicationContext(), ReceiverJobInBackground.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(getApplicationContext().ALARM_SERVICE);
        am.setRepeating (AlarmManager.RTC_WAKEUP, now.getTimeInMillis(), 120*1000, pendingIntent);

        return Service.START_STICKY;
    }//_____________________________________________________________________________________________ End onStartCommand

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        SaveLog("onDestroy : " + getStringCurrentDate());
    }


    private void SaveLog(String log) {//____________________________________________________________ Start SaveLog
        Realm realm = TrafficController
                .getApplication(getApplicationContext())
                .getRealmComponent()
                .getRealm();

        realm.beginTransaction();
        realm.createObject(DataBaseLog.class)
                .insert(log);
        realm.commitTransaction();
    }//_____________________________________________________________________________________________ End SaveLog


    public String getStringCurrentDate() {//_______________________________________________________ Start getStringCurrentDate
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        return simpleDateFormat.format(new Date());
    }//_____________________________________________________________________________________________ End getStringCurrentDate


}
