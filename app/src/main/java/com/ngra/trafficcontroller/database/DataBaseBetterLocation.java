package com.ngra.trafficcontroller.database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class DataBaseBetterLocation extends RealmObject {

    @PrimaryKey
    private Integer ID;
    boolean IsGPS;
    private double Latitude;
    private double Longitude;
    private double Altitude;
    private float Speed;
    private float Accuracy;
    private long LocTime;


    public void InsertDB(
            boolean isGPS,
            double latitude,
            double longitude,
            double altitude,
            float speed,
            float accuracy,
            long time) {//________________________________________________________________________ Start InsertDB
        IsGPS = isGPS;
        Latitude = latitude;
        Longitude = longitude;
        Altitude = altitude;
        Speed = speed;
        Accuracy = accuracy;
        LocTime = time;
    }//_____________________________________________________________________________________________ End InsertDB


    public boolean isGPS() {
        return IsGPS;
    }

    public void setGPS(boolean GPS) {
        IsGPS = GPS;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public double getAltitude() {
        return Altitude;
    }

    public void setAltitude(double altitude) {
        Altitude = altitude;
    }

    public float getSpeed() {
        return Speed;
    }

    public void setSpeed(float speed) {
        Speed = speed;
    }

    public float getAccuracy() {
        return Accuracy;
    }

    public void setAccuracy(float accuracy) {
        Accuracy = accuracy;
    }

    public long getLocTime() {
        return LocTime;
    }

    public void setLocTime(long locTime) {
        LocTime = locTime;
    }
}
