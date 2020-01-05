package com.ngra.trafficcontroller.database;

import io.realm.RealmObject;

public class DataBaseLog extends RealmObject {

    String LogString;

    public void insert(String log){
        LogString = log;
    }

    public String getLogString() {
        return LogString;
    }

    public void setLogString(String logString) {
        LogString = logString;
    }
}
