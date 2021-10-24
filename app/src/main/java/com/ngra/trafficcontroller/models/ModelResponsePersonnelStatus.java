package com.ngra.trafficcontroller.models;

import com.google.gson.annotations.SerializedName;

public class ModelResponsePersonnelStatus extends ModelResponcePrimery{
    @SerializedName("result")
    ModelStatus result;

    public ModelResponsePersonnelStatus(ModelStatus result) {
        this.result = result;
    }

    public ModelStatus getResult() {
        return result;
    }
}
