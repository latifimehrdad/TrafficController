package com.ngra.trafficcontroller.views.fragments.login;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.cunoraz.gifview.library.GifView;
import com.ngra.trafficcontroller.R;
import com.ngra.trafficcontroller.databinding.FragmentLoginBinding;
import com.ngra.trafficcontroller.viewmodels.fragment.login.ViewModel_FragmentLogin;
import com.ngra.trafficcontroller.views.activitys.LoginActivity;
import com.ngra.trafficcontroller.views.dialogs.DialogMessage;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static com.ngra.trafficcontroller.utility.StaticFunctions.TextChangeForChangeBack;

public class FragmentLogin extends Fragment {

    private Context context;
    private ViewModel_FragmentLogin viewModel;
    private View view;
    private String PhoneNumber;
    private LoginActivity loginActivity;
    private DisposableObserver<String> observer;

    @BindView(R.id.editPhoneNumber)
    EditText editPhoneNumber;

    @BindView(R.id.editPassword)
    EditText editPassword;

    @BindView(R.id.imgShowPassword)
    ImageView imgShowPassword;

    @BindView(R.id.BtnLogin)
    RelativeLayout BtnLogin;

    @BindView(R.id.ProgressGif)
    GifView ProgressGif;

    @BindView(R.id.BtnLoginText)
    TextView BtnLoginText;


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


    public FragmentLogin(Context context, LoginActivity loginActivity) {//__________________________ Start FragmentLogin
        this.context = context;
        this.loginActivity = loginActivity;
    }//_____________________________________________________________________________________________ End FragmentLogin


    @Override
    public void onStart() {//_______________________________________________________________________ Start onStart
        super.onStart();
        SetClick();
        SetTextWatcher();
        ObserverObservable();
        DismissProgress();
    }//_____________________________________________________________________________________________ End onStart


    private void SetClick() {//_____________________________________________________________________ Start SetClick

        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CheckEmpty()) {
                    ShowProgress();
                    viewModel.SendNumber(PhoneNumber);
                }
            }
        });


    }//_____________________________________________________________________________________________ End SetClick


    private void ObserverObservable() {//___________________________________________________________ Start ObserverObservable

        observer = new DisposableObserver<String>() {
            @Override
            public void onNext(String s) {
                getActivity()
                        .runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DismissProgress();
                                switch (s) {
                                    case "Done":
                                        loginActivity.getObservables().onNext(PhoneNumber);
                                        break;
                                    case "Failed":
                                        ShowMessage(
                                                viewModel.getMessageResult(),
                                                getResources().getColor(R.color.ML_White),
                                                getResources().getDrawable(R.drawable.ic_warning_red)
                                        );
                                        break;
                                    case "onFailure":
                                        ShowMessage(
                                                getResources().getString(R.string.onFailure),
                                                getResources().getColor(R.color.ML_White),
                                                getResources().getDrawable(R.drawable.ic_warning_red)
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


        viewModel
                .getObservables()
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

    }//_____________________________________________________________________________________________ End ObserverObservable


    private Boolean CheckEmpty() {//________________________________________________________________ Start CheckEmpty

        boolean phone = false;
        PhoneNumber = editPhoneNumber.getText().toString();


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


    private void ShowProgress() {//_________________________________________________________________ Start ShowProgress
        BtnLoginText.setVisibility(View.INVISIBLE);
        ProgressGif.setVisibility(View.VISIBLE);
    }//_____________________________________________________________________________________________ End ShowProgress



    private void DismissProgress() {//______________________________________________________________ Start DismissProgress
        BtnLoginText.setVisibility(View.VISIBLE);
        ProgressGif.setVisibility(View.INVISIBLE);
    }//_____________________________________________________________________________________________ End DismissProgress


    private void SetTextWatcher() {//_______________________________________________________________ Start SetTextWatcher
        editPhoneNumber.addTextChangedListener(TextChangeForChangeBack(editPhoneNumber));
    }//_____________________________________________________________________________________________ End SetTextWatcher


    private void ShowMessage(String message, int color, Drawable icon) {//__________________________ Start ShowMessage

        DialogMessage dialogMessage = new DialogMessage(context, message, color, icon);
        dialogMessage.setCancelable(false);
        dialogMessage.show(getFragmentManager(), NotificationCompat.CATEGORY_PROGRESS);

    }//_____________________________________________________________________________________________ End ShowMessage


    @Override
    public void onDestroy() {//_____________________________________________________________________ Start onDestroy
        super.onDestroy();
        if(observer != null)
            observer.dispose();
    }//_____________________________________________________________________________________________ End onDestroy


}
