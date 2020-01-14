package com.ngra.trafficcontroller.utility.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.location.LocationRequest;
import com.ngra.trafficcontroller.dagger.retrofit.RetrofitComponent;
import com.ngra.trafficcontroller.dagger.retrofit.RetrofitModule;
import com.ngra.trafficcontroller.database.DataBaseLocation;
import com.ngra.trafficcontroller.database.DataBaseLog;
import com.ngra.trafficcontroller.models.ModelLocation;
import com.ngra.trafficcontroller.models.ModelLocations;
import com.ngra.trafficcontroller.models.ModelResponcePrimery;
import com.ngra.trafficcontroller.models.Model_Result;
import com.ngra.trafficcontroller.utility.DeviceTools;

import com.ngra.trafficcontroller.views.application.TrafficController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.realm.Realm;
import io.realm.RealmResults;
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ngra.trafficcontroller.utility.StaticFunctions.CheckResponse;
import static com.ngra.trafficcontroller.utility.StaticFunctions.GetAuthorization;
import static com.ngra.trafficcontroller.views.application.TrafficController.ObservablesGpsAndNetworkChange;

public class ReceiverJobInBackground extends BroadcastReceiver {

    private Context context;
    private RealmResults<DataBaseLocation> locations;
    private boolean GetGPS;

    @Override
    public void onReceive(Context context, Intent intent) {//_______________________________________ Start GetCurrentLocation

        this.context = context;
        SaveLog("Start : " + getStringCurrentDate());

        if (TrafficController.getApplication(context).isLocationEnabled())
            GetCurrentLocation();
        else {

            SaveLog("GPS OFF : " + getStringCurrentDate());
            GetLocatointonFromDB();
        }

        DeleteOldLocationFromDataBase();

    }//_____________________________________________________________________________________________ End GetCurrentLocation


    private void SaveLog(String log) {//____________________________________________________________ Start SaveLog
        Realm realm = TrafficController
                .getApplication(context)
                .getRealmComponent()
                .getRealm();

        realm.beginTransaction();
        realm.createObject(DataBaseLog.class)
                .insert(log);
        realm.commitTransaction();
    }//_____________________________________________________________________________________________ End SaveLog


    private void GetCurrentLocation() {//___________________________________________________________ Start GetCurrentLocation

        GetGPS = false;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!GetGPS)
                    GetLocatointonFromDB();
            }
        }, 10000);

//        final int[] count = {0};
        LocationRequest request = LocationRequest.create() //standard GMS LocationRequest
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setNumUpdates(1)
                .setInterval(1000);

        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(context);
        Disposable subscription = locationProvider.getUpdatedLocation(request)
                .subscribe(new Consumer<Location>() {
                    @Override
                    public void accept(Location location) throws Exception {
//                        count[0] = count[0] + 1;
                        //                       if(count[0] == 5) {
                        GetGPS = true;
                        SaveLog("Get GPS RX : " + location.getLatitude() + "," + location.getLongitude() + " - " + getStringCurrentDate());
                        SaveToDataBase(
                                location.getLatitude(),
                                location.getLongitude(),
                                location.getAltitude(),
                                location.getSpeed()
                        );
//                        }
                    }
                });
    }//_____________________________________________________________________________________________ End GetCurrentLocation


    private void SaveToDataBase(
            double Latitude,
            double Longitude,
            double Altitude,
            float Speed) {//________________________________________________________________________ StartSaveToDataBase

        Realm realm = TrafficController
                .getApplication(context)
                .getRealmComponent()
                .getRealm();

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
            realm.createObject(DataBaseLocation.class, ID)
                    .InsertDataBaseLocation(Latitude, Longitude, time, Altitude, Speed, date);
            realm.commitTransaction();

        } catch (Exception ex) {
            realm.cancelTransaction();
        }

        SharedPreferences.Editor perf =
                context.getSharedPreferences("trafficcontroller", 0).edit();
        String time = getStringCurrentDate();
        perf.putString("lastgps", time);
        perf.apply();
        GetLocatointonFromDB();
        ObservablesGpsAndNetworkChange.onNext("LastGPS");
    }//_____________________________________________________________________________________________ End StartSaveToDataBase


    public String getStringCurrentDate() {//_______________________________________________________ Start getStringCurrentDate
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        return simpleDateFormat.format(new Date());
    }//_____________________________________________________________________________________________ End getStringCurrentDate


    private void GetLocatointonFromDB() {//_________________________________________________________ Start GetLocatointonFromDB

        Realm realm = TrafficController
                .getApplication(context)
                .getRealmComponent()
                .getRealm();

        locations = realm.where(DataBaseLocation.class).equalTo("Send", false).findAll();
        SaveLog("Get DB : " + locations.size() + "-- " + getStringCurrentDate());
        if (locations.size() > 0)
            SendLocatoinToServer();
    }//_____________________________________________________________________________________________ End GetLocatointonFromDB


    private void SendLocatoinToServer() {//_________________________________________________________ Start SendLocatoinToServer

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        ArrayList<ModelLocation> list = new ArrayList<>();
        for (DataBaseLocation location : locations) {
            list.add(new ModelLocation(location.getLatitude(),location.getLongitude(),location.getAltitude(),location.getSpeed(),location.getSaveDate()));
            JSONObject temp = new JSONObject();
            try {
                temp.put("latitude", location.getLatitude());
                temp.put("longitude", location.getLongitude());
                temp.put("altitude", location.getAltitude());
                temp.put("speed", location.getSpeed());
                temp.put("time", location.getTime());
                jsonArray.put(temp);
            } catch (JSONException ex) {

            }

        }

        ModelLocations lo = new ModelLocations(list);

//        try {
//            jsonObject.put("locations", list);
//        } catch (JSONException ex) {
//            return;
//        }

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
                        if (response.body() != null) {
                            Realm realm = TrafficController
                                    .getApplication(context)
                                    .getRealmComponent().getRealm();

                            for (DataBaseLocation location : locations) {
                                try {
                                    realm.beginTransaction();
                                    location.setSend(true);
                                    realm.commitTransaction();
                                } catch (Exception ex) {

                                }
                            }
                            SharedPreferences.Editor perf =
                                    context.getSharedPreferences("trafficcontroller", 0).edit();
                            String time = getStringCurrentDate();
                            perf.putString("lastnet", time);
                            perf.apply();
                            ObservablesGpsAndNetworkChange.onNext("LastNet");
                        }
                    }

                    @Override
                    public void onFailure(Call<ModelResponcePrimery> call, Throwable t) {
                        SaveLog("Response Failure : " + getStringCurrentDate());
                    }
                });

//                .enqueue(new Callback<Model_Result>() {
//                    @Override
//                    public void onResponse(Call<Model_Result> call, Response<Model_Result> response) {
//                        if (response != null) {
//                            if (response.body() != null) {
//                                String result = response.body().getResult();
//                                SaveLog("Response : " + result + " -- " + getStringCurrentDate());
//                                if (result.equalsIgnoreCase("done")) {
//                                    Realm realm = TrafficController
//                                            .getApplication(context)
//                                            .getRealmComponent().getRealm();
//
//                                    for (DataBaseLocation location : locations) {
//                                        try {
//                                            realm.beginTransaction();
//                                            location.setSend(true);
//                                            realm.commitTransaction();
//                                        } catch (Exception ex) {
//
//                                        }
//                                    }
//                                    SharedPreferences.Editor perf =
//                                            context.getSharedPreferences("trafficcontroller", 0).edit();
//                                    String time = getStringCurrentDate();
//                                    perf.putString("lastnet", time);
//                                    perf.apply();
//                                    ObservablesGpsAndNetworkChange.onNext("LastNet");
//                                }
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<Model_Result> call, Throwable t) {
//                        SaveLog("Response Failure : " + getStringCurrentDate());
//                    }
//                });


    }//_____________________________________________________________________________________________ End SendLocatoinToServer


    private void DeleteOldLocationFromDataBase() {//________________________________________________ Start DeleteOldLocationFromDataBase
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DATE, -11);

        Realm realm = TrafficController
                .getApplication(context)
                .getRealmComponent()
                .getRealm();

        RealmResults<DataBaseLocation> delete = realm
                .where(DataBaseLocation.class)
                .equalTo("Send", true)
                .and()
                .lessThanOrEqualTo("SaveDate", now.getTime())
                .findAll();

        realm.beginTransaction();
        delete.deleteAllFromRealm();
        realm.commitTransaction();

    }//_____________________________________________________________________________________________ End DeleteOldLocationFromDataBase


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
