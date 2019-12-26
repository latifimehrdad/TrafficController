package com.ngra.trafficcontroller.dagger.realm;

import com.ngra.trafficcontroller.dagger.DaggerScope;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;

@Module
public class RealmModul {
    @Provides
    @DaggerScope
    public Realm getRealm() {
        return Realm.getDefaultInstance();
    }
}
