package com.ngra.trafficcontroller.viewmodels.fragment.home;

import static com.ngra.trafficcontroller.utility.StaticFunctions.CheckResponse;
import static com.ngra.trafficcontroller.utility.StaticFunctions.GetAuthorization;
import static com.ngra.trafficcontroller.views.application.TrafficController.ObservablesGpsAndNetworkChange;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.ngra.trafficcontroller.dagger.retrofit.RetrofitComponent;
import com.ngra.trafficcontroller.dagger.retrofit.RetrofitModule;
import com.ngra.trafficcontroller.models.ModelLocation;
import com.ngra.trafficcontroller.models.ModelLocations;
import com.ngra.trafficcontroller.models.ModelResponcePrimery;
import com.ngra.trafficcontroller.models.ModelResponsePersonnelStatus;
import com.ngra.trafficcontroller.models.ModelStatus;
import com.ngra.trafficcontroller.utility.DeviceTools;
import com.ngra.trafficcontroller.utility.StaticFunctions;
import com.ngra.trafficcontroller.views.application.TrafficController;

import java.util.ArrayList;

import io.reactivex.subjects.PublishSubject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VM_FragmentSplash {

    private Context context;
    private PublishSubject<String> Observables = null;
    private String MessageResponcse;
    private ModelStatus modelStatus;

    public VM_FragmentSplash(Context context) {//___________________________________________________ Start VM_FragmentSplash
        this.context = context;
        Observables = PublishSubject.create();
    }//_____________________________________________________________________________________________ End VM_FragmentSplash


    public void CheckToken() {//_______________________________ ____________________________________ Start CheckToken

        SharedPreferences prefs = context.getSharedPreferences("trafficcontrollertoken", 0);
        if (prefs == null) {
            Observables.onNext("GetTokenFromServer");
        } else {
            String access_token = prefs.getString("accesstoken", null);
            String expires = prefs.getString("expires", null);
            if ((access_token != null) || (expires != null)) {
                boolean login = prefs.getBoolean("login", false);
                if (login)
                    PersonnelStatus();
                else
                    Observables.onNext("ConfigHandlerForLogin");
            } else
                Observables.onNext("ConfigHandlerForLogin");
        }

    }//_____________________________________________________________________________________________ End CheckToken


    public void PersonnelStatus() {//_________________________________________________________ Start PersonnelStatus


        RetrofitModule.isCancel = false;
        RetrofitComponent retrofitComponent =
                TrafficController
                        .getApplication(context)
                        .getRetrofitComponent();

        DeviceTools deviceTools = new DeviceTools(context);
        String imei = deviceTools.getIMEI();
        String Authorization = GetAuthorization(context);

        retrofitComponent
                .getRetrofitApiInterface()
                .PersonnelStatus(
                        Authorization,
                        StaticFunctions.Get_aToken(context)
                )
                .enqueue(new Callback<ModelResponsePersonnelStatus>() {
                    @Override
                    public void onResponse(Call<ModelResponsePersonnelStatus> call, Response<ModelResponsePersonnelStatus> response) {
                        MessageResponcse = CheckResponse(response, true);
                        if (MessageResponcse == null) {
                            modelStatus = response.body().getResult();
                            Observables.onNext("ConfigHandlerForHome");
                        } else
                            ObservablesGpsAndNetworkChange.onNext("error");
                    }

                    @Override
                    public void onFailure(Call<ModelResponsePersonnelStatus> call, Throwable t) {
                        ObservablesGpsAndNetworkChange.onNext("Failure");
                    }
                });

    }//_____________________________________________________________________________________________ End SendLocatoinToServer



    //______________________________________________________________________________________________ Start Getter
    public PublishSubject<String> getObservables() {
        return Observables;
    }
    //______________________________________________________________________________________________ End Getter


    public String getMessageResponcse() {
        return MessageResponcse;
    }


    public ModelStatus getModelStatus() {
        return modelStatus;
    }
}
