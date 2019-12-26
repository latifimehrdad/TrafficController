package com.ngra.trafficcontroller.database;


import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class DataBaseLocation extends RealmObject {

    @PrimaryKey
    private Integer ID;
    private double Latitude;
    private double Longitude;
    private Date Time;
    private double Altitude;
    private float Speed;

    public void InsertDataBaseLocation(
            double latitude,
            double longitude,
            Date time,
            double altitude,
            float speed) {//________________________________________________________________________ Start DataBaseLocation
        Latitude = latitude;
        Longitude = longitude;
        Time = time;
        Altitude = altitude;
        Speed = speed;
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

    public Date getTime() {
        return Time;
    }

    public void setTime(Date time) {
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
}
