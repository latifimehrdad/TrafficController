package com.ngra.trafficcontroller.models;

import java.util.Date;

public class ModelLocation {

    double Latitude;
    double Longitude;
    double Altitude;
    float Speed;
    Date ClientDate;

    public ModelLocation(double latitude, double longitude, double altitude, float speed, Date clientDate) {
        Latitude = latitude;
        Longitude = longitude;
        Altitude = altitude;
        Speed = speed;
        ClientDate = clientDate;
    }
}
