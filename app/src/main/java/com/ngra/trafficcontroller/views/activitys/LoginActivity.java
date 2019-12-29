package com.ngra.trafficcontroller.views.activitys;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ngra.trafficcontroller.R;
import com.ngra.trafficcontroller.databinding.ActivityLoginBinding;
import com.ngra.trafficcontroller.utility.broadcasts.ReceiverJobInBackground;
import com.ngra.trafficcontroller.utility.broadcasts.ReceiverLunchAppInBackground;
import com.ngra.trafficcontroller.viewmodels.activitys.ViewModel_LoginActivity;
import com.ngra.trafficcontroller.views.fragments.login.FragmentLogin;
import com.ngra.trafficcontroller.views.fragments.login.FragmentVerify;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class LoginActivity extends AppCompatActivity {

    private ViewModel_LoginActivity viewModel;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private PublishSubject<String> Observables;
    private String PhoneNumber;


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
        checkLocationPermission();
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
                                    case "finishok":
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                    sendBroadcast(
                                                            new Intent(
                                                                    LoginActivity.this,
                                                                    ReceiverLunchAppInBackground.class).setAction("ir.ngra.Lunch"));
                                                } else {
                                                    Intent i = new Intent("ir.ngra.Lunch");
                                                    sendBroadcast(i);
                                                    finish();
                                                }

                                            }
                                        }, 1000);

                                        break;
                                    default:
                                        PhoneNumber = s;
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
        FragmentVerify verify = new FragmentVerify(this, Observables,PhoneNumber);
        ft.replace(R.id.loginFragment, verify);
        ft.commit();
    }//_____________________________________________________________________________________________ End ShowFragmentVerify



    public void checkLocationPermission() {//_____________________________________________________________________________________________ Start checkLocationPermission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("دسترسی به موقعیت")
                        .setMessage("برای نمایش مکان شما به موقعیت دسترسی بدهید")
                        .setPositiveButton("تایید", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(LoginActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        1);
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
        }
    }//_____________________________________________________________________________________________ End checkLocationPermission



    public void checkReadPhonestate(){//____________________________________________________________ Start checkReadPhonestate
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 2);
        } else {
            //TODO
        }
    }//_____________________________________________________________________________________________ End checkReadPhonestate



    private void CheckToken() {//_______________________________ ___________________________________ Start CheckToken
        SharedPreferences prefs = this.getSharedPreferences("trafficcontroller", 0);
        if (prefs == null) {
            moveTaskToBack(true);
            System.exit(0);
        } else {
            Boolean login = prefs.getBoolean("login", false);
            if (login == false) {
                moveTaskToBack(true);
                System.exit(0);
            }
        }

    }//_____________________________________________________________________________________________ End CheckToken



    @Override
    protected void onDestroy() {//__________________________________________________________________ Start onDestroy
        super.onDestroy();
        CheckToken();
    }//_____________________________________________________________________________________________ End onDestroy


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {//____________ Start onRequestPermissionsResult
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    checkReadPhonestate();

//                    // permission was granted, yay! Do the
//                    // location-related task you need to do.
//                    if (ContextCompat.checkSelfPermission(this,
//                            Manifest.permission.ACCESS_FINE_LOCATION)
//                            == PackageManager.PERMISSION_GRANTED) {
//
//                        //Request location updates:
//                    }
//
//                } else {
//
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//
                }
                return;
            }
            case 2: {
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //TODO
                }
            }

        }
    }//_____________________________________________________________________________________________ End onRequestPermissionsResult





}
