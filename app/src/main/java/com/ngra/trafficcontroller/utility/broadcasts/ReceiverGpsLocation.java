package com.ngra.trafficcontroller.utility.broadcasts;

        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;

        import com.ngra.trafficcontroller.R;
        import com.ngra.trafficcontroller.utility.NotificationManagerClass;
        import com.ngra.trafficcontroller.utility.services.ServiceSetTimeForLunchApp;
        import com.ngra.trafficcontroller.views.activitys.MainActivity;
        import com.ngra.trafficcontroller.views.application.TrafficController;

        import static com.ngra.trafficcontroller.views.application.TrafficController.ObservablesGpsAndNetworkChange;

public class ReceiverGpsLocation extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {//_______________________________________ Start onReceive
        if (!TrafficController.getApplication(context).isLocationEnabled()) {
            NotificationManagerClass managerClass =
                    new NotificationManagerClass(
                            context,
                            context.getResources().getString(R.string.DisconnectGPS)
                            , false
                            , true
                    );
        }
        context.startService(new Intent(context, ServiceSetTimeForLunchApp.class));
        ObservablesGpsAndNetworkChange.onNext("changeGPS");
    }//_____________________________________________________________________________________________ End onReceive


}
