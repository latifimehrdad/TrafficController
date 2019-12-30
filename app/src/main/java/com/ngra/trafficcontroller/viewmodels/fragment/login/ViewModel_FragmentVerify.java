package com.ngra.trafficcontroller.viewmodels.fragment.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import com.ngra.trafficcontroller.dagger.retrofit.RetrofitComponent;
import com.ngra.trafficcontroller.dagger.retrofit.RetrofitModule;
import com.ngra.trafficcontroller.models.Model_Result;
import com.ngra.trafficcontroller.utility.DeviceTools;
import com.ngra.trafficcontroller.views.application.TrafficController;

import io.reactivex.subjects.PublishSubject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewModel_FragmentVerify {

    private Context context;
    private String messageResult;
    private PublishSubject<String> Observables;

    public ViewModel_FragmentVerify(Context context) {//____________________________________________ Start ViewModel_FragmentVerify
        this.context = context;
        Observables = PublishSubject.create();
    }//_____________________________________________________________________________________________ End ViewModel_FragmentVerify


    public void SendNumber(String PhoneNumbet) {//__________________________________________________ Start SendNumber

        RetrofitModule.isCancel = false;
        RetrofitComponent retrofitComponent =
                TrafficController
                        .getApplication(context)
                        .getRetrofitComponent();


        retrofitComponent
                .getRetrofitApiInterface()
                .SendPhoneNumber(
                        PhoneNumbet
                )
                .enqueue(new Callback<Model_Result>() {
                    @Override
                    public void onResponse(Call<Model_Result> call, Response<Model_Result> response) {
                        if (response == null)
                            Observables.onNext("onFailure");
                        else {
                            String result = response.body().getResult();
                            if (result.equalsIgnoreCase("failed")) {
                                setMessageResult(response.body().getError());
                                Observables.onNext("Failed");
                            } else if (result.equalsIgnoreCase("done")){
                                setMessageResult(response.body().getCode());
                                Observables.onNext("SendDone");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Model_Result> call, Throwable t) {
                        setMessageResult(t.getMessage());
                        Observables.onNext("onFailure");
                    }
                });


    }//_____________________________________________________________________________________________ End SendNumber


    public void VerifyNumber(String PhoneNumbet, String verify) {//_________________________________ Start VerifyNumber

        RetrofitModule.isCancel = false;
        RetrofitComponent retrofitComponent =
                TrafficController
                        .getApplication(context)
                        .getRetrofitComponent();

        DeviceTools deviceTools = new DeviceTools(context);
        String imei = deviceTools.getIMEI();

        retrofitComponent
                .getRetrofitApiInterface()
                .VerifyPhoneNumber(
                        PhoneNumbet,
                        verify,
                        imei
                )
                .enqueue(new Callback<Model_Result>() {
                    @Override
                    public void onResponse(Call<Model_Result> call, Response<Model_Result> response) {
                        if (response == null)
                            Observables.onNext("onFailure");
                        else {
                            String result = response.body().getResult();
                            if (result.equalsIgnoreCase("failed")) {
                                setMessageResult(response.body().getError());
                                Observables.onNext("Failed");
                            } else if (result.equalsIgnoreCase("done")){
                                setMessageResult(response.body().getCode());
                                SaveUserLogin(PhoneNumbet);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Model_Result> call, Throwable t) {
                        setMessageResult(t.getMessage());
                        Observables.onNext("onFailure");
                    }
                });


    }//_____________________________________________________________________________________________ End VerifyNumber



    private void SaveUserLogin(String PhoneNumbet) {//______________________________________________ Start SaveUserLogin

        SharedPreferences.Editor token =
                context.getSharedPreferences("trafficcontroller", 0).edit();
        token.putString("phone", PhoneNumbet);
        token.putBoolean("login",true);
        token.putString("code",getMessageResult());
        token.apply();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Observables.onNext("VerifyDone");
            }
        },1000);


    }//_____________________________________________________________________________________________ End SaveUserLogin



    //______________________________________________________________________________________________  Start Getter & Setter
    public String getMessageResult() {
        return messageResult;
    }

    public void setMessageResult(String messageResult) {
        this.messageResult = messageResult;
    }

    public PublishSubject<String> getObservables() {
        return Observables;
    }

    //______________________________________________________________________________________________  End Getter & Sette

}
