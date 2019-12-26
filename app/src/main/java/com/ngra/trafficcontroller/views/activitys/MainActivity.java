package com.ngra.trafficcontroller.views.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.databinding.DataBindingUtil;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.ngra.trafficcontroller.R;
import com.ngra.trafficcontroller.databinding.ActivityMainBinding;
import com.ngra.trafficcontroller.utility.broadcasts.ReceiverLunchAppInBackground;
import com.ngra.trafficcontroller.viewmodels.activitys.ViewModel_MainActivity;
import com.ngra.trafficcontroller.views.application.TrafficController;

import org.reactivestreams.Subscription;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider;


import static com.ngra.trafficcontroller.views.application.TrafficController.ObservablesGpsAndNetworkChange;

public class MainActivity extends AppCompatActivity {

    private ViewModel_MainActivity viewModel;
    int click = 1;
    private Subscription subscription;

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
        ActivityMainBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        binding.setMain(viewModel);
        ButterKnife.bind(this);
        //CheckToken();
        ObserverObservableGpsAndNetworkChange();

        if (TrafficController.getApplication(this).isLocationEnabled())
            imgLocation.setImageResource(R.drawable.ic_location_on);
        else
            imgLocation.setImageResource(R.drawable.ic_location_off);

        if (TrafficController.getApplication(this).isInternetConnected())
            imgInternet.setImageResource(R.drawable.ic_internet_on);
        else
            imgInternet.setImageResource(R.drawable.ic_internet_off);


        imgInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObservableInterval();
                //GetCurrentLocation();
            }
        });


        imgLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);

//                Locale locale = new Locale("fa_IR");
//                Locale.setDefault(locale);
//                ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(MainActivity.this);
//                Observable<List<Address>> reverseGeocodeObservable = locationProvider
//                        .getReverseGeocodeObservable(locale, 35.830154, 50.962814, 5);
//
//                reverseGeocodeObservable
//                        .subscribeOn(Schedulers.io())               // use I/O thread to query for addresses
//                        .observeOn(AndroidSchedulers.mainThread())  // return result in main android thread to manipulate UI
//                        .subscribe(new DisposableObserver<List<Address>>() {
//                            @Override
//                            public void onNext(List<Address> addresses) {
//                                if (addresses.size() == 0)
//                                    return;
//                                String LongAddress = "";
//                                for (Address longAddress : addresses) {
//                                    String ad = longAddress.getAddressLine(0);
//                                    if (ad.length() > LongAddress.length()) {
//                                        LongAddress = ad;
//                                    }
//                                }
//                                ShowNotificationOld(LongAddress);
//
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//
//                            }
//
//                            @Override
//                            public void onComplete() {
//
//                            }
//                        });
            }
        });


//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    sendBroadcast(new Intent(MainActivity.this, ReceiverLunchAppInBackground.class).setAction("ir.ngra.Lunch"));
//                } else {
//                    Intent i = new Intent("ir.ngra.Lunch");
//                    sendBroadcast(i);
//                }
//
//
//            }
//        }, 1000);


    }//_____________________________________________________________________________________________ End OnBindView


    private void ObservableInterval(){

        Observable.interval(1,TimeUnit.MINUTES)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd _ HH:mm:ss", Locale.getDefault());
                        String currentDateandTime = sdf.format(new Date());
                        Log.i("meri", "T*** " + currentDateandTime);
                    }

                    @Override
                    public void onNext(Long aLong) {
                        GetCurrentLocation();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    private void GetAddressFromLatLong() {//_________________________________________________________ Start GetAddressFromLatLong
        String LongAddress = "";
        try {
            Geocoder geocoder;
            List<Address> addresses;
            Locale locale = new Locale("fa_IR");
            Locale.setDefault(locale);
            geocoder = new Geocoder(getApplicationContext(), locale);
            addresses = geocoder.getFromLocation(35.830154, 50.962814, 5); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            if (addresses.size() == 0) {

            } else {
                for (Address longAddress : addresses) {
                    String ad = longAddress.getAddressLine(0);
                    if (ad.length() > LongAddress.length()) {
                        LongAddress = ad;
                    }
                }
                ShowNotificationOld(LongAddress);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }//_____________________________________________________________________________________________ End GetAddressFromLatLong


    NotificationManager notifManager;

    private void ShowNotificationOld(String Text) {//________________________________ Start ShowNotificationOld

        long when = System.currentTimeMillis();
        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
                this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(this.getResources().getString(R.string.app_name))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(Text))
                .setWhen(when);
        getManager().notify(7126, mNotifyBuilder.build());

    }//_____________________________________________________________________________________________ End ShowNotificationOld


    private NotificationManager getManager() {//____________________________________________________ Start getManager
        if (notifManager == null) {
            notifManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notifManager;
    }//_____________________________________________________________________________________________ End getManager


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
                                            else {
                                                imgLocation.setImageResource(R.drawable.ic_location_off);
                                            }
                                            break;
                                        case "changeNetwork":
                                            if (TrafficController.getApplication(MainActivity.this).isInternetConnected())
                                                imgInternet.setImageResource(R.drawable.ic_internet_on);
                                            else {
                                                imgInternet.setImageResource(R.drawable.ic_internet_off);
                                            }
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
                        Log.i("meri", "LatLong : " + location.getLatitude() + " __ " + location.getLongitude());
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
