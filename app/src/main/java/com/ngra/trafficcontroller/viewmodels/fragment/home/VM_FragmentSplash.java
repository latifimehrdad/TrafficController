package com.ngra.trafficcontroller.viewmodels.fragment.home;

import android.content.Context;
import android.content.SharedPreferences;

import com.ngra.trafficcontroller.dagger.retrofit.RetrofitApis;
import com.ngra.trafficcontroller.dagger.retrofit.RetrofitComponent;
import com.ngra.trafficcontroller.models.ModelToken;
import com.ngra.trafficcontroller.views.application.TrafficController;

import io.reactivex.subjects.PublishSubject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ngra.trafficcontroller.utility.StaticFunctions.CheckResponse;

public class VM_FragmentSplash {

    private Context context;
    private String MessageResponcse;
    private ModelToken modelToken;
    private PublishSubject<String> Observables = null;

    public VM_FragmentSplash(Context context) {//___________________________________________________ Start VM_FragmentSplash
        this.context = context;
        Observables = PublishSubject.create();
    }//_____________________________________________________________________________________________ End VM_FragmentSplash


    public void GetTokenBeforeLoginFromServer() {//__________________________________________________ Start GetTokenBeforeLoginFromServer

        RetrofitComponent retrofitComponent =
                TrafficController
                        .getApplication(context)
                        .getRetrofitComponent();

        retrofitComponent
                .getRetrofitApiInterface()
                .getToken(
                        RetrofitApis.client_id_value,
                        RetrofitApis.client_secret_value,
                        RetrofitApis.grant_type_value)
                .enqueue(new Callback<ModelToken>() {
                    @Override
                    public void onResponse(Call<ModelToken> call, Response<ModelToken> response) {
                        MessageResponcse = CheckResponse(response, true);
                        if (MessageResponcse == null) {
                            modelToken = response.body();
                            SaveToken();
                        } else
                            Observables.onNext("Error");
                    }

                    @Override
                    public void onFailure(Call<ModelToken> call, Throwable t) {
                        Observables.onNext("Failure");
                    }
                });


    }//_____________________________________________________________________________________________ End GetTokenBeforeLoginFromServer


    private void SaveToken() {//____________________________________________________________________ Start SaveToken

        SharedPreferences.Editor token =
                context.getSharedPreferences("trafficcontrollertoken", 0).edit();
        token.putString("accesstoken", modelToken.getAccess_token());
        token.putString("tokentype", modelToken.getToken_type());
        token.putInt("expiresin", modelToken.getExpires_in());
        token.putString("clientid", modelToken.getClient_id());
        token.putString("issued", modelToken.getIssued());
        token.putString("expires", modelToken.getExpires());
        token.apply();
        Observables.onNext("ConfigHandlerForLogin");

    }//_____________________________________________________________________________________________ End SaveToken




    public void CheckToken() {//_______________________________ ____________________________________ Start CheckToken
        SharedPreferences prefs = context.getSharedPreferences("trafficcontrollertoken", 0);
        if (prefs == null) {
            Observables.onNext("GetTokenFromServer");
        } else {
            String access_token = prefs.getString("accesstoken", null);
            String expires = prefs.getString("expires", null);
            if ((access_token == null) || (expires == null))
                Observables.onNext("GetTokenFromServer");
            else {
                boolean login = prefs.getBoolean("login", false);
                if (login)
                    Observables.onNext("ConfigHandlerForHome");
                else
                    Observables.onNext("ConfigHandlerForLogin");

            }
        }

    }//_____________________________________________________________________________________________ End CheckToken




    //______________________________________________________________________________________________ Start Getter
    public String getMessageResponcse() {
        return MessageResponcse;
    }

    public PublishSubject<String> getObservables() {
        return Observables;
    }
    //______________________________________________________________________________________________ End Getter

}
