package com.ngra.trafficcontroller.views.activitys;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ngra.trafficcontroller.R;
import com.ngra.trafficcontroller.databinding.ActivityLoginBinding;
import com.ngra.trafficcontroller.viewmodels.activitys.ViewModel_LoginActivity;
import com.ngra.trafficcontroller.views.fragments.login.FragmentLogin;
import com.ngra.trafficcontroller.views.fragments.login.FragmentVerify;

import butterknife.ButterKnife;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class LoginActivity extends AppCompatActivity {

    private ViewModel_LoginActivity viewModel;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private PublishSubject<String> Observables;


    @Override
    protected void onCreate(Bundle savedInstanceState) {//__________________________________________ Start onCreate
        super.onCreate(savedInstanceState);
        OnBindView();
    }//_____________________________________________________________________________________________ End onCreate


    private void OnBindView() {//___________________________________________________________________ Start OnBindView
        viewModel = new ViewModel_LoginActivity(this);
        ActivityLoginBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.setLogin(viewModel);
        ButterKnife.bind(this);
        Observables = PublishSubject.create();
        ObserverObservables();
        ShowFragmentLogin();
        //Observables.onNext("login");
    }//_____________________________________________________________________________________________ End OnBindView


    private void ObserverObservables() {//__________________________________________________________ Start ObserverObservables
        Observables
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                switch (s) {
                                    case "login":
                                        ShowFragmentLogin();
                                        break;
                                    case "verify":
                                        ShowFragmentVerify();
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

    }//_____________________________________________________________________________________________ End ObserverObservables


    public void attachBaseContext(Context newBase) {//______________________________________________ Start attachBaseContext
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }//_____________________________________________________________________________________________ End attachBaseContext


    private void ShowFragmentLogin() {//_____________________________________________________________ Start ShowFragmentLogin
        fm = null;
        ft = null;
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        FragmentLogin login = new FragmentLogin(this, Observables);
        ft.replace(R.id.loginFragment, login);
        ft.commit();
    }//_____________________________________________________________________________________________ End ShowFragmentLogin


    private void ShowFragmentVerify() {//____________________________________________________________ Start ShowFragmentVerify
        fm = null;
        ft = null;
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        FragmentVerify verify = new FragmentVerify(this, Observables);
        ft.replace(R.id.loginFragment, verify);
        ft.commit();
    }//_____________________________________________________________________________________________ End ShowFragmentVerify


}
