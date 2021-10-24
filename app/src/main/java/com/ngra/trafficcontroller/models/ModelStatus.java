package com.ngra.trafficcontroller.models;

import com.google.gson.annotations.SerializedName;

public class ModelStatus {

    @SerializedName("status")
    int status;

    public ModelStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
