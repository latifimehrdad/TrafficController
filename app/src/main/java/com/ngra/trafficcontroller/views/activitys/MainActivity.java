package com.ngra.trafficcontroller.views.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ngra.trafficcontroller.R;
import com.ngra.trafficcontroller.databinding.ActivityMainBinding;
import com.ngra.trafficcontroller.viewmodels.activitys.ViewModel_MainActivity;

import butterknife.ButterKnife;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class MainActivity extends AppCompatActivity {

    private ViewModel_MainActivity viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {//__________________________________________ Start onCreate
        super.onCreate(savedInstanceState);
        OnBindView();
    }//_____________________________________________________________________________________________ End onCreate


    private void OnBindView() {//___________________________________________________________________ Start OnBindView
        viewModel = new ViewModel_MainActivity(this);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setMain(viewModel);
        ButterKnife.bind(this);
        //ShowLoginActivity();
    }//_____________________________________________________________________________________________ End OnBindView




    private void ShowLoginActivity() {//____________________________________________________________ Start ShowLoginActivity
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }//_____________________________________________________________________________________________ End ShowLoginActivity



    public void attachBaseContext(Context newBase) {//______________________________________________ Start attachBaseContext
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }//_____________________________________________________________________________________________ End attachBaseContext


}
