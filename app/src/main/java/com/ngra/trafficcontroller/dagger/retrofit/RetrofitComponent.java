package com.ngra.trafficcontroller.dagger.retrofit;


import com.ngra.trafficcontroller.dagger.DaggerScope;

import dagger.Component;

@DaggerScope
@Component(modules = RetrofitModule.class)
public interface RetrofitComponent {
    RetrofitApiInterface getRetrofitApiInterface();
}
