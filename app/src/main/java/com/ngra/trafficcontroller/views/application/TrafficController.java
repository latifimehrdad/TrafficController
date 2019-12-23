package com.ngra.trafficcontroller.views.application;

import android.app.Application;
import android.content.Context;

import com.ngra.trafficcontroller.R;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class TrafficController extends Application {

    private Context context;

    @Override
    public void onCreate() {//______________________________________________________________________ Start onCreate
        super.onCreate();
        this.context = getApplicationContext();
        ConfigurationCalligraphy();
    }//_____________________________________________________________________________________________ End onCreate


    private void ConfigurationCalligraphy() {//_____________________________________________________ Start ConfigurationCalligraphy
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("font/iransanslight.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

    }//_____________________________________________________________________________________________ End ConfigurationCalligraphy



}
