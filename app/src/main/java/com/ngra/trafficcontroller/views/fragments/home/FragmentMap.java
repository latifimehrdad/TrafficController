package com.ngra.trafficcontroller.views.fragments.home;


import android.content.Context;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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


    @BindView(R.id.imgBack)
    ImageView imgBack;


    public FragmentMap() {//________________________________________________________________________ Start FragmentMap

    }//_____________________________________________________________________________________________ End FragmentMap


    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {//__________________________________________________________ Start onCreateView
        this.context = getContext();
        FragmentMapBinding binding = DataBindingUtil.inflate(
                inflater,R.layout.fragment_map,container,false
        );
        vm_fragmentMap = new VM_FragmentMap(context);
        binding.setMap(vm_fragmentMap);
        view = binding.getRoot();
        ButterKnife.bind(this,view);
        return view;
    }//_____________________________________________________________________________________________ Start onCreateView



    @Override
    public void onStart() {//_______________________________________________________________________ Start onStart
        super.onStart();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.fpraMap);
        mapFragment.getMapAsync(this);

        SetOnClick();
    }//_____________________________________________________________________________________________ End onStart




    private void SetOnClick() {//___________________________________________________________________ Start SetOnClick

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
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
        DrawPolygon();

    }//_____________________________________________________________________________________________ End Void onMapReady




    private void DrawPolygon() {//__________________________________________________________________ Start DrawPolygon

        MehrdadLatifiMap mehrdadLatifiMap = new MehrdadLatifiMap();
        mehrdadLatifiMap.setGoogleMap(mMap);
        mehrdadLatifiMap.setML_Stroke_Width(1.0f);
        mehrdadLatifiMap.setML_Stroke_Color(getResources().getColor(R.color.colorPrimary));
        mehrdadLatifiMap.setML_Fill_Color(getResources().getColor(R.color.ML_MapPolyGon));
        mehrdadLatifiMap.setML_LatLongs(vm_fragmentMap.GetWorkingRange());
        mehrdadLatifiMap.DrawPolygon(false);
        mehrdadLatifiMap.AutoZoom();

        mehrdadLatifiMap.setML_Stroke_Width(2.0f);
        mehrdadLatifiMap.setML_Stroke_Color(getResources().getColor(R.color.ML_Button));
        mehrdadLatifiMap.setML_LatLongs(vm_fragmentMap.GetWorking());
        mehrdadLatifiMap.DrawPolyLinesWithArrow();

    }//_____________________________________________________________________________________________ End DrawPolygon


}
