package com.ngra.trafficcontroller.views.fragments.home;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

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

import com.ngra.trafficcontroller.R;
import com.ngra.trafficcontroller.database.DataBaseLog;
import com.ngra.trafficcontroller.databinding.FragmentHomeBinding;
import com.ngra.trafficcontroller.models.ModelChartMeasureDistance;
import com.ngra.trafficcontroller.backgroundservice.broadcasts.ReceiverLunchAppInBackground;
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
import io.realm.Realm;
import io.realm.RealmResults;

import static com.ngra.trafficcontroller.views.application.TrafficController.ObservablesGpsAndNetworkChange;


public class FragmentHome extends Fragment {

    private NavController navController;
    private View view;
    private Context context;
    private VM_FragmentHome vm_fragmentHome;
    private DisposableObserver<String> observer;

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

    @BindView(R.id.LogFile)
    LinearLayout LogFile;

    @BindView(R.id.list)
    ListView listview;

    @BindView(R.id.ShowMap)
    LinearLayout ShowMap;


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
        StartService();
        if (observer != null)
            observer.dispose();
        observer = null;
        ObserverObservableGpsAndNetworkChange();
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
                            case "LastGPS":
                                LastGPS.setText(vm_fragmentHome.GetLastGPS());
                                break;

                            case "LastNet":
                                LastNet.setText(vm_fragmentHome.GetLastNet());
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

        navController = Navigation.findNavController(view);
        CircleMenu.setVisibility(View.INVISIBLE);
        listview.setVisibility(View.INVISIBLE);

        ImgCircleMenu.setImageResource(R.drawable.ic_apps);

        if (TrafficController.getApplication(context).isLocationEnabled())
            imgLocation.setImageResource(R.drawable.ic_location_on);
        else
            imgLocation.setImageResource(R.drawable.ic_location_off);

        if (TrafficController.getApplication(context).isInternetConnected())
            imgInternet.setImageResource(R.drawable.ic_internet_on);
        else
            imgInternet.setImageResource(R.drawable.ic_internet_off);

        LastGPS.setText(vm_fragmentHome.GetLastGPS());
        LastNet.setText(vm_fragmentHome.GetLastNet());
        SetClicks();

    }//_____________________________________________________________________________________________ End init


    private void SetClicks() {//____________________________________________________________________ Start SetClicks

        CircleMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LayoutNetSetting.getVisibility() == View.VISIBLE) {
                    LayoutNetSetting
                            .startAnimation(AnimationUtils
                                    .loadAnimation(context, R.anim.slide_out_bottom));
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

        ShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (observer != null)
                    observer.dispose();
                observer = null;
                navController.navigate(R.id.action_fragmentHome_to_fragmentMap);
            }
        });

        LogFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Realm realm = TrafficController
                        .getApplication(context)
                        .getRealmComponent()
                        .getRealm();

                RealmResults<DataBaseLog> logs = realm.where(DataBaseLog.class).findAll();
                List<DataBaseLog> sub;
                if (logs.size() > 501)
                    sub = logs.subList(logs.size() - 500, logs.size());
                else
                    sub = logs;

                List<String> list = new ArrayList<>();
                for (int i = sub.size() - 1; i > -1; i--)
                    list.add(sub.get(i).getLogString());

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, list);
                listview.setAdapter(adapter);
                listview.setVisibility(View.VISIBLE);
            }
        });


        LayoutPrimary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listview.setVisibility(View.INVISIBLE);
                File file = new File(context.getFilesDir(), "config.txt");
                file.delete();

                if (LayoutNetSetting.getVisibility() == View.VISIBLE) {
                    LayoutNetSetting
                            .startAnimation(AnimationUtils
                                    .loadAnimation(context, R.anim.slide_out_bottom));
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
                        arrayList = vm_fragmentHome.getModelChartMeasureDistances();
                ShowChart(arrayList);
            }
        });


        LayoutMeasureDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Integer MD = vm_fragmentHome.getLocationsForMeasureDistance();
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


        CircleMenuCenter.setOnClickListener(v -> {
/*            if (LayoutNetSetting.getVisibility() == View.VISIBLE) {
                LayoutNetSetting
                        .startAnimation(AnimationUtils
                                .loadAnimation(context, R.anim.slide_out_bottom));
                LayoutNetSetting.setVisibility(View.INVISIBLE);
            }

            if (CircleMenu.getVisibility() == View.INVISIBLE) {
                CircleMenu.setVisibility(View.VISIBLE);
                ImgCircleMenu.setImageResource(R.drawable.ic_center_focus);
            } else {
                CircleMenu.setVisibility(View.INVISIBLE);
                ImgCircleMenu.setImageResource(R.drawable.ic_apps);
            }*/
        });


        ImgWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LayoutNetSetting.getVisibility() == View.VISIBLE) {
                    LayoutNetSetting
                            .startAnimation(AnimationUtils
                                    .loadAnimation(context, R.anim.slide_out_bottom));
                    LayoutNetSetting.setVisibility(View.INVISIBLE);
                }

                startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            }
        });


        ImgData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (LayoutNetSetting.getVisibility() == View.VISIBLE) {
                    LayoutNetSetting
                            .startAnimation(AnimationUtils
                                    .loadAnimation(context, R.anim.slide_out_bottom));
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

                if (TrafficController.getApplication(context).isInternetConnected())
                    return;

                if (LayoutNetSetting.getVisibility() == View.INVISIBLE) {
                    LayoutNetSetting
                            .startAnimation(AnimationUtils
                                    .loadAnimation(context, R.anim.slide_in_bottom));
                    LayoutNetSetting.setVisibility(View.VISIBLE);
                } else {
                    LayoutNetSetting
                            .startAnimation(AnimationUtils
                                    .loadAnimation(context, R.anim.slide_out_bottom));
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
                                    .loadAnimation(context, R.anim.slide_out_bottom));
                    LayoutNetSetting.setVisibility(View.INVISIBLE);
                }

                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
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


    private void StartService() {//_________________________________________________________________ Start StartService
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.sendBroadcast(new Intent(context, ReceiverLunchAppInBackground.class).setAction("ir.ngra.Lunch"));
                } else {
                    Intent i = new Intent("ir.ngra.Lunch");
                    context.sendBroadcast(i);
                }


            }
        }, 1000);
    }//_____________________________________________________________________________________________ End StartService


    @Override
    public void onDestroy() {//_____________________________________________________________________ Start onDestroy
        super.onDestroy();
        if (observer != null)
            observer.dispose();
        observer = null;
    }//_____________________________________________________________________________________________ End onDestroy


    private void SaveLog(String log) {//____________________________________________________________ Start SaveLog
        Realm realm = TrafficController
                .getApplication(context)
                .getRealmComponent()
                .getRealm();

        realm.beginTransaction();
        realm.createObject(DataBaseLog.class)
                .insert(log);
        realm.commitTransaction();
    }//_____________________________________________________________________________________________ End SaveLog


    public String getStringCurrentDate() {//_______________________________________________________ Start getStringCurrentDate
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        return simpleDateFormat.format(new Date());
    }//_____________________________________________________________________________________________ End getStringCurrentDate


}
