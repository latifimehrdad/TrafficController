package com.ngra.trafficcontroller.viewmodels.fragment.home;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
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




    public List<LatLng> GetWorking() {//____________________________________________________________ Start GetWorkingRange

        List<LatLng> latLngs = new ArrayList<>();
        latLngs.add(new LatLng(35.830815, 50.960178));
        latLngs.add(new LatLng(35.830223, 50.960510));
        latLngs.add(new LatLng(35.829771, 50.961669));
        latLngs.add(new LatLng(35.829397, 50.961530));
        latLngs.add(new LatLng(35.829119, 50.960725));
        return latLngs;
    }//_____________________________________________________________________________________________ End GetWorkingRange

}
