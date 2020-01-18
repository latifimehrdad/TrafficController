package com.ngra.trafficcontroller.backgroundservice.broadcasts;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;
import com.ngra.trafficcontroller.R;
import com.ngra.trafficcontroller.backgroundservice.services.ServiceSetTimeForLunchApp;
import com.ngra.trafficcontroller.database.DataBaseBetterLocation;
import com.ngra.trafficcontroller.utility.MehrdadLatifiMap;
import com.ngra.trafficcontroller.utility.NotificationManagerClass;
import com.ngra.trafficcontroller.dagger.retrofit.RetrofitComponent;
import com.ngra.trafficcontroller.dagger.retrofit.RetrofitModule;
import com.ngra.trafficcontroller.database.DataBaseLocation;
import com.ngra.trafficcontroller.database.DataBaseLog;
import com.ngra.trafficcontroller.models.ModelLocation;
import com.ngra.trafficcontroller.models.ModelLocations;
import com.ngra.trafficcontroller.models.ModelResponcePrimery;
import com.ngra.trafficcontroller.utility.DeviceTools;

import com.ngra.trafficcontroller.views.application.TrafficController;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ngra.trafficcontroller.utility.StaticFunctions.CheckResponse;
import static com.ngra.trafficcontroller.utility.StaticFunctions.GetAuthorization;
import static com.ngra.trafficcontroller.views.application.TrafficController.ObservablesGpsAndNetworkChange;

public class ReceiverJobInBackground extends BroadcastReceiver {

    private Context context;
    private RealmResults<DataBaseLocation> locations;


    public LocationManager locationManager;
    public Location previousBestLocation = null;
    public MyLocationListener listener;
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private Realm realm;


    @Override
    public void onReceive(Context context, Intent intent) {//_______________________________________ Start GetCurrentLocation

        this.context = context;

        realm = TrafficController
                .getApplication(context)
                .getRealmComponent()
                .getRealm();

        SaveLog("onReceive : " + getStringCurrentDate());

        SharedPreferences.Editor perf =
                context.getSharedPreferences("trafficcontroller", 0).edit();
        String time = getStringCurrentDate();
        perf.putString("lasttime", time);
        perf.apply();

        try {
            RealmResults<DataBaseBetterLocation> locations =
                    realm.where(DataBaseBetterLocation.class).findAll();
            realm.beginTransaction();
            locations.deleteAllFromRealm();
            realm.commitTransaction();
            locations = null;
        } catch (RealmException e) {
            realm.cancelTransaction();
        }


        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        } else {
            locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
            listener = new MyLocationListener();
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, (LocationListener) listener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, listener);
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                locationManager.removeUpdates(listener);
                GetCurrentLocation();
            }
        }, 30 * 1000);


//
//        SaveLog("Start : " + getStringCurrentDate());
//
//        if (TrafficController.getApplication(context).isLocationEnabled())
//            GetCurrentLocation();
//        else {
//
//            SaveLog("GPS OFF : " + getStringCurrentDate());
//            GetLocatointonFromDB();
//        }
//
//        DeleteOldLocationFromDataBase();

    }//_____________________________________________________________________________________________ End GetCurrentLocation


    public class MyLocationListener implements LocationListener {//_________________________________ Start MyLocationListener

        public void onLocationChanged(final Location loc) {
            Log.i("meri", "onLocationChanged");
            if (isBetterLocation(loc, previousBestLocation)) {

                try {
                    Integer ID = 1;
                    Number currentIdNum = realm.where(DataBaseBetterLocation.class).max("ID");
                    if (currentIdNum == null) {
                        ID = 1;
                    } else {
                        ID = currentIdNum.intValue() + 1;
                    }
                    boolean isGPS = false;
                    if (loc.getProvider().equalsIgnoreCase("gps"))
                        isGPS = true;

                    realm.beginTransaction();
                    realm.createObject(DataBaseBetterLocation.class, ID)
                            .InsertDB(
                                    isGPS,
                                    loc.getLatitude(),
                                    loc.getLongitude(),
                                    loc.getAltitude(),
                                    loc.getSpeed(),
                                    loc.getAccuracy()
                            );
                    realm.commitTransaction();
                } catch (RealmException e) {
                    realm.cancelTransaction();
                }
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        public void onProviderDisabled(String provider) {

        }


        public void onProviderEnabled(String provider) {

        }
    }//_____________________________________________________________________________________________ End MyLocationListener


    private void SaveLog(String log) {//____________________________________________________________ Start SaveLog
        try {
            realm.beginTransaction();
            realm.createObject(DataBaseLog.class)
                    .insert(log);
            realm.commitTransaction();
        } catch (RealmException e) {
            realm.cancelTransaction();
        }

    }//_____________________________________________________________________________________________ End SaveLog


    private void GetCurrentLocation() {//___________________________________________________________ Start GetCurrentLocation

        RealmResults<DataBaseBetterLocation> locations = realm
                .where(DataBaseBetterLocation.class)
                .sort("ID")
                .findAll();

        if (locations.size() == 0)
            GetLocatointonFromDB();
        else {
            boolean isGPS = false;
            int i;
            int count = locations.size() - 1;
            for (i = count; i > -1; i--) {
                if (locations.get(i).isGPS()) {
                    isGPS = true;
                    break;
                }
            }

            if (isGPS) {
                SaveToDataBase(
                        locations.get(i).getLatitude(),
                        locations.get(i).getLongitude(),
                        locations.get(i).getAltitude(),
                        locations.get(i).getSpeed(),
                        true,
                        locations.get(i).getAccuracy()
                );
            } else {
                SaveToDataBase(
                        locations.get(count).getLatitude(),
                        locations.get(count).getLongitude(),
                        locations.get(count).getAltitude(),
                        locations.get(count).getSpeed(),
                        false,
                        locations.get(count).getAccuracy()
                );
            }
            locations = null;
            SaveLog("Getl Location : " + isGPS + " -- " + getStringCurrentDate());
        }


//        GetGPS = false;
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (!GetGPS)
//                    GetLocatointonFromDB();
//            }
//        }, 10000);
//
//

//        final int[] count = {0};
//
//        LocationRequest request = LocationRequest.create() //standard GMS LocationRequest
//                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//                .setNumUpdates(1)
//                .setInterval(1000);
//
//        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(context);
//        Disposable subscription = locationProvider.getUpdatedLocation(request)
//                .subscribe(new Consumer<Location>() {
//                    @Override
//                    public void accept(Location location) throws Exception {
////                        count[0] = count[0] + 1;
//                        //                       if(count[0] == 5) {
//                        GetGPS = true;
//                        SaveLog("Get GPS RX : " + location.getLatitude() + "," + location.getLongitude() + " - " + getStringCurrentDate());
//                        SaveToDataBase(
//                                location.getLatitude(),
//                                location.getLongitude(),
//                                location.getAltitude(),
//                                location.getSpeed()
//                        );
////                        }
//                        CheckPointInWorkingRange(location);
//                    }
//                });
    }//_____________________________________________________________________________________________ End GetCurrentLocation


    private void SaveToDataBase(
            double Latitude,
            double Longitude,
            double Altitude,
            float Speed,
            boolean isGPS,
            float Accuracy) {//________________________________________________________________________ StartSaveToDataBase

        Date last = realm.where(DataBaseLocation.class).maximumDate("SaveDate");
        long bet = 2 * 60 * 1000 + 1;
        Date date = new Date();
        if (last != null)
            bet = Math.abs(last.getTime() - date.getTime());

        if (bet < 2 * 60 * 1000)
            return;


        try {
            Integer ID = 1;
            Number currentIdNum = realm.where(DataBaseLocation.class).max("ID");
            if (currentIdNum == null) {
                ID = 1;
            } else {
                ID = currentIdNum.intValue() + 1;
            }

            String time = getStringCurrentDate();
            realm.beginTransaction();
            realm.createObject(DataBaseLocation.class, ID).InsertDataBaseLocation(
                    Latitude,
                    Longitude,
                    time,
                    Altitude,
                    Speed,
                    date,
                    isGPS,
                    Accuracy);
            realm.commitTransaction();
            SharedPreferences.Editor perf =
                    context.getSharedPreferences("trafficcontroller", 0).edit();
            perf.putString("lastgps", time);
            perf.apply();
            CheckPointInWorkingRange(Latitude, Longitude);
            GetLocatointonFromDB();
            ObservablesGpsAndNetworkChange.onNext("LastGPS");

        } catch (RealmException ex) {
            realm.cancelTransaction();
        } catch (Exception e) {

        }


    }//_____________________________________________________________________________________________ End StartSaveToDataBase


    public String getStringCurrentDate() {//_______________________________________________________ Start getStringCurrentDate
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        return simpleDateFormat.format(new Date());
    }//_____________________________________________________________________________________________ End getStringCurrentDate


    private void GetLocatointonFromDB() {//_________________________________________________________ Start GetLocatointonFromDB
        try {
            locations = realm.where(DataBaseLocation.class).equalTo("Send", false).findAll();
            SaveLog("Get DB : " + locations.size() + "-- " + getStringCurrentDate());
            if (locations.size() > 0)
                SendLocatoinToServer();
        } catch (RealmException e) {

        }
    }//_____________________________________________________________________________________________ End GetLocatointonFromDB


    private void SendLocatoinToServer() {//_________________________________________________________ Start SendLocatoinToServer

        ArrayList<ModelLocation> list = new ArrayList<>();
        for (DataBaseLocation location : locations) {
            list.add(new ModelLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    location.getAltitude(),
                    location.getSpeed(),
                    location.getSaveDate(),
                    location.getAccuracy(),
                    location.isGPS()));
        }

        ModelLocations lo = new ModelLocations(list);

        RetrofitModule.isCancel = false;
        RetrofitComponent retrofitComponent =
                TrafficController
                        .getApplication(context)
                        .getRetrofitComponent();

        DeviceTools deviceTools = new DeviceTools(context);
        String imei = deviceTools.getIMEI();
        String Authorization = GetAuthorization(context);

        retrofitComponent
                .getRetrofitApiInterface()
                .DeviceLogs(
                        imei,
                        Authorization,
                        lo
                )
                .enqueue(new Callback<ModelResponcePrimery>() {
                    @Override
                    public void onResponse(Call<ModelResponcePrimery> call, Response<ModelResponcePrimery> response) {
                        String MessageResponcse = CheckResponse(response, true);
                        if (MessageResponcse == null) {
                            for (DataBaseLocation location : locations) {
                                try {
                                    realm.beginTransaction();
                                    location.setSend(true);
                                    realm.commitTransaction();
                                } catch (RealmException ex) {
                                    realm.cancelTransaction();
                                }
                            }
                            SharedPreferences.Editor perf =
                                    context.getSharedPreferences("trafficcontroller", 0).edit();
                            String time = getStringCurrentDate();
                            perf.putString("lastnet", time);
                            perf.apply();
                            ObservablesGpsAndNetworkChange.onNext("LastNet");
                            SaveLog("Response Done : " + getStringCurrentDate());
                            DeleteOldLocationFromDataBase();
                        }
                    }

                    @Override
                    public void onFailure(Call<ModelResponcePrimery> call, Throwable t) {
                        SaveLog("Response Failure : " + getStringCurrentDate());
                    }
                });

    }//_____________________________________________________________________________________________ End SendLocatoinToServer


    private void DeleteOldLocationFromDataBase() {//________________________________________________ Start DeleteOldLocationFromDataBase
        try {
            Calendar now = Calendar.getInstance();
            now.add(Calendar.DATE, -11);

            RealmResults<DataBaseLocation> delete = realm
                    .where(DataBaseLocation.class)
                    .equalTo("Send", true)
                    .and()
                    .lessThanOrEqualTo("SaveDate", now.getTime())
                    .findAll();

            realm.beginTransaction();
            delete.deleteAllFromRealm();
            realm.commitTransaction();
            delete = null;
        } catch (RealmException e) {
            realm.cancelTransaction();
        }

    }//_____________________________________________________________________________________________ End DeleteOldLocationFromDataBase


    private void CheckPointInWorkingRange(double Latitude, double Longitude) {//____________________ Start CheckPointInWorkingRange()
        List<LatLng> latLngs = new ArrayList<>();
        latLngs.add(new LatLng(35.831420, 50.959401));
        latLngs.add(new LatLng(35.830141, 50.963637));
        latLngs.add(new LatLng(35.829315, 50.963046));
        latLngs.add(new LatLng(35.829080, 50.963432));
        latLngs.add(new LatLng(35.826106, 50.961197));
        latLngs.add(new LatLng(35.826665, 50.958141));
        latLngs.add(new LatLng(35.831420, 50.959401));
        LatLng latLng = new LatLng(Latitude, Longitude);
        boolean in = MehrdadLatifiMap.MlMap_isInside(latLng, latLngs);
        SharedPreferences prefs = context.getSharedPreferences("trafficcontroller", 0);
        boolean InterWorkingRange = false;
        if (prefs != null)
            InterWorkingRange = prefs.getBoolean("interworkingwange", false);

        if (in) {
            if (!InterWorkingRange)
                ShowNotification(true);
        } else {
            if (InterWorkingRange)
                ShowNotification(false);
        }

    }//_____________________________________________________________________________________________ End CheckPointInWorkingRange()


    private void ShowNotification(boolean inter) {//________________________________________________ Start ShowNotification
        SharedPreferences.Editor perf =
                context.getSharedPreferences("trafficcontroller", 0).edit();
        perf.putBoolean("interworkingwange", inter);
        perf.apply();

        if (inter) {
            NotificationManagerClass managerClass =
                    new NotificationManagerClass(
                            context,
                            context.getResources().getString(R.string.EnterToWorkingRange)
                            , false
                            , 4
                    );
        } else {
            NotificationManagerClass managerClass =
                    new NotificationManagerClass(
                            context,
                            context.getResources().getString(R.string.ExitToWorkingRange)
                            , false
                            , 5
                    );
        }
    }//_____________________________________________________________________________________________ End ShowNotification


    private boolean isSameProvider(String provider1, String provider2) {//__________________________ Start isSameProvider
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }//_____________________________________________________________________________________________ End isSameProvider


    protected boolean isBetterLocation(Location location, Location currentBestLocation) {//_________ Start isBetterLocation
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }//_____________________________________________________________________________________________ End isBetterLocation


//    private void LogService(String test) {//________________________________________________________ Start SentLocatointoServer
//        File file = new File(context.getFilesDir(), "config.txt");
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd _ HH:mm:ss", Locale.getDefault());
//        String currentDateandTime = sdf.format(new Date());
//
//        if (file.exists()) {
//            String text = readFromFile(context);
//            text = text + System.getProperty("line.separator") + test + currentDateandTime ;
//            writeToFile(text, context);
//        } else {
//            String text = " T***** " + currentDateandTime;
//            writeToFile(text, context);
//        }
//    }//_____________________________________________________________________________________________ End SentLocatointoServer


    private void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


    private String readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("config.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }


}
