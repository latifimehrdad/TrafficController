package com.ngra.trafficcontroller.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelToken {

    @SerializedName("access_token")
    private String access_token;
    @SerializedName("token_type")
    private String token_type;
    @SerializedName("expires_in")
    private Integer expires_in;
    @SerializedName("as:client_id")
    private String client_id;
    @SerializedName(".issued")
    private String issued;
    @SerializedName(".expires")
    private String expires;
    @SerializedName("userName")
    private String PhoneNumber;
    @SerializedName("aToken")
    private String aToken;

    @Expose
    @SerializedName("error_description")
    private String error_description;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public Integer getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(Integer expires_in) {
        this.expires_in = expires_in;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getIssued() {
        return issued;
    }

    public void setIssued(String issued) {
        this.issued = issued;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getError_description() {
        return error_description;
    }

    public String getaToken() {
        return aToken;
    }

    public void setaToken(String aToken) {
        this.aToken = aToken;
    }
}
