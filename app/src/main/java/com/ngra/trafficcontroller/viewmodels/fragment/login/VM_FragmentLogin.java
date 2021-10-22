package com.ngra.trafficcontroller.viewmodels.fragment.login;

import android.content.Context;
import android.content.SharedPreferences;

import com.ngra.trafficcontroller.dagger.retrofit.RetrofitApis;
import com.ngra.trafficcontroller.dagger.retrofit.RetrofitComponent;
import com.ngra.trafficcontroller.dagger.retrofit.RetrofitModule;
import com.ngra.trafficcontroller.models.ModelResponcePrimery;
import com.ngra.trafficcontroller.models.ModelToken;
import com.ngra.trafficcontroller.utility.DeviceTools;
import com.ngra.trafficcontroller.views.application.TrafficController;

import io.reactivex.subjects.PublishSubject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ngra.trafficcontroller.utility.StaticFunctions.CheckResponse;
import static com.ngra.trafficcontroller.utility.StaticFunctions.GetAuthorization;

public class VM_FragmentLogin {

    private Context context;
    private String MessageResponcse;
    private PublishSubject<String> Observables;
    private ModelToken modelToken;

    public VM_FragmentLogin(Context context) {//____________________________________________________ Start VM_FragmentLogin
        this.context = context;
        Observables = PublishSubject.create();
    }//_____________________________________________________________________________________________ End VM_FragmentLogin


    public void SendNumber(String PhoneNumber) {//__________________________________________________ Start SendNumber


        RetrofitModule.isCancel = false;
        RetrofitComponent retrofitComponent =
                TrafficController
                        .getApplication(context)
                        .getRetrofitComponent();

        retrofitComponent
                .getRetrofitApiInterface()
                .SendPhoneNumber(PhoneNumber)
                .enqueue(new Callback<ModelResponcePrimery>() {
                    @Override
                    public void onResponse(Call<ModelResponcePrimery> call, Response<ModelResponcePrimery> response) {
                        if (RetrofitModule.isCancel)
                            return;
                        MessageResponcse = CheckResponse(response, true);
                        if (MessageResponcse == null) {
                            Observables.onNext("Successful");
                        } else
                            Observables.onNext("Error");
                    }

                    @Override
                    public void onFailure(Call<ModelResponcePrimery> call, Throwable t) {
                        Observables.onNext("Failure");
                    }
                });


    }//_____________________________________________________________________________________________ End SendNumber




    private void SaveLoginToken(String PhoneNumber) {//_______________________________________________________________ Start SaveLoginToken

        SharedPreferences.Editor token =
                context.getSharedPreferences("trafficcontrollertoken", 0).edit();
        token.putString("accesstoken", modelToken.getAccess_token());
        token.putString("tokentype", modelToken.getToken_type());
        token.putInt("expiresin", modelToken.getExpires_in());
        token.putString("clientid", modelToken.getClient_id());
        token.putString("issued", modelToken.getIssued());
        token.putString("expires", modelToken.getExpires());
        token.putString("phonenumber", PhoneNumber);
        token.putBoolean("login", true);
        token.apply();
        Observables.onNext("LoginDone");

    }//_____________________________________________________________________________________________ End SaveLoginToken




    //______________________________________________________________________________________________  Start Getter & Setter

    public PublishSubject<String> getObservables() {
        return Observables;
    }

    public String getMessageResponcse() {
        return MessageResponcse;
    }

    //______________________________________________________________________________________________  End Getter & Setter
}
