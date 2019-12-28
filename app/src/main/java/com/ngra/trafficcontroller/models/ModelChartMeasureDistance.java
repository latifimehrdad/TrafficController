package com.ngra.trafficcontroller.models;

public class ModelChartMeasureDistance {

    String date;
    float measure;


    public ModelChartMeasureDistance(String date, float measure) {
        this.date = date;
        this.measure = measure;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getMeasure() {
        return measure;
    }

    public void setMeasure(float measure) {
        this.measure = measure;
    }
}
