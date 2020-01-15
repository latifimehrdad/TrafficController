package com.ngra.trafficcontroller.utility;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.ngra.trafficcontroller.R;
import com.ngra.trafficcontroller.views.activitys.MainActivity;

public class NotificationManagerClass {

    private Context context;
    private NotificationManager notifManager;
    private Integer NotiId;
    private Bitmap icon;
    private long when;
    private String CHANNEL_ONE_NAME = "New Event";
    private String CHANNEL_ONE_ID = "com.ngra.trafficcontroller.Event";
    private String CHANNEL_SERVICE_NAME = "Run App";
    private String CHANNEL_SERVICE_ID = "com.ngra.trafficcontroller.Run";
    private String Text;
    private Boolean ShowAlways;
    private int GPS;
    private NotificationCompat.Builder OldNotifyBuilder;
    private Notification.Builder NewNotifyBuilder;
    private android.app.Notification notification;


    public NotificationManagerClass(
            Context context,
            String text,
            Boolean showAlways,
            int GPS) {//____________________________________________________________________________ Start NotificationManager
        this.context = context;
        Text = text;
        ShowAlways = showAlways;
        this.GPS = GPS;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CreateChannelsEvent();
            ShowNotificationNew();
        } else {
            ShowNotificationOld();
        }

    }//_____________________________________________________________________________________________ End NotificationManager


    private void ShowNotificationOld() {//__________________________________________________________ Start ShowNotificationOld

        Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        SetNotiIdAndBitmap(GPS);
        OldNotifyBuilder = new NotificationCompat.Builder(
                context)
                .setOngoing(ShowAlways)
                .setSmallIcon(R.drawable.miniicon)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 80, 80, false))
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(Text))
                .setAutoCancel(true)
                .setWhen(when)
                .setContentIntent(resultPendingIntent);
        if (GPS != 3) {
            OldNotifyBuilder.setSound(getSound());
            OldNotifyBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        }
        OldNotifyBuilder.setColor(context.getResources().getColor(R.color.colorPrimary));
        getManager().notify(NotiId, OldNotifyBuilder.build());
        notification = OldNotifyBuilder.build();

    }//_____________________________________________________________________________________________ End ShowNotificationOld


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void ShowNotificationNew() {//___________________________________________________________ Start ShowNotificationNew
        Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        SetNotiIdAndBitmap(GPS);
        NewNotifyBuilder = new Notification.Builder(context, CHANNEL_ONE_ID)
                .setOngoing(ShowAlways)
                .setSmallIcon(R.drawable.miniicon)
                .setContentText(Text)
                .setLargeIcon(icon)
                .setStyle(new Notification.BigTextStyle()
                        .bigText(Text))
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true);
        NewNotifyBuilder.setColor(context.getResources().getColor(R.color.colorPrimary));
        getManager().notify(NotiId, NewNotifyBuilder.build());
        notification = NewNotifyBuilder.build();
    }//_____________________________________________________________________________________________ End ShowNotificationNew


    private void SetNotiIdAndBitmap(int GPS) {//________________________________________________ Start SetNotiIdAndBitmap
        when = System.currentTimeMillis();
        if (GPS == 1) {
            icon = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.gps_off);
            NotiId = context.getResources().getInteger(R.integer.NotificationGPS);
        } else if (GPS == 0) {
            icon = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.net_off);
            NotiId = context.getResources().getInteger(R.integer.NotificationNet);
        } else if (GPS == 2) {
            NotiId = context.getResources().getInteger(R.integer.NotificationDate);
            icon = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.changetime);
        } else if (GPS == 3) {
            NotiId = context.getResources().getInteger(R.integer.NotificationRun);
            icon = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.miniicon);
        } else if (GPS == 4) {
            NotiId = context.getResources().getInteger(R.integer.NotificationEnter);
            icon = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.enter_locatoin);
        }
        else if (GPS == 5) {
            NotiId = context.getResources().getInteger(R.integer.NotificationExit);
            icon = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.exit_location);
        }
    }//_____________________________________________________________________________________________ End SetNotiIdAndBitmap


    @TargetApi(Build.VERSION_CODES.O)
    public void CreateChannelsEvent() {//___________________________________________________________ Start CreateChannelsEvent
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                CHANNEL_ONE_NAME, notifManager.IMPORTANCE_HIGH);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(context.getResources().getColor(R.color.colorPrimary));
        notificationChannel.setShowBadge(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getManager().createNotificationChannel(notificationChannel);
    }//_____________________________________________________________________________________________ End CreateChannelsEvent


//    @TargetApi(Build.VERSION_CODES.O)
//    public void CreateChannelsRun() {//_____________________________________________________________ Start CreateChannelsRun
//        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_SERVICE_ID,
//                CHANNEL_SERVICE_NAME, notifManager.IMPORTANCE_HIGH);
//        notificationChannel.enableLights(true);
//        notificationChannel.setLightColor(context.getResources().getColor(R.color.colorPrimary));
//        notificationChannel.setShowBadge(true);
//        notificationChannel.setSound(null,null);
//        notificationChannel.enableVibration(false);
//        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
//        getManager().createNotificationChannel(notificationChannel);
//    }//_____________________________________________________________________________________________ End CreateChannelsRun


    public Uri getSound() {//________________________________________________________________________ Start getSound
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    }//_____________________________________________________________________________________________ End getSound


    private NotificationManager getManager() {//____________________________________________________ Start getManager
        if (notifManager == null) {
            notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notifManager;
    }//_____________________________________________________________________________________________ End getManager


    public NotificationCompat.Builder getOldNotifyBuilder() {//_____________________________________ Start getOldNotifyBuilder
        return OldNotifyBuilder;
    }//_____________________________________________________________________________________________ End getOldNotifyBuilder


    public Notification.Builder getNewNotifyBuilder() {//___________________________________________ Start getNewNotifyBuilder
        return NewNotifyBuilder;
    }//_____________________________________________________________________________________________ End getNewNotifyBuilder


    public Notification getNotification() {//_______________________________________________________ Start getNotification
        return notification;
    }//_____________________________________________________________________________________________ End getNotification
}
