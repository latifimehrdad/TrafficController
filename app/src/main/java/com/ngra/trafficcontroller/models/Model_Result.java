package com.ngra.trafficcontroller.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Model_Result {


    @SerializedName("result")
    String result;

    @SerializedName("error")
    @Expose
    private String error;

    @SerializedName("code")
    @Expose
    private String code;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
