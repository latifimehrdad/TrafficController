package com.ngra.trafficcontroller.dagger.retrofit;

import com.google.gson.JsonObject;
import com.ngra.trafficcontroller.models.ModelLocation;
import com.ngra.trafficcontroller.models.ModelLocations;
import com.ngra.trafficcontroller.models.ModelResponcePrimery;
import com.ngra.trafficcontroller.models.ModelToken;
import com.ngra.trafficcontroller.models.Model_Result;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface RetrofitApiInterface {


    String Version = "/api/v1";

    //______________________________________________________________________________________________ getToken
    @FormUrlEncoded
    @POST("/token")
    Call<ModelToken> getToken
            (
                    @Field("client_id") String client_id,
                    @Field("client_secret") String client_secret,
                    @Field("grant_type") String grant_type
            );


    //______________________________________________________________________________________________ SendPhoneNumber
    @FormUrlEncoded
    @POST(Version + "/deviceaccount/demandverificationcode")
    Call<ModelResponcePrimery> SendPhoneNumber
            (
                    @Field("DeviceNumber") String DeviceNumber,
                    @Field("Type") Integer Type,
                    @Field("DeviceSpecification") String DeviceSpecification,
                    @Header("Authorization") String Authorization
            );


    //______________________________________________________________________________________________ SendVerifyCode
    @FormUrlEncoded
    @POST(Version + "/account/confirmmobile")
    Call<ModelResponcePrimery> SendVerifyCode
            (
                    @Field("Mobile") String PhoneNumber,
                    @Field("Code") String Password,
                    @Header("Authorization") String Authorization
            );


    //______________________________________________________________________________________________ getLoginToken
    @FormUrlEncoded
    @POST("/token")
    Call<ModelToken> getLoginToken
            (
                    @Field("client_id") String client_id,
                    @Field("client_secret") String client_secret,
                    @Field("grant_type") String grant_type,
                    @Field("DeviceSpecification") String DeviceSpecification,
                    @Field("DeviceNumber") String DeviceNumber,
                    @Field("DeviceType") Integer DeviceType
            );



    //______________________________________________________________________________________________ DeviceLogs
    @POST(Version + "/deviceattendance/report")
    Call<ModelResponcePrimery> DeviceLogs
            (
                    @Header("DeviceSpecification") String DeviceSpecification,
                    @Header("Authorization") String Authorization,
                    @Body ModelLocations locations
            );


//    @FormUrlEncoded
//    @POST("Api.aspx?action=register")
//    Call<Model_Result> SendPhoneNumber
//            (
//                    @Field("phone") String PhoneNumber
//            );


//    @POST("Api.aspx?action=validate")
//    @FormUrlEncoded
//    Call<Model_Result> VerifyPhoneNumber
//            (
//                    @Field("phone") String phone,
//                    @Field("code") String code,
//                    @Field("imei") String imei
//            );

//    @POST("Api.aspx?action=sync_location")
//    @FormUrlEncoded
//    Call<Model_Result> SendLocation(
//            @Field("imei") String imei,
//            @Field("locationsJson") String locationsJson
//    );

}
