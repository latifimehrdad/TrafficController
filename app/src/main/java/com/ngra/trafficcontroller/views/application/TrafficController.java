package com.ngra.trafficcontroller.views.application;

import android.app.Application;
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
import com.ngra.trafficcontroller.dagger.realm.DaggerRealmComponent;
import com.ngra.trafficcontroller.dagger.realm.RealmComponent;
import com.ngra.trafficcontroller.dagger.realm.RealmModul;
import com.ngra.trafficcontroller.dagger.retrofit.DaggerRetrofitComponent;
import com.ngra.trafficcontroller.dagger.retrofit.RetrofitComponent;
import com.ngra.trafficcontroller.dagger.retrofit.RetrofitModule;
import com.ngra.trafficcontroller.utility.broadcasts.ReceiverDateTimeChange;
import com.ngra.trafficcontroller.utility.broadcasts.ReceiverGpsLocation;
import com.ngra.trafficcontroller.utility.broadcasts.ReceiverLunchAppInBackground;
import com.ngra.trafficcontroller.utility.broadcasts.ReceiverNetworkChange;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.reactivex.subjects.PublishSubject;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class TrafficController extends MultiDexApplication {

    private Context context;
    private RetrofitComponent retrofitComponent;
    private RealmComponent realmComponent;
    public static PublishSubject<String> ObservablesGpsAndNetworkChange = PublishSubject.create();
    private IntentFilter s_intentFilter;


    @Override
    public void onCreate() {//______________________________________________________________________ Start onCreate
        super.onCreate();
        this.context = getApplicationContext();
        setComponentEnabledSetting();
        registerBroadcast();
        ConfigurationCalligraphy();
        ConfigrationRetrofitComponent();
        ConfigrationRealmComponent();
    }//_____________________________________________________________________________________________ End onCreate


    private void ConfigrationRetrofitComponent() {//________________________________________________ Start ConfigrationRetrofitComponent
        retrofitComponent = DaggerRetrofitComponent
                .builder()
                .retrofitModule(new RetrofitModule(context))
                .build();
    }//_____________________________________________________________________________________________ End ConfigrationRetrofitComponent


    private void ConfigrationRealmComponent() {//___________________________________________________ Start ConfigrationRealmComponent
        Realm.init(this);
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder().name("TrafficControllerRealm").schemaVersion(1).build());
        realmComponent = DaggerRealmComponent
                .builder()
                .realmModul(new RealmModul())
                .build();
    }//_____________________________________________________________________________________________ End ConfigrationRealmComponent


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


    private void registerBroadcast() {//____________________________________________________________ Start registerBroadcast
        BroadcastReceiver gpsChange = new ReceiverGpsLocation();
        getApplicationContext().registerReceiver(gpsChange, new IntentFilter("android.location.PROVIDERS_CHANGED"));
        BroadcastReceiver netChange = new ReceiverNetworkChange();
        getApplicationContext().registerReceiver(netChange, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        s_intentFilter = new IntentFilter();
        s_intentFilter.addAction(Intent.ACTION_TIME_TICK);
        s_intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        s_intentFilter.addAction(Intent.ACTION_TIME_CHANGED);

        ReceiverDateTimeChange timeChange = new ReceiverDateTimeChange();
        getApplicationContext().registerReceiver(timeChange, s_intentFilter);

    }//_____________________________________________________________________________________________ End registerBroadcast


    public RealmComponent getRealmComponent() {//___________________________________________________ Start getRealmComponent
        return realmComponent;
    }//_____________________________________________________________________________________________ End getRealmComponent


    private void setComponentEnabledSetting() {//___________________________________________________ Start setComponentEnabledSetting
        ComponentName receiver = new ComponentName(this, ReceiverLunchAppInBackground.class);
        PackageManager pm = this.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }//_____________________________________________________________________________________________ End setComponentEnabledSetting


}
