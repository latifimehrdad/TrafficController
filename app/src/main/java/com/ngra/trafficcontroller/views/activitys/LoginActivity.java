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

import butterknife.ButterKnife;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class LoginActivity extends AppCompatActivity {

    private ViewModel_LoginActivity viewModel;
    private FragmentManager fm;
    private FragmentTransaction ft;


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
        ShowFragmentLogin();
    }//_____________________________________________________________________________________________ End OnBindView



    public void attachBaseContext(Context newBase) {//______________________________________________ Start attachBaseContext
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }//_____________________________________________________________________________________________ End attachBaseContext



    private void ShowFragmentLogin(){//_____________________________________________________________ Start ShowFragmentLogin
        fm = null;
        ft = null;
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        FragmentLogin login = new FragmentLogin(this);
        ft.replace(R.id.loginFragment, login);
        ft.commit();
    }//_____________________________________________________________________________________________ End ShowFragmentLogin

}
