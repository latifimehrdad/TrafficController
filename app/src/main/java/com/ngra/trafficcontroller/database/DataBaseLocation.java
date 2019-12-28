package com.ngra.trafficcontroller.database;


import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class DataBaseLocation extends RealmObject {

    @PrimaryKey
    private Integer ID;
    private double Latitude;
    private double Longitude;
    private String Time;
    private double Altitude;
    private float Speed;
    private Date SaveDate;
    private boolean Send = false;

    public void InsertDataBaseLocation(
            double latitude,
            double longitude,
            String time,
            double altitude,
            float speed,
            Date saveDate) {//________________________________________________________________________ Start DataBaseLocation
        Latitude = latitude;
        Longitude = longitude;
        Time = time;
        Altitude = altitude;
        Speed = speed;
        SaveDate = saveDate;
    }//_____________________________________________________________________________________________ End DataBaseLocation


    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
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

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
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

    public boolean isSend() {
        return Send;
    }

    public void setSend(boolean send) {
        Send = send;
    }

    public Date getSaveDate() {
        return SaveDate;
    }

    public void setSaveDate(Date saveDate) {
        SaveDate = saveDate;
    }
}
