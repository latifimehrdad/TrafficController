package com.ngra.trafficcontroller.views.fragments.home;


import android.content.Context;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.ngra.trafficcontroller.R;
import com.ngra.trafficcontroller.databinding.FragmentMapBinding;
import com.ngra.trafficcontroller.utility.MehrdadLatifiMap;
import com.ngra.trafficcontroller.viewmodels.fragment.home.VM_FragmentMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMap extends Fragment implements OnMapReadyCallback {

    private View view;
    private Context context;
    private VM_FragmentMap vm_fragmentMap;
    private GoogleMap mMap;
//    private DisposableObserver<String> observer;


    @BindView(R.id.imgBack)
    ImageView imgBack;

    @BindView(R.id.imgCenter)
    ImageView imgCenter;


    public FragmentMap() {//________________________________________________________________________ Start FragmentMap

    }//_____________________________________________________________________________________________ End FragmentMap


    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {//__________________________________________________________ Start onCreateView
        this.context = getContext();
        FragmentMapBinding binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_map, container, false
        );
        vm_fragmentMap = new VM_FragmentMap(context);
        binding.setMap(vm_fragmentMap);
        view = binding.getRoot();
        ButterKnife.bind(this, view);
        return view;
    }//_____________________________________________________________________________________________ Start onCreateView


    @Override
    public void onStart() {//_______________________________________________________________________ Start onStart
        super.onStart();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.fpraMap);
        mapFragment.getMapAsync(this);
//        ObserverObservableGpsAndNetworkChange();
        SetOnClick();
    }//_____________________________________________________________________________________________ End onStart


    private void SetOnClick() {//___________________________________________________________________ Start SetOnClick

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        imgCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MehrdadLatifiMap mehrdadLatifiMap = new MehrdadLatifiMap();
                mehrdadLatifiMap.setGoogleMap(mMap);
                LatLng negra = mMap.getCameraPosition().target;

                boolean in = mehrdadLatifiMap.MlMap_isInside(negra, vm_fragmentMap.GetWorkingRange());
                if (in) {
                    Toast.makeText(context, "IN", Toast.LENGTH_SHORT).show();
                    mehrdadLatifiMap.AddMarker(negra, "null", "null", R.drawable.ic_location_on, 0);
                } else {
                    Toast.makeText(context, "OUT", Toast.LENGTH_SHORT).show();
                    mehrdadLatifiMap.AddMarker(negra, "null", "null", R.drawable.ic_location_off, 0);
                }
            }
        });

    }//_____________________________________________________________________________________________ End SetOnClick


    @Override
    public void onMapReady(GoogleMap googleMap) {//_________________________________________________ Start Void onMapReady
        mMap = googleMap;
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.setMyLocationEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
//        DrawPolygon();

    }//_____________________________________________________________________________________________ End Void onMapReady


    private void DrawPolygon() {//__________________________________________________________________ Start DrawPolygon

//        MehrdadLatifiMap mehrdadLatifiMap = new MehrdadLatifiMap();
//        mehrdadLatifiMap.setGoogleMap(mMap);
//        mehrdadLatifiMap.setML_Stroke_Width(1.0f);
//        mehrdadLatifiMap.setML_Stroke_Color(getResources().getColor(R.color.colorPrimary));
//        mehrdadLatifiMap.setML_Fill_Color(getResources().getColor(R.color.ML_MapPolyGon));
//        mehrdadLatifiMap.setML_LatLongs(vm_fragmentMap.GetWorkingRange());
//        mehrdadLatifiMap.DrawPolygon(false);
//        mehrdadLatifiMap.AutoZoom();
//
//        mehrdadLatifiMap.setML_Stroke_Width(2.0f);
//        mehrdadLatifiMap.setML_Stroke_Color(getResources().getColor(R.color.ML_Button));
//        mehrdadLatifiMap.setML_LatLongs(vm_fragmentMap.GetWorking());
//        mehrdadLatifiMap.DrawPolyLinesWithArrow();


    }//_____________________________________________________________________________________________ End DrawPolygon


//    private void ObserverObservableGpsAndNetworkChange() {//________________________________________ Start ObserverObservableGpsAndNetworkChange
//
//        observer = new DisposableObserver<String>() {
//            @Override
//            public void onNext(String s) {
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mMap.clear();
//                        switch (s) {
//                            case "Getgps":
//                                if(ReceiverJobInBackground.LastLocation != null){
//                                    MehrdadLatifiMap mehrdadLatifiMap = new MehrdadLatifiMap();
//                                    mehrdadLatifiMap.setGoogleMap(mMap);
//                                    mehrdadLatifiMap.AddMarker(ReceiverJobInBackground.LastLocation,
//                                            "null",
//                                            "null",
//                                            R.drawable.getgps,
//                                            0);
//
//                                }
//
//                                break;
//
//                            case "Getnetwork":
//                                if(ReceiverJobInBackground.LastLocation != null){
//                                    MehrdadLatifiMap mehrdadLatifiMap = new MehrdadLatifiMap();
//                                    mehrdadLatifiMap.setGoogleMap(mMap);
//                                    mehrdadLatifiMap.AddMarker(ReceiverJobInBackground.LastLocation,
//                                            "null",
//                                            "null",
//                                            R.drawable.getnetwork,
//                                            0);
//                                }
//                                break;
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        };
//
//        if (ObservablesGpsAndNetworkChange != null) {
//            ObservablesGpsAndNetworkChange
//                    .observeOn(Schedulers.io())
//                    .subscribeOn(AndroidSchedulers.mainThread())
//                    .subscribe(observer);
//        }
//    }//_____________________________________________________________________________________________ End ObserverObservableGpsAndNetworkChange


    @Override
    public void onDestroy() {//_____________________________________________________________________ Start onDestroy
        super.onDestroy();
//        observer.dispose();
    }//_____________________________________________________________________________________________ End onDestroy
}
