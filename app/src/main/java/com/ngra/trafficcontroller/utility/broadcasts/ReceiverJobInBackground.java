package com.ngra.trafficcontroller.utility.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.ngra.trafficcontroller.database.DataBaseLocation;
import com.ngra.trafficcontroller.views.activitys.MainActivity;
import com.ngra.trafficcontroller.views.application.TrafficController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.realm.Realm;
import io.realm.RealmResults;
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider;

public class ReceiverJobInBackground extends BroadcastReceiver {

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {//_______________________________________ Start GetCurrentLocation

        this.context = context;
        GetCurrentLocation();
        //SentLocatointoServer();

    }//_____________________________________________________________________________________________ End GetCurrentLocation


    private void GetCurrentLocation() {//___________________________________________________________ Start GetCurrentLocation

        LocationRequest request = LocationRequest.create() //standard GMS LocationRequest
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setNumUpdates(1)
                .setInterval(1000);
        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(context);
        Disposable subscription = locationProvider.getUpdatedLocation(request)
                .subscribe(new Consumer<Location>() {
                    @Override
                    public void accept(Location location) throws Exception {
                        SaveToDataBase(
                                location.getLatitude(),
                                location.getLongitude(),
                                location.getAltitude(),
                                location.getSpeed()
                        );
                    }
                });
    }//_____________________________________________________________________________________________ End GetCurrentLocation


    private void SaveToDataBase(double Latitude, double Longitude, double Altitude, float Speed) {// StartSaveToDataBase
        Realm realm = TrafficController
                .getApplication(context)
                .getRealmComponent()
                .getRealm();

        try {
            Integer ID = 1;
            Number currentIdNum = realm.where(DataBaseLocation.class).max("ID");
            if (currentIdNum == null) {
                ID = 1;
            } else {
                ID = currentIdNum.intValue() + 1;
            }
            Date date = new Date();

            realm.beginTransaction();
            realm.createObject(DataBaseLocation.class, ID)
                    .InsertDataBaseLocation(Latitude, Longitude, date,Altitude, Speed);
            realm.commitTransaction();

        } catch (Exception ex) {
            realm.cancelTransaction();
        }


    }//_____________________________________________________________________________________________ End StartSaveToDataBase


    private void SentLocatointoServer() {//_________________________________________________________ Start SentLocatointoServer
        Realm realm = TrafficController
                .getApplication(context)
                .getRealmComponent()
                .getRealm();

        RealmResults<DataBaseLocation> locations = realm.where(DataBaseLocation.class).findAll();
        Log.i("meri", "fdsfdsfds");
    }//_____________________________________________________________________________________________ End SentLocatointoServer


    private void LogService() {//___________________________________________________________________ Start SentLocatointoServer
        File file = new File(context.getFilesDir(), "config.txt");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd _ HH:mm:ss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());

        if (file.exists()) {
            String text = readFromFile(context);
            text = text + "\n" + " T***** " + currentDateandTime;
            writeToFile(text, context);
        } else {
            String text = " T***** " + currentDateandTime;
            writeToFile(text, context);
        }
    }//_____________________________________________________________________________________________ End SentLocatointoServer


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
