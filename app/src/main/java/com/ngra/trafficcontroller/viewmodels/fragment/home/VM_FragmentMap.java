package com.ngra.trafficcontroller.viewmodels.fragment.home;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.ngra.trafficcontroller.views.application.TrafficController;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class VM_FragmentMap {

    private Context context;

    public VM_FragmentMap(Context context) {//______________________________________________________ Start VM_FragmentMap
        this.context = context;
    }//_____________________________________________________________________________________________ End VM_FragmentMap


    public List<LatLng> GetWorkingRange() {//_______________________________________________________ Start GetWorkingRange

        List<LatLng> latLngs = new ArrayList<>();
        latLngs.add(new LatLng(35.831420, 50.959401));
        latLngs.add(new LatLng(35.830141, 50.963637));
        latLngs.add(new LatLng(35.829315, 50.963046));
        latLngs.add(new LatLng(35.829080, 50.963432));
        latLngs.add(new LatLng(35.826106, 50.961197));
        latLngs.add(new LatLng(35.826665, 50.958141));
        latLngs.add(new LatLng(35.831420, 50.959401));
        return latLngs;
    }//_____________________________________________________________________________________________ End GetWorkingRange


/*
    public List<LatLng> GetWorking() {//____________________________________________________________ Start GetWorkingRange

        Realm realm = TrafficController
                .getApplication(context)
                .getRealmComponent()
                .getRealm();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 01);

        Calendar now = Calendar.getInstance();

        RealmResults<DataBaseLocation> locations =
                realm
                        .where(DataBaseLocation.class)
                        .between("SaveDate",calendar.getTime(),now.getTime())
                        .findAll();

        List<LatLng> latLngs = new ArrayList<>();
        for (DataBaseLocation location : locations) {
            latLngs.add(new LatLng(location.getLatitude(), location.getLongitude()));
        }
        return latLngs;
    }//_____________________________________________________________________________________________ End GetWorkingRange
*/

}
