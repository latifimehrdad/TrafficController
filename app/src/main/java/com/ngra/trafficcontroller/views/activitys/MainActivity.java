package com.ngra.trafficcontroller.views.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.databinding.DataBindingUtil;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ngra.trafficcontroller.R;
import com.ngra.trafficcontroller.databinding.ActivityMainBinding;
import com.ngra.trafficcontroller.models.ModelChartMeasureDistance;
import com.ngra.trafficcontroller.utility.broadcasts.ReceiverLunchAppInBackground;
import com.ngra.trafficcontroller.viewmodels.activitys.ViewModel_MainActivity;
import com.ngra.trafficcontroller.views.application.TrafficController;
import com.ngra.trafficcontroller.views.dialogs.DialogChartMeasure;
import com.ngra.trafficcontroller.views.dialogs.DialogMessage;

import org.reactivestreams.Subscription;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


import static com.ngra.trafficcontroller.views.application.TrafficController.ObservablesGpsAndNetworkChange;

public class MainActivity extends AppCompatActivity {

    private ViewModel_MainActivity viewModel;
    private boolean ShowLogin = false;
    private Subscription subscription;

    @BindView(R.id.imgLocation)
    ImageView imgLocation;

    @BindView(R.id.imgInternet)
    ImageView imgInternet;

    @BindView(R.id.LastGPS)
    TextView LastGPS;

    @BindView(R.id.LastNet)
    TextView LastNet;

    @BindView(R.id.CircleMenu)
    RelativeLayout CircleMenu;

    @BindView(R.id.CircleMenuCenter)
    LinearLayout CircleMenuCenter;

    @BindView(R.id.ImgCircleMenu)
    ImageView ImgCircleMenu;

    @BindView(R.id.LayoutMeasureDistance)
    LinearLayout LayoutMeasureDistance;

    @BindView(R.id.LayoutMeasureDistanceChart)
    LinearLayout LayoutMeasureDistanceChart;

    @BindView(R.id.LayoutNetSetting)
    LinearLayout LayoutNetSetting;

    @BindView(R.id.ImgWifi)
    ImageView ImgWifi;


    @BindView(R.id.ImgData)
    ImageView ImgData;

    @BindView(R.id.LayoutPrimary)
    RelativeLayout LayoutPrimary;


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
        init();
        CheckToken();
        ObserverObservableGpsAndNetworkChange();

    }//_____________________________________________________________________________________________ End OnBindView


    private void init() {//_________________________________________________________________________ Start init

        CircleMenu.setVisibility(View.INVISIBLE);
        ImgCircleMenu.setImageResource(R.drawable.ic_apps);

        if (TrafficController.getApplication(this).isLocationEnabled())
            imgLocation.setImageResource(R.drawable.ic_location_on);
        else
            imgLocation.setImageResource(R.drawable.ic_location_off);

        if (TrafficController.getApplication(this).isInternetConnected())
            imgInternet.setImageResource(R.drawable.ic_internet_on);
        else
            imgInternet.setImageResource(R.drawable.ic_internet_off);

        LastGPS.setText(viewModel.GetLastGPS());
        LastNet.setText(viewModel.GetLastNet());
        SetClicks();

    }//_____________________________________________________________________________________________ End init


    private void SetClicks() {//____________________________________________________________________ Start SetClicks


        LayoutPrimary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (LayoutNetSetting.getVisibility() == View.VISIBLE) {
                    LayoutNetSetting
                            .startAnimation(AnimationUtils
                                    .loadAnimation(getBaseContext(), R.anim.slide_out_bottom));
                    LayoutNetSetting.setVisibility(View.INVISIBLE);
                }
                CircleMenu.setVisibility(View.INVISIBLE);
                ImgCircleMenu.setImageResource(R.drawable.ic_apps);
            }
        });


        LayoutMeasureDistanceChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<ModelChartMeasureDistance>
                        arrayList = viewModel.getModelChartMeasureDistances();
                ShowChart(arrayList);
            }
        });


        LayoutMeasureDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Integer MD = viewModel.getLocationsForMeasureDistance();
                String message = getResources().getString(R.string.MeasureDistanceToday);
                message = message +
                        "\n" +
                        String.valueOf(MD / 1000) +
                        " " +
                        getResources().getString(R.string.KM) +
                        " و " +
                        String.valueOf(MD % 1000) +
                        " " +
                        getResources().getString(R.string.Meter);

                ShowMessage(
                        message,
                        getResources().getColor(R.color.ML_White),
                        getResources().getDrawable(R.drawable.ic_directions_walk)
                );

            }
        });

        CircleMenuCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LayoutNetSetting.getVisibility() == View.VISIBLE) {
                    LayoutNetSetting
                            .startAnimation(AnimationUtils
                                    .loadAnimation(getBaseContext(), R.anim.slide_out_bottom));
                    LayoutNetSetting.setVisibility(View.INVISIBLE);
                }

                if (CircleMenu.getVisibility() == View.INVISIBLE) {
                    CircleMenu.setVisibility(View.VISIBLE);
                    ImgCircleMenu.setImageResource(R.drawable.ic_center_focus);
                } else {
                    CircleMenu.setVisibility(View.INVISIBLE);
                    ImgCircleMenu.setImageResource(R.drawable.ic_apps);
                }
            }
        });


        ImgWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LayoutNetSetting.getVisibility() == View.VISIBLE) {
                    LayoutNetSetting
                            .startAnimation(AnimationUtils
                                    .loadAnimation(getBaseContext(), R.anim.slide_out_bottom));
                    LayoutNetSetting.setVisibility(View.INVISIBLE);
                }

                startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
//                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//                wifiManager.setWifiEnabled(true);
            }
        });


        ImgData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (LayoutNetSetting.getVisibility() == View.VISIBLE) {
                    LayoutNetSetting
                            .startAnimation(AnimationUtils
                                    .loadAnimation(getBaseContext(), R.anim.slide_out_bottom));
                    LayoutNetSetting.setVisibility(View.INVISIBLE);
                }

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setComponent(new ComponentName("com.android.settings",
                        "com.android.settings.Settings$DataUsageSummaryActivity"));
                startActivity(intent);
            }
        });


        imgInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TrafficController.getApplication(MainActivity.this).isInternetConnected())
                    return;

                if (LayoutNetSetting.getVisibility() == View.INVISIBLE) {
                    LayoutNetSetting
                            .startAnimation(AnimationUtils
                                    .loadAnimation(getBaseContext(), R.anim.slide_in_bottom));
                    LayoutNetSetting.setVisibility(View.VISIBLE);
                } else {
                    LayoutNetSetting
                            .startAnimation(AnimationUtils
                                    .loadAnimation(getBaseContext(), R.anim.slide_out_bottom));
                    LayoutNetSetting.setVisibility(View.INVISIBLE);
                }
            }
        });


        imgLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (LayoutNetSetting.getVisibility() == View.VISIBLE) {
                    LayoutNetSetting
                            .startAnimation(AnimationUtils
                                    .loadAnimation(getBaseContext(), R.anim.slide_out_bottom));
                    LayoutNetSetting.setVisibility(View.INVISIBLE);
                }

                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);

//                Intent startMain = new Intent(Intent.ACTION_MAIN);
//                startMain.addCategory(Intent.CATEGORY_HOME);
//                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(startMain);

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
    }//_____________________________________________________________________________________________ End SetClicks


    private void ShowMessage(String message, int color, Drawable icon) {//__________________________ Start ShowMessage
        DialogMessage dialogMessage = new DialogMessage(this, message, color, icon);
        dialogMessage.setCancelable(false);
        dialogMessage.show(getSupportFragmentManager(), NotificationCompat.CATEGORY_PROGRESS);
    }//_____________________________________________________________________________________________ End ShowMessage


    private void ShowChart(ArrayList<ModelChartMeasureDistance> arrayList) {//______________________ Start ShowChart
        DialogChartMeasure measure = new DialogChartMeasure(this, arrayList);
        measure.setCancelable(false);
        measure.show(getSupportFragmentManager(), NotificationCompat.CATEGORY_PROGRESS);
    }//_____________________________________________________________________________________________ End ShowChart


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
                                        case "LastGPS":
                                            LastGPS.setText(viewModel.GetLastGPS());
                                            break;

                                        case "LastNet":
                                            LastNet.setText(viewModel.GetLastNet());
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


    private void CheckToken() {//_______________________________ ___________________________________ Start CheckToken
        SharedPreferences prefs = this.getSharedPreferences("trafficcontroller", 0);
        if (prefs == null) {
            ShowLogin = true;
            ShowLoginActivity();
        } else {
            Boolean login = prefs.getBoolean("login", false);
            if (login == false) {
                ShowLogin = true;
                ShowLoginActivity();
            } else
                StartService();
        }

    }//_____________________________________________________________________________________________ End CheckToken


    private void ShowLoginActivity() {//____________________________________________________________ Start ShowLoginActivity
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }//_____________________________________________________________________________________________ End ShowLoginActivity


    public void attachBaseContext(Context newBase) {//______________________________________________ Start attachBaseContext
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }//_____________________________________________________________________________________________ End attachBaseContext


    private void ObservableInterval() {//___________________________________________________________ Start ObservableInterval

        Observable.interval(1, TimeUnit.MINUTES)
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
                        //GetCurrentLocation();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }//_____________________________________________________________________________________________ End ObservableInterval


    private void StartService() {//_________________________________________________________________ Start StartService
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sendBroadcast(new Intent(MainActivity.this, ReceiverLunchAppInBackground.class).setAction("ir.ngra.Lunch"));
                } else {
                    Intent i = new Intent("ir.ngra.Lunch");
                    sendBroadcast(i);
                }


            }
        }, 1000);
    }//_____________________________________________________________________________________________ End StartService


}
