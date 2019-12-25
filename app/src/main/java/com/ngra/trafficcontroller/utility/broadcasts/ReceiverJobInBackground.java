package com.ngra.trafficcontroller.utility.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

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

public class ReceiverJobInBackground extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        File file = new File(context.getFilesDir(),"config.txt");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd _ HH:mm:ss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());

        if(file.exists()){
            String text = readFromFile(context);
            text = text + "\n" + " T***** " + currentDateandTime;
            writeToFile(text, context);
        } else {
            String text = " T***** " + currentDateandTime;
            writeToFile(text, context);
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            context.sendBroadcast(new Intent(context, ReceiverLunchAppInBackground.class).setAction("ir.ngra.Lunch"));
//        } else {
//            Intent i = new Intent("ir.ngra.Lunch");
//            context.sendBroadcast(i);
//        }
    }



    private void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }



    private String readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("config.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }



}
