package com.ngra.trafficcontroller.views.fragments.login;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.core.app.NotificationCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.ngra.trafficcontroller.R;
import com.ngra.trafficcontroller.databinding.FragmentLoginBinding;
import com.ngra.trafficcontroller.viewmodels.fragment.login.ViewModel_FragmentLogin;
import com.ngra.trafficcontroller.views.dialogs.DialogProgress;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentLogin extends Fragment {

    private Context context;
    private ViewModel_FragmentLogin viewModel;
    private View view;
    private boolean passwordVisible;
    private DialogProgress progress;

    @BindView(R.id.editPhoneNumber)
    EditText editPhoneNumber;

    @BindView(R.id.editPassword)
    EditText editPassword;

    @BindView(R.id.imgShowPassword)
    ImageView imgShowPassword;

    @BindView(R.id.btnLogin)
    Button btnLogin;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModel_FragmentLogin(context);
        FragmentLoginBinding binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_login, container, false
        );
        binding.setLogin(viewModel);
        view = binding.getRoot();
        ButterKnife.bind(this, view);
        return view;
    }//_____________________________________________________________________________________________ Start onCreateView


    public FragmentLogin(Context context) {//_______________________________________________________ Start FragmentLogin
        this.context = context;
    }//_____________________________________________________________________________________________ End FragmentLogin


    @Override
    public void onStart() {//_______________________________________________________________________ Start onStart
        super.onStart();
        init();
        SetClick();
    }//_____________________________________________________________________________________________ End onStart


    private void init() {//_________________________________________________________________________ Start init
        editPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordVisible = false;
    }//_____________________________________________________________________________________________ End init


    private void SetClick() {//_____________________________________________________________________ Start SetClick

//        ForgetPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ActivityBeforLogin.this, ActivitySendPhoneNumber.class);
//                intent.putExtra("type", "forget");
//                intent.putExtra("PhoneNumber", EditPhoneNumber.getText().toString());
//                intent.putExtra("Password", EditPassword.getText().toString());
//                startActivity(intent);
//            }
//        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CheckEmpty()) {
                    ShowProgressDialog();
                }
            }
        });


    }//_____________________________________________________________________________________________ End SetClick


    private Boolean CheckEmpty() {//________________________________________________________________ Start CheckEmpty

        boolean phone = false;


        if (editPhoneNumber.getText().length() != 11) {
            editPhoneNumber.setBackgroundResource(R.drawable.edit_empty_background);
            editPhoneNumber.setError(getResources().getString(R.string.EmptyPhoneNumber));
            editPhoneNumber.requestFocus();
            phone = false;
        } else {
            String ZeroNine = editPhoneNumber.getText().subSequence(0, 2).toString();
            if (ZeroNine.equalsIgnoreCase("09"))
                phone = true;
            else {
                editPhoneNumber.setBackgroundResource(R.drawable.edit_empty_background);
                editPhoneNumber.setError(getResources().getString(R.string.EmptyPhoneNumber));
                editPhoneNumber.requestFocus();
                phone = false;
            }
        }

        if (phone)
            return true;
        else
            return false;

    }//_____________________________________________________________________________________________ End CheckEmpty


    private void ShowProgressDialog() {//___________________________________________________________ Start ShowProgressDialog
        progress = new DialogProgress(context, null);
        progress.show(getFragmentManager(), NotificationCompat.CATEGORY_PROGRESS);
    }//_____________________________________________________________________________________________ End ShowProgressDialog


}
