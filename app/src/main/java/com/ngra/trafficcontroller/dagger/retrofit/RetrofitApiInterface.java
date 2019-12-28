package com.ngra.trafficcontroller.dagger.retrofit;

import com.ngra.trafficcontroller.models.Model_Result;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RetrofitApiInterface {


    @FormUrlEncoded
    @POST("Api.aspx?action=register")
    Call<Model_Result> SendPhoneNumber
            (
                    @Field("phone") String PhoneNumber
            );


    @POST("Api.aspx?action=validate")
    @FormUrlEncoded
    Call<Model_Result> VerifyPhoneNumber
            (
                    @Field("phone") String phone,
                    @Field("code") String code,
                    @Field("imei") String imei
            );

    @POST("Api.aspx?action=sync_location")
    @FormUrlEncoded
    Call<Model_Result> SendLocation(
            @Field("imei") String imei,
            @Field("locationsJson") String locationsJson
    );

}
