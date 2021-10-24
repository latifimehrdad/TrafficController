package com.ngra.trafficcontroller.views.application;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.multidex.MultiDexApplication;

import com.ngra.trafficcontroller.R;
import com.ngra.trafficcontroller.dagger.retrofit.DaggerRetrofitComponent;
import com.ngra.trafficcontroller.dagger.retrofit.RetrofitComponent;
import com.ngra.trafficcontroller.dagger.retrofit.RetrofitModule;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.reactivex.subjects.PublishSubject;

public class TrafficController extends MultiDexApplication {

    private Context context;
    private RetrofitComponent retrofitComponent;
    public static PublishSubject<String> ObservablesGpsAndNetworkChange = PublishSubject.create();
    private IntentFilter s_intentFilter;


    @Override
    public void onCreate() {//______________________________________________________________________ Start onCreate
        super.onCreate();
        this.context = getApplicationContext();
//        setComponentEnabledSetting();
        ConfigurationCalligraphy();
        ConfigrationRetrofitComponent();
    }//_____________________________________________________________________________________________ End onCreate


    private void ConfigrationRetrofitComponent() {//________________________________________________ Start ConfigrationRetrofitComponent
        retrofitComponent = DaggerRetrofitComponent
                .builder()
                .retrofitModule(new RetrofitModule(context))
                .build();
    }//_____________________________________________________________________________________________ End ConfigrationRetrofitComponent


    private void ConfigurationCalligraphy() {//_____________________________________________________ Start ConfigurationCalligraphy
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("font/iransanslight.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

    }//_____________________________________________________________________________________________ End ConfigurationCalligraphy


    public static TrafficController getApplication(Context context) {//_____________________________ Start getApplication
        return (TrafficController) context.getApplicationContext();
    }//_____________________________________________________________________________________________ End getApplication


    public RetrofitComponent getRetrofitComponent() {//_____________________________________________ Start getRetrofitComponent
        return retrofitComponent;
    }//_____________________________________________________________________________________________ End getRetrofitComponent


    public boolean isLocationEnabled() {//_________________________________________________________ Start isLocationEnabled
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }//_____________________________________________________________________________________________ End isLocationEnabled


    public boolean isInternetConnected() {//________________________________________________________ Start isInternetConnected
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }//_____________________________________________________________________________________________ End isInternetConnected



}
