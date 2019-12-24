package com.ngra.trafficcontroller.viewmodels.fragment.login;

import android.content.Context;

import com.ngra.trafficcontroller.dagger.retrofit.RetrofitComponent;
import com.ngra.trafficcontroller.dagger.retrofit.RetrofitModule;
import com.ngra.trafficcontroller.models.Model_Result;
import com.ngra.trafficcontroller.views.application.TrafficController;

import io.reactivex.subjects.PublishSubject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewModel_FragmentLogin {

    private Context context;
    private String messageResult;
    public PublishSubject<String> Observables;

    public ViewModel_FragmentLogin(Context context) {//_____________________________________________ Start ViewModel_FragmentLogin
        this.context = context;
        Observables = PublishSubject.create();
    }//_____________________________________________________________________________________________ End ViewModel_FragmentLogin


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
                                Observables.onNext("failed");
                            } else if (result.equalsIgnoreCase("done")){
                                setMessageResult(response.body().getCode());
                                Observables.onNext("done");
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


    //______________________________________________________________________________________________  Start Getter & Setter
    public String getMessageResult() {
        return messageResult;
    }

    public void setMessageResult(String messageResult) {
        this.messageResult = messageResult;
    }
    //______________________________________________________________________________________________  End Getter & Setter
}
