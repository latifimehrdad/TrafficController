package com.ngra.trafficcontroller.viewmodels.activitys;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;
import com.ngra.trafficcontroller.database.DataBaseLocation;
import com.ngra.trafficcontroller.models.ModelChartMeasureDistance;
import com.ngra.trafficcontroller.utility.ApplicationUtility;
import com.ngra.trafficcontroller.utility.MehrdadLatifiMap;
import com.ngra.trafficcontroller.views.application.TrafficController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

public class ViewModel_MainActivity {

    private Context context;

    public ViewModel_MainActivity(Context context) {//______________________________________________ Start ViewModel_MainActivity
        this.context = context;
    }//_____________________________________________________________________________________________ End ViewModel_MainActivity


    public ArrayList<ModelChartMeasureDistance> getModelChartMeasureDistances() {//_________________ Start getModelChartMeasureDistances

        ApplicationUtility utility = new ApplicationUtility();

        ArrayList<ModelChartMeasureDistance> result = new ArrayList<>();

        Calendar calendarStart = Calendar.getInstance();
        calendarStart.add(Calendar.DATE, -9);
        calendarStart.set(Calendar.HOUR_OF_DAY, 00);
        calendarStart.set(Calendar.MINUTE, 00);
        calendarStart.set(Calendar.SECOND, 00);

        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.add(Calendar.DATE, -8);
        calendarEnd.set(Calendar.HOUR_OF_DAY, 00);
        calendarEnd.set(Calendar.MINUTE, 00);
        calendarEnd.set(Calendar.SECOND, 00);


        Realm realm = TrafficController
                .getApplication(context)
                .getRealmComponent()
                .getRealm();

        for (int i = 0; i < 10; i++) {
            RealmResults<DataBaseLocation> locations =
                    realm.where(DataBaseLocation.class)
                            //.lessThanOrEqualTo("SaveDate", calendarEnd.getTime())
                            //.and()
                            //.greaterThanOrEqualTo("SaveDate", calendarStart.getTime())
                            .between("SaveDate", calendarStart.getTime(), calendarEnd.getTime())
                            .findAll();
            float data = CalculatorMeasuredistance(locations);
            data = data / 1000;
            result.add(new ModelChartMeasureDistance(
                    utility.MiladiToJalali(calendarStart.getTime(), "FullJalaliNumber"),
                    data
            ));

            calendarStart.add(Calendar.DATE, 1);
            calendarEnd.add(Calendar.DATE, 1);
        }

        return result;

    }//_____________________________________________________________________________________________ End getModelChartMeasureDistances


    public Integer getLocationsForMeasureDistance() {//_____________________________________________ Start getLocationsForMeasureDistance

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 01);

        Realm realm = TrafficController
                .getApplication(context)
                .getRealmComponent()
                .getRealm();

        RealmResults<DataBaseLocation> locations = realm
                .where(DataBaseLocation.class)
                .greaterThanOrEqualTo("SaveDate", calendar.getTime())
                .sort("SaveDate")
                .findAll();

        Integer MD = CalculatorMeasuredistance(locations);

        return MD;
    }//_____________________________________________________________________________________________ End getLocationsForMeasureDistance


    private Integer CalculatorMeasuredistance(RealmResults<DataBaseLocation> locations) {//_________ Start CalculatorMeasuredistance

        if (locations.size() < 2) {
            return 0;
        }
        float MeasuteDistance = 0;
        MehrdadLatifiMap latifiMap = new MehrdadLatifiMap();

        for (int i = 0; i < locations.size() - 1; i++) {
            LatLng old = new LatLng(
                    locations.get(i).getLatitude(),
                    locations.get(i).getLongitude());

            LatLng New = new LatLng(
                    locations.get(i + 1).getLatitude(),
                    locations.get(i + 1).getLongitude());

            float[] results = latifiMap.MeasureDistance(old, New);

            if (results[0] > 0)
                MeasuteDistance = MeasuteDistance + results[0];
        }

        Integer MD = Math.round(MeasuteDistance);

        return MD;
    }//_____________________________________________________________________________________________ End CalculatorMeasuredistance


    public String GetLastGPS() {//__________________________________________________________________ Start GetLastGPS
        String ret = "0000/00/00 - 00:00";
        SharedPreferences prefs = context.getSharedPreferences("trafficcontroller", 0);
        if (prefs != null) {
            String GPS = prefs.getString("lastgps", "null");
            if (!GPS.equalsIgnoreCase("null")) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                try {
                    Date date = simpleDateFormat.parse(GPS);
                    ApplicationUtility applicationUtility = new ApplicationUtility();
                    ret = applicationUtility.MiladiToJalali(date, "FullJalaliNumber");
                    simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.US);
                    ret = ret + " - " + simpleDateFormat.format(date);
                    return ret;
                } catch (ParseException e) {
                    e.printStackTrace();
                    return ret;
                }
            }
            return ret;
        }
        return ret;
    }//_____________________________________________________________________________________________ End GetLastGPS


    public String GetLastNet() {//__________________________________________________________________ Start GetLastNet
        String ret = "0000/00/00 - 00:00";
        SharedPreferences prefs = context.getSharedPreferences("trafficcontroller", 0);
        if (prefs != null) {
            String GPS = prefs.getString("lastnet", "null");
            if (!GPS.equalsIgnoreCase("null")) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                try {
                    Date date = simpleDateFormat.parse(GPS);
                    ApplicationUtility applicationUtility = new ApplicationUtility();
                    ret = applicationUtility.MiladiToJalali(date, "FullJalaliNumber");
                    simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.US);
                    ret = ret + " - " + simpleDateFormat.format(date);
                    return ret;
                } catch (ParseException e) {
                    e.printStackTrace();
                    return ret;
                }
            }
            return ret;
        }
        return ret;
    }//_____________________________________________________________________________________________ End GetLastNet


}
