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

import com.cunoraz.gifview.library.GifView;
import com.ngra.trafficcontroller.R;
import com.ngra.trafficcontroller.databinding.FragmentLoginBinding;
import com.ngra.trafficcontroller.viewmodels.fragment.login.ViewModel_FragmentLogin;
import com.ngra.trafficcontroller.views.dialogs.DialogProgress;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.subjects.PublishSubject;

import static com.ngra.trafficcontroller.utility.StaticFunctions.TextChangeForChangeBack;

public class FragmentLogin extends Fragment {

    private Context context;
    private ViewModel_FragmentLogin viewModel;
    private View view;
//    private DialogProgress progress;
    private PublishSubject<String> ActivityObservables;

    @BindView(R.id.editPhoneNumber)
    EditText editPhoneNumber;

    @BindView(R.id.editPassword)
    EditText editPassword;

    @BindView(R.id.imgShowPassword)
    ImageView imgShowPassword;

    @BindView(R.id.btnLogin)
    Button btnLogin;

    @BindView(R.id.gifWatting)
    GifView gifWatting;


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


    public FragmentLogin(Context context, PublishSubject<String> ActivityObservables) {//___________ Start FragmentLogin
        this.context = context;
        this.ActivityObservables = ActivityObservables;
    }//_____________________________________________________________________________________________ End FragmentLogin


    @Override
    public void onStart() {//_______________________________________________________________________ Start onStart
        super.onStart();
        SetClick();
        SetTextWatcher();
    }//_____________________________________________________________________________________________ End onStart


    private void SetClick() {//_____________________________________________________________________ Start SetClick

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CheckEmpty()) {
                    ActivityObservables.onNext("verify");
                    //ShowProgressDialog();
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
        btnLogin.setText("");
        gifWatting.setVisibility(View.VISIBLE);
//        progress = new DialogProgress(context, null);
//        progress.show(getFragmentManager(), NotificationCompat.CATEGORY_PROGRESS);
    }//_____________________________________________________________________________________________ End ShowProgressDialog



    private void SetTextWatcher() {//_______________________________________________________________ Start SetTextWatcher
        editPhoneNumber.addTextChangedListener(TextChangeForChangeBack(editPhoneNumber));
    }//_____________________________________________________________________________________________ End SetTextWatcher

}
