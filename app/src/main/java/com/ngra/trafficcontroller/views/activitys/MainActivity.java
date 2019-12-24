package com.ngra.trafficcontroller.views.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.location.LocationRequest;
import com.ngra.trafficcontroller.R;
import com.ngra.trafficcontroller.databinding.ActivityMainBinding;
import com.ngra.trafficcontroller.utility.broadcasts.GpsLocationReceiver;
import com.ngra.trafficcontroller.viewmodels.activitys.ViewModel_MainActivity;
import com.ngra.trafficcontroller.views.application.TrafficController;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider;

import static com.ngra.trafficcontroller.views.application.TrafficController.ObservablesGpsAndNetworkChange;

public class MainActivity extends AppCompatActivity {

    private ViewModel_MainActivity viewModel;

    @BindView(R.id.imgLocation)
    ImageView imgLocation;

    @BindView(R.id.imgInternet)
    ImageView imgInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {//__________________________________________ Start onCreate
        super.onCreate(savedInstanceState);
        OnBindView();
    }//_____________________________________________________________________________________________ End onCreate


    private void OnBindView() {//___________________________________________________________________ Start OnBindView
        viewModel = new ViewModel_MainActivity(this);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setMain(viewModel);
        ButterKnife.bind(this);
        CheckToken();
        ObserverObservableGpsAndNetworkChange();

        if (TrafficController.getApplication(this).isLocationEnabled())
            imgLocation.setImageResource(R.drawable.ic_location_on);
        else
            imgLocation.setImageResource(R.drawable.ic_location_off);

        if(TrafficController.getApplication(this).isInternetConnected())
            imgInternet.setImageResource(R.drawable.ic_internet_on);
        else
            imgInternet.setImageResource(R.drawable.ic_internet_off);


    }//_____________________________________________________________________________________________ End OnBindView


    private void ObserverObservableGpsAndNetworkChange() {//________________________________________ Start ObserverObservableGpsAndNetworkChange
        if (ObservablesGpsAndNetworkChange != null) {
            ObservablesGpsAndNetworkChange
                    .observeOn(Schedulers.io())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableObserver<String>() {
                        @Override
                        public void onNext(String s) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    switch (s) {
                                        case "changeGPS":
                                            if (TrafficController.getApplication(MainActivity.this).isLocationEnabled())
                                                imgLocation.setImageResource(R.drawable.ic_location_on);
                                            else
                                                imgLocation.setImageResource(R.drawable.ic_location_off);
                                            break;
                                        case "changeNetwork":
                                            if(TrafficController.getApplication(MainActivity.this).isInternetConnected())
                                                imgInternet.setImageResource(R.drawable.ic_internet_on);
                                            else
                                                imgInternet.setImageResource(R.drawable.ic_internet_off);
                                            break;
                                    }
                                }
                            });
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }//_____________________________________________________________________________________________ End ObserverObservableGpsAndNetworkChange


    private void GetCurrentLocation() {
        LocationRequest request = LocationRequest.create() //standard GMS LocationRequest
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setNumUpdates(1)
                .setInterval(1000);
        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(MainActivity.this);
        Disposable subscription = locationProvider.getUpdatedLocation(request)
                .subscribe(new Consumer<Location>() {
                    @Override
                    public void accept(Location location) throws Exception {
                        Log.i("meri", location.getLatitude() + " __ " + location.getLongitude());
                    }


                });
    }


    private void CheckToken() {//_______________________________ ___________________________________ Start CheckToken
        SharedPreferences prefs = this.getSharedPreferences("trafficcontroller", 0);
        if (prefs == null) {
            ShowLoginActivity();
        } else {
            Boolean login = prefs.getBoolean("login", false);
            if (login == false)
                ShowLoginActivity();
        }

    }//_____________________________________________________________________________________________ End CheckToken


    private void ShowLoginActivity() {//____________________________________________________________ Start ShowLoginActivity
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }//_____________________________________________________________________________________________ End ShowLoginActivity


    public void attachBaseContext(Context newBase) {//______________________________________________ Start attachBaseContext
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }//_____________________________________________________________________________________________ End attachBaseContext


}
