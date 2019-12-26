package com.ngra.trafficcontroller.utility;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
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

public class NotificationManagerClass {

    private Context context;
    private NotificationManager notifManager;
    private Integer NotiId;
    private Bitmap icon;
    private long when;
    private String CHANNEL_ONE_NAME = "New Event";
    private String CHANNEL_ONE_ID = "com.ngra.trafficcontroller";
    private String Text;
    private Boolean ShowAlways;
    private boolean GPS;


    public NotificationManagerClass(Context context, String text, Boolean showAlways, boolean GPS) {// Start NotificationManager
        this.context = context;
        Text = text;
        ShowAlways = showAlways;
        this.GPS = GPS;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CreateChannels();
            ShowNotificationNew();
        } else {
            ShowNotificationOld();
        }

    }//_____________________________________________________________________________________________ End NotificationManager





    private void ShowNotificationOld() {//__________________________________________________________ Start ShowNotificationOld

        SetNotiIdAndBitmap(GPS);
        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
                context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 80, 80, false))
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(Text))
                .setSound(getSound())
                .setAutoCancel(true)
                .setWhen(when)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        mNotifyBuilder.setColor(context.getResources().getColor(R.color.colorPrimary));
        getManager().notify(NotiId, mNotifyBuilder.build());

    }//_____________________________________________________________________________________________ End ShowNotificationOld


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void ShowNotificationNew() {//___________________________________________________________ Start ShowNotificationNew
        SetNotiIdAndBitmap(GPS);
        CreateChannels();
        Notification.Builder builder = new Notification.Builder(context, CHANNEL_ONE_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(Text)
                .setLargeIcon(icon)
                .setStyle(new Notification.BigTextStyle()
                        .bigText(Text))
                .setAutoCancel(true);
        builder.setColor(context.getResources().getColor(R.color.colorPrimary));
        getManager().notify(NotiId, builder.build());
    }//_____________________________________________________________________________________________ End ShowNotificationNew


    private void SetNotiIdAndBitmap(boolean GPS) {//________________________________________________ Start SetNotiIdAndBitmap
        when = System.currentTimeMillis();
        if (GPS) {
            icon = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.gps_off);
            NotiId = 7126;
        } else {
            icon = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.net_off);
            NotiId = 6780;
        }
    }//_____________________________________________________________________________________________ End SetNotiIdAndBitmap



    @TargetApi(Build.VERSION_CODES.O)
    public void CreateChannels() {//________________________________________________________________ Start createChannels
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                CHANNEL_ONE_NAME, notifManager.IMPORTANCE_HIGH);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(context.getResources().getColor(R.color.colorPrimary));
        notificationChannel.setShowBadge(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getManager().createNotificationChannel(notificationChannel);
    }//_____________________________________________________________________________________________ End createChannels



    public Uri getSound() {//________________________________________________________________________ Start getSound
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    }//_____________________________________________________________________________________________ End getSound


    private NotificationManager getManager() {//____________________________________________________ Start getManager
        if (notifManager == null) {
            notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notifManager;
    }//_____________________________________________________________________________________________ End getManager

}
