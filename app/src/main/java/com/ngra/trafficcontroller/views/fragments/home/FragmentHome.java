package com.ngra.trafficcontroller.views.fragments.home;


import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cunoraz.gifview.library.GifView;
import com.ngra.trafficcontroller.R;
import com.ngra.trafficcontroller.databinding.FragmentHomeBinding;
import com.ngra.trafficcontroller.models.ModelChartMeasureDistance;
import com.ngra.trafficcontroller.viewmodels.fragment.home.VM_FragmentHome;
import com.ngra.trafficcontroller.views.application.TrafficController;
import com.ngra.trafficcontroller.views.dialogs.DialogChartMeasure;
import com.ngra.trafficcontroller.views.dialogs.DialogMessage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static com.ngra.trafficcontroller.views.application.TrafficController.ObservablesGpsAndNetworkChange;


public class FragmentHome extends Fragment {

    private NavController navController;
    private View view;
    private Context context;
    private VM_FragmentHome vm_fragmentHome;
    private DisposableObserver<String> observer;
    private Integer TryToLocation = 0;
    private LocationManager locationManager;
    public MyLocationListener listener;
    private final int TWO_MINUTES = 1000 * 60 * 2;
    private Location previousBestLocation = null;
    private Location currentLocation = null;
    private int status;

    @BindView(R.id.imgLocation)
    ImageView imgLocation;

    @BindView(R.id.imgInternet)
    ImageView imgInternet;

    @BindView(R.id.LayoutPrimary)
    RelativeLayout LayoutPrimary;

    @BindView(R.id.textViewWaiting)
    TextView textViewWaiting;

    @BindView(R.id.LayoutSend)
    LinearLayout LayoutSend;

    @BindView(R.id.ProgressGif)
    GifView ProgressGif;

    @BindView(R.id.imgSend)
    ImageView imgSend;

    @BindView(R.id.textViewSend)
    TextView textViewSend;



    public FragmentHome() {//_______________________________________________________________________ Start FragmentLogin

    }//_____________________________________________________________________________________________ End FragmentLogin


    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {//__________________________________________________________ Start onCreateView
        this.context = getContext();
        vm_fragmentHome = new VM_FragmentHome(context);
        FragmentHomeBinding binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_home, container, false
        );
        if (getArguments() != null)
            status = getArguments().getInt("status");
        binding.setMain(vm_fragmentHome);
        view = binding.getRoot();
        ButterKnife.bind(this, view);
        return view;
    }//_____________________________________________________________________________________________ Start onCreateView


    @Override
    public void onStart() {//_______________________________________________________________________ Start onStart
        super.onStart();
        //Log.i("meri","onStart");
        init();
//        StartService();
        if (observer != null)
            observer.dispose();
        observer = null;
        ObserverObservableGpsAndNetworkChange();
        GetCurrentLocation();
        //getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }//_____________________________________________________________________________________________ End onStart


    private void ObserverObservableGpsAndNetworkChange() {//________________________________________ Start ObserverObservableGpsAndNetworkChange

        observer = new DisposableObserver<String>() {
            @Override
            public void onNext(String s) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (s) {
                            case "changeGPS":
                                if (TrafficController.getApplication(context).isLocationEnabled())
                                    imgLocation.setImageResource(R.drawable.ic_location_on);
                                else {
                                    imgLocation.setImageResource(R.drawable.ic_location_off);
                                }
                                break;
                            case "changeNetwork":
                                if (TrafficController.getApplication(context).isInternetConnected())
                                    imgInternet.setImageResource(R.drawable.ic_internet_on);
                                else {
                                    imgInternet.setImageResource(R.drawable.ic_internet_off);
                                }
                                break;
                            case "Error":
                                ProgressGif.setVisibility(View.GONE);
                                imgSend.setVisibility(View.VISIBLE);
                                ShowMessage(
                                        vm_fragmentHome.getMessageResponcse(),
                                        getResources().getColor(R.color.ML_White),
                                        getResources().getDrawable(R.drawable.ic_warning_red)
                                );
                                break;
                            case "Failure":
                                ProgressGif.setVisibility(View.GONE);
                                imgSend.setVisibility(View.VISIBLE);
                                ShowMessage(
                                        getResources().getString(R.string.onFailure),
                                        getResources().getColor(R.color.ML_White),
                                        getResources().getDrawable(R.drawable.ic_warning_red)
                                );
                                break;
                            case "send":
                                ProgressGif.setVisibility(View.GONE);
                                imgSend.setVisibility(View.VISIBLE);
                                LayoutSend.setVisibility(View.GONE);
                                ShowMessage(
                                        vm_fragmentHome.getMessageResponcse(),
                                        getResources().getColor(R.color.ML_White),
                                        getResources().getDrawable(R.drawable.ic_directions_walk)
                                );
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
        };

        if (ObservablesGpsAndNetworkChange != null) {
            ObservablesGpsAndNetworkChange
                    .observeOn(Schedulers.io())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer);
        }
    }//_____________________________________________________________________________________________ End ObserverObservableGpsAndNetworkChange


    private void init() {//_________________________________________________________________________ Start init

        LayoutSend.setVisibility(View.GONE);
        navController = Navigation.findNavController(view);

        if (status == 0) {
            imgSend.setImageResource(R.drawable.enter_locatoin);
            textViewSend.setText("ثبت ورود");
            LayoutSend.setBackground(getContext().getResources().getDrawable(R.drawable.button_bg));
        } else if (status == 1) {
            imgSend.setImageResource(R.drawable.exit_location);
            textViewSend.setText("ثبت خروج");
            LayoutSend.setBackground(getContext().getResources().getDrawable(R.drawable.button_red));
        } else {
            imgSend.setImageResource(R.drawable.enter_locatoin);
            textViewSend.setText("ثبت ورود");
            LayoutSend.setBackground(getContext().getResources().getDrawable(R.drawable.button_bg));
        }

        if (TrafficController.getApplication(context).isLocationEnabled())
            imgLocation.setImageResource(R.drawable.ic_location_on);
        else
            imgLocation.setImageResource(R.drawable.ic_location_off);

        if (TrafficController.getApplication(context).isInternetConnected())
            imgInternet.setImageResource(R.drawable.ic_internet_on);
        else
            imgInternet.setImageResource(R.drawable.ic_internet_off);

        SetClicks();

    }//_____________________________________________________________________________________________ End init


    private void SetClicks() {//____________________________________________________________________ Start SetClicks

        LayoutSend.setOnClickListener(v -> {
            ProgressGif.setVisibility(View.VISIBLE);
            imgSend.setVisibility(View.GONE);
            vm_fragmentHome.sendLocationToServer(currentLocation);
        });

    }//_____________________________________________________________________________________________ End SetClicks


    private void ShowMessage(String message, int color, Drawable icon) {//__________________________ Start ShowMessage
        DialogMessage dialogMessage = new DialogMessage(context, message, color, icon);
        dialogMessage.setCancelable(false);
        dialogMessage.show(getFragmentManager(), NotificationCompat.CATEGORY_PROGRESS);
    }//_____________________________________________________________________________________________ End ShowMessage


    private void ShowChart(ArrayList<ModelChartMeasureDistance> arrayList) {//______________________ Start ShowChart
        DialogChartMeasure measure = new DialogChartMeasure(context, arrayList);
        measure.setCancelable(false);
        measure.show(getFragmentManager(), NotificationCompat.CATEGORY_PROGRESS);
    }//_____________________________________________________________________________________________ End ShowChart



    @Override
    public void onDestroy() {//_____________________________________________________________________ Start onDestroy
        super.onDestroy();
        if (observer != null)
            observer.dispose();
        observer = null;
    }//_____________________________________________________________________________________________ End onDestroy





    private void GetCurrentLocation() {//___________________________________________________________ Start GetCurrentLocation

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

        } else {
            textViewWaiting.setVisibility(View.VISIBLE);
            TryToLocation++;
            if (TryToLocation > 3) {
                textViewWaiting.setVisibility(View.GONE);
                ShowMessage(
                        getResources().getString(R.string.DisconnectGPS),
                        getResources().getColor(R.color.ML_White),
                        getResources().getDrawable(R.drawable.ic_warning));

                return;
            }

            locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
            listener = new MyLocationListener();
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, (LocationListener) listener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, listener);

            Handler handler = new Handler();
            handler.postDelayed(() -> {
                locationManager.removeUpdates(listener);
                if (currentLocation == null) {
                    GetCurrentLocation();
                } else {
                    textViewWaiting.setVisibility(View.GONE);
                    LayoutSend.setVisibility(View.VISIBLE);
                    Log.i("meri", "lat : " + currentLocation.getLatitude() + " , lng : " + currentLocation.getLongitude());
                }
            }, 5 * 1000);
        }
    }//_____________________________________________________________________________________________ End GetCurrentLocation



    public class MyLocationListener implements LocationListener {//_________________________________ Start MyLocationListener

        public void onLocationChanged(final Location loc) {

            if (isBetterLocation(loc, previousBestLocation)) {
                currentLocation = loc;
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        public void onProviderDisabled(String provider) {

        }


        public void onProviderEnabled(String provider) {

        }
    }//_____________________________________________________________________________________________ End MyLocationListener



    protected boolean isBetterLocation(Location location, Location currentBestLocation) {//_________ Start isBetterLocation
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }//_____________________________________________________________________________________________ End isBetterLocation


    private boolean isSameProvider(String provider1, String provider2) {//__________________________ Start isSameProvider
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }//_____________________________________________________________________________________________ End isSameProvider


}
