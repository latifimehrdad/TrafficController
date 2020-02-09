package com.ngra.trafficcontroller.backgroundservice.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;


public class GetCurrentLocation extends Service implements LocationListener {

    private LocationManager locationManager;
    public static LSInterface lsInterface;
    public Context mcontext;
    public int count = 0;


    public interface LSInterface {//________________________________________________________________ Start INTGetDataService

        void GetLatLong(double lat, double lon);

        void ShowAlertLocation();

    }//_____________________________________________________________________________________________ End INTGetDataService


    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {//____________________________ Start onStartCommand
        count = 0;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        int b = isLocationEnabled();
        if (b == 1) {

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, GetCurrentLocation.this);

        } else if (b == 2) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, GetCurrentLocation.this);
        } else {
            lsInterface.ShowAlertLocation();
        }
        return START_STICKY;
    }//_____________________________________________________________________________________________ End onStartCommand


    @Override
    public IBinder onBind(Intent intent) {//________________________________________________________ Start onBind

        return null;
    }//_____________________________________________________________________________________________ End onBind


    @Override
    public void onLocationChanged(Location location) {//____________________________________________ Start onLocationChanged

        lsInterface.ShowAlertLocation();
        try {
            if (count > 10) {
                lsInterface.GetLatLong(location.getLatitude(), location.getLongitude());
                locationManager.removeUpdates(GetCurrentLocation.this);
                locationManager = null;
                this.stopSelf();
            } else {
                count = count + 1;
                lsInterface.ShowAlertLocation();
            }
        } catch (Throwable t) {

        }

    }//_____________________________________________________________________________________________ End onLocationChanged


    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {//_________________________________ Start onStatusChanged

    }//_____________________________________________________________________________________________ End onStatusChanged


    @Override
    public void onProviderEnabled(String s) {//_____________________________________________________ Start onProviderEnabled

    }//_____________________________________________________________________________________________ End onProviderEnabled


    @Override
    public void onProviderDisabled(String s) {//____________________________________________________ Start onProviderDisabled

    }//_____________________________________________________________________________________________ End onProviderDisabled


    private int isLocationEnabled() {//_____________________________________________________________ Start isLocationEnabled

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            return 1;
        else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            return 2;
        else
            return 0;
    }//_____________________________________________________________________________________________ End isLocationEnabled


}