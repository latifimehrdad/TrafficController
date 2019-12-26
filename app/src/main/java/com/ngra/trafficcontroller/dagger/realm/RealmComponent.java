package com.ngra.trafficcontroller.dagger.realm;

import com.ngra.trafficcontroller.dagger.DaggerScope;

import dagger.Component;
import io.realm.Realm;

@DaggerScope
@Component(modules = {RealmModul.class})
public interface RealmComponent {
    Realm getRealm();
}
